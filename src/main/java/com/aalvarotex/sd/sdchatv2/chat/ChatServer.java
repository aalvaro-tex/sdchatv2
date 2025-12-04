/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.chat;

import com.aalvarotex.sd.sdchatv2.entities.Chat;
import com.aalvarotex.sd.sdchatv2.json.ChatWriter;
import com.aalvarotex.sd.sdchatv2.login.LoginBackingBean;
import com.aalvarotex.sd.sdchatv2.utils.Constantes;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import javax.websocket.EncodeException;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author alvar
 */
@ServerEndpoint("/websocket/{room}")
public class ChatServer {

    public static class Config extends ServerEndpointConfig.Configurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest req, HandshakeResponse resp) {
            // guardamos headers y requestURI para usarlos en onOpen
            sec.getUserProperties().put("headers", req.getHeaders());
            sec.getUserProperties().put("requestURI", req.getRequestURI());
        }
    }

    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());
    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    private final Map<String, Long> dedupe = new ConcurrentHashMap<>();

    @Inject
    LoginBackingBean loginBean;

    private Client client;
    private WebTarget target;
    private String baseUri;

    /**
     *
     * @param peer
     * @param room
     */
    @OnOpen
    public void onOpen(Session peer, @PathParam("room") String room) {
        //System.out.println(room);
        peers.add(peer);
        URI u = peer.getRequestURI(); // puede ser relativa en algunos contenedores
        String scheme = "http";
        String host = null;
        int port = -1;
        String path = u != null ? u.getPath() : null;

        if (u != null && u.isAbsolute()) {
            scheme = "wss".equalsIgnoreCase(u.getScheme()) ? "https" : "http";
            host = u.getHost();
            port = u.getPort();
        }

        // 2) Si no hay host, miramos el header Host de la handshake
        if (host == null || host.isEmpty()) {
            @SuppressWarnings("unchecked")
            Map<String, List<String>> headers = (Map<String, List<String>>) peer.getUserProperties().get("headers");
            if (headers != null) {
                List<String> hostHeader = headers.get("Host");
                if (hostHeader != null && !hostHeader.isEmpty()) {
                    String h = hostHeader.get(0);
                    int idx = h.indexOf(':');
                    if (idx > 0) {
                        host = h.substring(0, idx);
                        try {
                            port = Integer.parseInt(h.substring(idx + 1));
                        } catch (NumberFormatException ignore) {
                        }
                    } else {
                        host = h;
                    }
                }
                List<String> proto = headers.get("X-Forwarded-Proto");
                if (proto != null && !proto.isEmpty()) {
                    scheme = proto.get(0);
                }
            }
        }

        // 3) Derivamos el contextPath desde la ruta del WS
        String ctx = "";
        if (path != null) {
            int cut = path.indexOf("/websocket/");
            if (cut > 0) {
                ctx = path.substring(0, cut); // ej. "/sdChatv2"
            }
        }

        if (host == null || host.isEmpty()) {
            host = "localhost";
        }
        if (port == -1) {
            port = "https".equalsIgnoreCase(scheme) ? 443 : 80;
        }

        baseUri = scheme + "://" + host + ((port == 80 && scheme.equals("http")) || (port == 443 && scheme.equals("https")) ? "" : ":" + port)
                + ctx + "/webresources";

        logger.log(Level.INFO, "Base REST URI: {0}", baseUri);

        // Preparamos el target específico de Chat (reutilizable)
        target = client.target(baseUri).path("com.aalvarotex.sd.sdchatv2.entities.chat");
    }

    /**
     *
     * @param peer
     */
    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
        if (peers.isEmpty() && client != null) {
            client.close();
            client = null;
        }
    }

    /**
     *
     * @param message
     * @param client
     * @throws IOException
     * @throws EncodeException
     */
    @OnMessage
    public void message(String message, Session client) {
        for (Session peer : peers) {
            System.out.println(message);
            String datos = message.split("_")[0];
            String idConversacion = datos.split("@")[1];
            Long idEmisor = Long.parseLong(datos.split("@")[0]);
            Long idReceptor = -1L;
            // el receptor lo obtenemos como la parte del id de la conversacion que no es el id del emisor
            if (idEmisor == Long.parseLong(idConversacion.split("-")[0])) {
                idReceptor = Long.parseLong(idConversacion.split("-")[1]);
            } else {
                idReceptor = Long.parseLong(idConversacion.split("-")[0]);
            }
            String mensaje = message.split("_")[1];
            System.out.println("Guardo un mensaje");
            this.saveMessage(client, idEmisor, idReceptor, idConversacion, mensaje);

            // después, lo enviamos
            try {
                peer.getBasicRemote().sendText(message);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, message);
            }
        }
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildKey(Long idEmisor, Long idReceptor, String idConversacion, String message) {
        long a = Math.min(idEmisor, idReceptor);
        long b = Math.max(idEmisor, idReceptor);
        String msgHash = sha256(message);
        System.out.println(msgHash);
        return idConversacion + "|" + a + "|" + b + "|" + msgHash;
    }

    private boolean tryAcquireOnce(String key) {
        long now = System.currentTimeMillis();
        // purga simple por tiempo
        dedupe.entrySet().removeIf(e -> now - e.getValue() > Constantes.DEDUPE_TTL_MS);
        return dedupe.putIfAbsent(key, now) == null; // true si no existía
    }

    private WebTarget ensureChatTarget(Session session) {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = ClientBuilder.newClient();
                }
            }
        }
        if (target == null) {
            synchronized (this) {
                if (target == null) {
                    URI u = session.getRequestURI(); // ej: ws://host:8080/context/websocket/room
                    String scheme = (u != null && "wss".equalsIgnoreCase(u.getScheme())) ? "https" : "http";
                    String host = (u != null) ? u.getHost() : null;
                    int port = (u != null) ? u.getPort() : -1;
                    String path = (u != null) ? u.getPath() : "";

                    // Fallback si el contenedor no da URI absoluta
                    if (host == null || host.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = (Map<String, List<String>>) session.getUserProperties().get("headers");
                        if (headers != null) {
                            List<String> hostHeader = headers.get("Host");
                            if (hostHeader != null && !hostHeader.isEmpty()) {
                                String h = hostHeader.get(0);
                                int idx = h.indexOf(':');
                                if (idx > 0) {
                                    host = h.substring(0, idx);
                                    try {
                                        port = Integer.parseInt(h.substring(idx + 1));
                                    } catch (Exception ignore) {
                                    }
                                } else {
                                    host = h;
                                }
                            }
                            List<String> xfProto = headers.get("X-Forwarded-Proto");
                            if (xfProto != null && !xfProto.isEmpty()) {
                                scheme = xfProto.get(0);
                            }
                        }
                    }

                    String ctx = "";
                    int cut = path.indexOf("/websocket/");
                    if (cut > 0) {
                        ctx = path.substring(0, cut);
                    }

                    if (host == null || host.isEmpty()) {
                        host = "localhost";
                    }
                    if (port < 0) {
                        port = "https".equalsIgnoreCase(scheme) ? 443 : 80;
                    }
                    String portPart = ((port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme))) ? "" : (":" + port);

                    baseUri = scheme + "://" + host + portPart + ctx + "/webresources";
                    logger.log(Level.INFO, "Base REST URI: {0}", baseUri);

                    target = client.target(baseUri)
                            .path("com.aalvarotex.sd.sdchatv2.entities.chat");
                }
            }
        }
        return target;
    }

    public void saveMessage(Session session, Long idEmisor, Long idReceptor, String idConversacion, String message) {
        String key = buildKey(idEmisor, idReceptor, idConversacion, message);

        if (tryAcquireOnce(key)) {
            // --- aquí SÍ guardamos ---
            LocalDateTime hoy = LocalDateTime.now(ZoneId.of("Europe/Madrid"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String timestamp = hoy.format(formatter);

            Chat c = new Chat();
            c.setIdConversacion(idConversacion);
            c.setIdEmisor(idEmisor);
            c.setIdReceptor(idReceptor);
            c.setMensaje(message);
            c.setTimestamp(timestamp);

            WebTarget wtarget = ensureChatTarget(session);

            logger.log(Level.INFO, "POST {0}", wtarget.getUri());

            Response response = wtarget.register(ChatWriter.class)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(c, MediaType.APPLICATION_JSON));

            logger.log(Level.INFO, "Añadir mensaje -> HTTP {0}", response.getStatus());
        } else {
            System.out.println("Duplicado detectado. No lo guardo");
        }
    }
}
