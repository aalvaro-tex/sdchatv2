/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.login;

import com.aalvarotex.sd.sdchatv2.chat.ChatBackingBean;
import com.aalvarotex.sd.sdchatv2.entities.Usuario;
import com.aalvarotex.sd.sdchatv2.entities.UsuarioDetalles;
import com.aalvarotex.sd.sdchatv2.jaas.UsuarioEJB;
import com.aalvarotex.sd.sdchatv2.json.UsuarioDetallesWriter;
import com.aalvarotex.sd.sdchatv2.json.UsuarioReader;
import com.aalvarotex.sd.sdchatv2.json.UsuarioWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
@Named
@RequestScoped
public class LoginClientBean implements Serializable {

    private static final Logger logger = Logger.getLogger(LoginClientBean.class.getName());

    Client client;
    WebTarget target;

    @Inject
    LoginBackingBean bean;

    @Inject
    ChatBackingBean chatBean;

    @Inject
    UsuarioEJB usuarioEJB;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        bean.setUsuarioLogeado(null);
    }

    public void signup() {
        Usuario u = new Usuario(bean.getNuevoNombreUsuario(), bean.getNuevoPassword());
        logger.log(Level.INFO, "Usuario a crear: {0} y {1}", new Object[]{u.getNombreUsuario(), u.getPassword()});
        try {
            usuarioEJB.createUser(u);
            try {
                Usuario found = usuarioEJB.findByNombreUsuario(u.getNombreUsuario());
                bean.setUsuarioLogeado(found);
                logger.log(Level.INFO, "Usuario creado: {0}", found.getNombreUsuario());
                // añadimos a la tabla de detalles de usuario una línea con el id del usuario y el resto vacío
                target = client
                        .target(base())
                        .path("com.aalvarotex.sd.sdchatv2.entities.usuariodetalles");
                UsuarioDetalles ud = new UsuarioDetalles(found.getId());
                Response r = target.register(UsuarioDetallesWriter.class)
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(ud, MediaType.APPLICATION_JSON));
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("/sdchat/web/chat/chat.xhtml?idConversacion=0-0&user=" + found.getId());
            } catch (Exception e) {
                bean.showError("Error al crear usuario");
                e.printStackTrace();
            }
        } catch (Exception e) {
            bean.showError("El nombre de usuario ya está en uso");
        }

    }

    public void login() throws IOException, Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(bean.getNombreUsuario(), bean.getPassword());
        } catch (ServletException e) {
            bean.showError("El usuario no está registrado");
            e.printStackTrace();
        }
        try {
            Usuario found = usuarioEJB.findByNombreUsuario(bean.getNombreUsuario());
            // aqui recuperamos los detalles del usuario para establecer el tema
            UsuarioDetalles ud = new UsuarioDetalles();
            target = client
                    .target(base())
                    .path("com.aalvarotex.sd.sdchatv2.entities.usuariodetalles");
            Response response = target.register(UsuarioDetallesWriter.class)
                    .path("{id}")
                    .resolveTemplate("id", found.getId())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() == 200) {
                ud = response.readEntity(UsuarioDetalles.class);
                bean.setTemaUsuario(ud.getTema());
                System.out.println("El tema del usuario es: " + bean.getTemaUsuario());
            }
            bean.setUsuarioLogeado(found);
            if (request.isUserInRole("usuario")) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("/sdchat/web/chat/chat.xhtml?idConversacion=0-0&user=" + found.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            bean.setUsuarioLogeado(null);
            chatBean.setIdConversacionSelected(null);
            request.logout();
            ((HttpSession) context.getExternalContext().getSession(false)).invalidate();
        } catch (ServletException e) {
            bean.showError("Error al cerrar sesión");
        }

        return "logoutok";
    }

    private String base() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ctx = req.getContextPath();        // p.ej. "/sdChatv2" o "/sdchat"
        int port = req.getLocalPort();          // normalmente 8080 en la Pi
        return "http://localhost:" + port + ctx + "/webresources";
    }
}
