/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.chat;

import com.aalvarotex.sd.sdchatv2.dto.UsuarioDTO;
import com.aalvarotex.sd.sdchatv2.entities.Chat;
import com.aalvarotex.sd.sdchatv2.entities.Usuario;
import com.aalvarotex.sd.sdchatv2.entities.UsuarioDetalles;
import com.aalvarotex.sd.sdchatv2.json.ChatReader;
import com.aalvarotex.sd.sdchatv2.json.ChatWriter;
import com.aalvarotex.sd.sdchatv2.json.UsuarioDetallesWriter;
import com.aalvarotex.sd.sdchatv2.json.UsuarioReader;
import com.aalvarotex.sd.sdchatv2.json.UsuarioWriter;
import com.aalvarotex.sd.sdchatv2.utils.Constantes;
import com.aalvarotex.sd.sdchatv2.utils.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author alvar
 */
@Named
@RequestScoped
public class ChatClientBean {

    private static final Logger logger = Logger.getLogger(ChatClientBean.class.getName());
    Client client;
    WebTarget target;

    @Inject
    ChatBackingBean bean;

    @Inject
    StringUtils stringUtils;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
    }

    // Método para encontrar al usuario con el que se quiere hablar
    public String startConversacion() {

        String success = "failure";
        String username = bean.getNewChatUsername();

        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.usuario");
        try {

            Response r = target.register(UsuarioReader.class)
                    .path("find-by-username/{username}")
                    .resolveTemplate("username", username)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (r.getStatus() == 200) {
                // si el usuario existe, iniciamos una nueva conversación con él
                // primero debemos comprobar si ya tenemos una conversación iniciada
                // para ello buscamos si el posible id de la conversación (o su opuesto) ya existe
                Usuario u = r.readEntity(Usuario.class);
                if (Objects.equals(u.getId(), bean.getIdUserLogeado())) {
                    bean.showError("¡No puedes hablar contigo mismo!");
                } else {
                    String idConversacion = bean.getIdUserLogeado() + "-" + u.getId();
                    System.out.println("id de la conversación: " + idConversacion);
                    target = client
                            .target(base())
                            .path("com.aalvarotex.sd.sdchatv2.entities.chat");
                    r = target.register(ChatReader.class)
                            .path("exists/{idConversacion}")
                            .resolveTemplate("idConversacion", idConversacion)
                            .request()
                            .get();

                    if (r.readEntity(Boolean.class) == true) {
                        boolean existe = true;
                        bean.setIdConversacionSelectedRefresh(idConversacion);
                        // aquí la conversación existe
                        success = "success";
                    } else {
                        // aquí debemos probar con el id opuesto
                        idConversacion = u.getId() + "-" + bean.getIdUserLogeado();
                        target = client
                                .target(base())
                                .path("com.aalvarotex.sd.sdchatv2.entities.chat");
                        r = target.register(ChatReader.class)
                                .path("exists/{idConversacion}")
                                .resolveTemplate("idConversacion", idConversacion)
                                .request()
                                .get();

                        if (r.readEntity(Boolean.class)) {
                            boolean existe = true;
                            // aquí la conversación existe
                            bean.setIdConversacionSelectedRefresh(idConversacion);
                            success = "success";
                        } else {
                            // aquí no existe la conversación
                            System.out.println("La conversación no existe");
                            bean.setIdConversacionSelectedRefresh(idConversacion);
                            success = "success";
                        }
                    }
                }
            } else {
                bean.showError("El usuario no existe");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return success;
    }

    /**
     *
     * @return
     */
    public List<Chat> getChatsByUser() {
        logger.info("Entro");
        System.out.println("Recuperando chats por usuario: " + bean.getIdUserLogeado());
        List<Chat> chats = new ArrayList<>();
        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.chat");

        Response response = target.register(ChatWriter.class)
                .path("my-chats/{idUsuario}")
                .resolveTemplate("idUsuario", bean.getIdUserLogeado())
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatus() == 200) {
            chats = response.readEntity(List.class);
        }
        if (chats == null) {
            System.out.println("Chats es nulo");
        }
        return chats;
    }

    // Determina el nombre del receptor desde el punto de vista del usuario logeado
    // Utiliza el id de la conversación para ello
    public String getNombreReceptor(String idConversacion) {
        String nombre = "";
        // del id de la conversación obtenemos dos ids
        // basta comparar ambos con el id del usuario logeado
        // el que no coincida es el id del receptor
        Long id1 = Long.parseLong(idConversacion.split("-")[0]);
        Long id2 = Long.parseLong(idConversacion.split("-")[1]);
        Long idReceptor = -1L;
        if (bean.getIdUserLogeado() == id1) {
            idReceptor = id2;
        } else {
            idReceptor = id1;
        }
        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.usuario");
        Response r = target.register(UsuarioReader.class)
                .path("{id}")
                .resolveTemplate("id", idReceptor)
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (r.getStatus() == 200) {
            Usuario u = r.readEntity(Usuario.class);
            nombre = u.getNombreUsuario();
        }
        return nombre;
    }

    public char getFirstLetterReceptor(String idConversacion) {
        return this.getNombreReceptor(idConversacion).charAt(0);
    }

    /**
     *
     * @param idConversacion
     * @return
     */
    public List<Chat> getLatestMessageByUser(String idConversacion) {
        List<Chat> chats = null;
        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.chat");
        Response response = target.register(ChatWriter.class)
                .path("last-message/{idConversacion}")
                .resolveTemplate("idConversacion", idConversacion)
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatus() == 200) {
            chats = response.readEntity(List.class);
            Collections.reverse(chats);
        }
        return chats;
    }

    public UsuarioDTO getUsuarioById() {
        Usuario u = new Usuario();
        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.usuario");
        Response response = target.register(UsuarioWriter.class)
                .path("{id}")
                .resolveTemplate("id", bean.getIdUserLogeado())
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatus() == 200) {
            u = response.readEntity(Usuario.class);
        }
        UsuarioDetalles ud = new UsuarioDetalles();
        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.usuariodetalles");
        response = target.register(UsuarioDetallesWriter.class)
                .path("{id}")
                .resolveTemplate("id", bean.getIdUserLogeado())
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatus() == 200) {
            ud = response.readEntity(UsuarioDetalles.class);
        }
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(u.getId());
        if (ud.getTema().equalsIgnoreCase("NE")) {
            dto.setColorPref(Constantes.COLOR_PREFERENTE_DEFECTO);
        } else {
            dto.setColorPref(ud.getTema());
        }
        if (ud.getFotoPerfil().equalsIgnoreCase("NE")) {
            dto.setFotoPerfil(Constantes.FOTO_PERFIL_DEFECTO);
        } else {
            dto.setFotoPerfil(ud.getFotoPerfil());
        }
        dto.setNombreUsuario(u.getNombreUsuario());
        return dto;
    }

    // devuelve la imagen de usuario dado id de la conversacion, en base64
    public String getUserImgById(String idConversacion) {
        Long id1 = Long.parseLong(idConversacion.split("-")[0]);
        Long id2 = Long.parseLong(idConversacion.split("-")[1]);
        Long idReceptor = -1L;
        if (Objects.equals(bean.getIdUserLogeado(), id1)) {
            idReceptor = id2;
        } else {
            idReceptor = id1;
        }
        UsuarioDetalles ud = new UsuarioDetalles();
        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.usuariodetalles");
        Response response = target.register(UsuarioDetallesWriter.class)
                .path("{id}")
                .resolveTemplate("id", idReceptor)
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatus() == 200) {
            ud = response.readEntity(UsuarioDetalles.class);
        }
        String foto = "";
        if (ud.getFotoPerfil().equalsIgnoreCase("NE")) {
            foto = Constantes.FOTO_PERFIL_DEFECTO;
        } else {
            foto = ud.getFotoPerfil();
        }
        return foto;
    }

    // elimina una conversación de la BD
    public void deleteConversacion(String id) throws IOException {
        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.chat");

        target.path("delete-chat/{id}").resolveTemplate("id", id).request().delete();
        logger.info("Conversaciones borradas");
        bean.setIdConversacionSelected("0-0");
        FacesContext.getCurrentInstance().getExternalContext()
                .redirect("/sdchat/web/chat/chat.xhtml?idConversacion=0-0&user=" + bean.getIdUserLogeado());
    }

    private String base() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ctx = req.getContextPath();        // p.ej. "/sdChatv2" o "/sdchat"
        int port = req.getLocalPort();          // normalmente 8080 en la Pi
        return "http://localhost:" + port + ctx + "/webresources";
    }

    public String getDisplayTime(String fecha) {
        String display;
        String texto = StringUtils.horaOHaceDias(fecha);
        // si el texto tiene el formado hh:mm entonces el mensaje es del mismo dia
        // no hace falta modificarlo
        // si es un número, entonces el mensaje es de hace al menos un dia 
        if (!texto.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            display = "hace " + texto + " días";
        } else {
            display = texto;
        }
        return display;
    }
}
