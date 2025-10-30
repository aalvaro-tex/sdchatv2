/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.chat;

import com.aalvarotex.sd.sdchatv2.entities.Chat;
import com.aalvarotex.sd.sdchatv2.json.ChatWriter;
import com.aalvarotex.sd.sdchatv2.login.LoginBackingBean;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
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

    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());

    private static final Set<String> mensajesPersistidos
            = java.util.concurrent.ConcurrentHashMap.newKeySet();

    @Inject
    LoginBackingBean loginBean;

    private Client client;
    private WebTarget target;

    /**
     *
     */
    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();

    }

    /**
     *
     */
    @PreDestroy
    public void destroy() {
        client.close();
    }

    /**
     *
     * @param peer
     * @param room
     */
    @OnOpen
    public void onOpen(Session peer, @PathParam("room") String room) {
        //System.out.println(room);
        peers.add(peer);
    }

    /**
     *
     * @param peer
     */
    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
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
            this.saveMessage(idEmisor, idReceptor, idConversacion, mensaje);

            // después, lo enviamos
            try {
                peer.getBasicRemote().sendText(message);
            } catch (IOException ex) {
                System.out.println("Excepcion");
            }
        }
    }

    private void saveMessage(Long idEmisor, Long idReceptor, String idConversacion, String message) {
        String key = idEmisor + "|" + idReceptor + "|" + idConversacion + "|" + message.hashCode();
        // primero determinamos quién es el emisor y quién el receptor
        // si el usuario logueado es un refugio, su id es el número después de los dos puntos
        // fecha y hora actuales
        if (mensajesPersistidos.add(key)) {
            LocalDateTime hoy = LocalDateTime.now(ZoneId.of("Europe/Madrid"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String timestamp = hoy.format(formatter);

            // construimos el objeto chat para guardarlo en la BD
            Chat c = new Chat();
            c.setIdConversacion(idConversacion);
            c.setIdEmisor(idEmisor);
            c.setIdReceptor(idReceptor);
            c.setMensaje(message);
            c.setTimestamp(timestamp);

            // lo guardamos en la BD
            // se guarda una vez por cada cliente distinto usando la sala (max 2)
            // si el numero de peers es 2, debemos guardar y borrar el último mensaje con el id de la conversación 
            // (será el mensaje repetido)
            target = client
                    .target(base()).path("com.aalvarotex.sd.sdchatv2.entities.chat");

            // guardamos sin borrar
            Response response = target.register(ChatWriter.class)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(c, MediaType.APPLICATION_JSON));
            System.out.println("Añadir mensaje: " + response);
        } else {
        }
    }

    private String base() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ctx = req.getContextPath();        // p.ej. "/sdChatv2" o "/sdchat"
        int port = req.getLocalPort();          // normalmente 8080 en la Pi
        return "http://localhost:" + port + ctx + "/webresources";
    }
}
