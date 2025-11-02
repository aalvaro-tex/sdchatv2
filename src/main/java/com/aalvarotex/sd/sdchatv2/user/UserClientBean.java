/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.user;

import com.aalvarotex.sd.sdchatv2.entities.UsuarioDetalles;
import com.aalvarotex.sd.sdchatv2.json.UsuarioDetallesWriter;
import com.aalvarotex.sd.sdchatv2.login.LoginBackingBean;
import com.aalvarotex.sd.sdchatv2.utils.Constantes;
import com.aalvarotex.sd.sdchatv2.utils.ImageUtils;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
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
public class UserClientBean {

    Client client;
    WebTarget target;

    @Inject
    private ImageUtils imageUtils;
    @Inject
    private UserBackingBean userBackingBean;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
    }
    
    // recupera los detalles del usuario
    public void getUserDetails(){
        UsuarioDetalles ud = new UsuarioDetalles();
         target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.usuariodetalles");
         
        Response response = target.register(UsuarioDetallesWriter.class)
                .path("{id}")
                .resolveTemplate("id", userBackingBean.getIdUsuarioLogeado())
                .request(MediaType.APPLICATION_JSON)
                .get();
        if (response.getStatus() == 200) {
            ud = response.readEntity(UsuarioDetalles.class);
        }
        if(ud.getFotoPerfil().equalsIgnoreCase("NE")){
            ud.setFotoPerfil(Constantes.fotoPerfilDefecto);
        }
        if(ud.getColorPreferente().equalsIgnoreCase("NE")){
            ud.setColorPreferente(Constantes.colorPreferenteDefecto);
        }
        this.userBackingBean.setUd(ud);
        this.userBackingBean.setFotoPerfilSrc(ud.getFotoPerfil());
        System.out.println("Color pref: " + userBackingBean.getUd().getColorPreferente());
    }

    // guarda los cambios en los datos de usuario
    public void saveChanges() {
        UsuarioDetalles ud = new UsuarioDetalles(userBackingBean.getIdUsuarioLogeado());

        if (userBackingBean.getFotoPerfil() != null) {
            ud.setFotoPerfil(imageUtils.upload(userBackingBean.getFotoPerfil()));
        }
        if (userBackingBean.getColorPreferente() != null) {
            ud.setColorPreferente(userBackingBean.getColorPreferente());
        }
        System.out.println("Cambios guardados para usuario " + ud.getIdUsuario());
        System.out.println("Color: " + ud.getColorPreferente() + " e imagen actualizada: " + ud.getFotoPerfil());

        target = client
                .target(base())
                .path("com.aalvarotex.sd.sdchatv2.entities.usuariodetalles");
        
        Response r = target.register(UsuarioDetallesWriter.class)
                .path("{id}")
                .resolveTemplate("id", ud.getIdUsuario())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(ud, MediaType.APPLICATION_JSON));

        System.out.println(r.toString());
    }
    
    private String base() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ctx = req.getContextPath();        // p.ej. "/sdChatv2" o "/sdchat"
        int port = req.getLocalPort();          // normalmente 8080 en la Pi
        return "http://localhost:" + port + ctx + "/webresources";
    }
}
