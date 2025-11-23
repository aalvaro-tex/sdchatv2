/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.user;

import com.aalvarotex.sd.sdchatv2.entities.UsuarioDetalles;
import com.aalvarotex.sd.sdchatv2.utils.ImageUtils;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author alvar
 */
@Named
@SessionScoped
public class UserBackingBean implements Serializable {

    private UploadedFile fotoPerfil;
    private String nuevoNombre;
    private String fotoPerfilSrc;
    private String tema;

    private Long idUsuarioLogeado;

    private UsuarioDetalles ud;

    @Inject
    private ImageUtils imageUtils;
    @Inject
    private UserClientBean userClientBean;

    public void onPreRenderView() {
        userClientBean.getUserDetails();
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }
    
    public UploadedFile getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(UploadedFile fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
        this.setFotoPerfilSrc(imageUtils.upload(fotoPerfil));
    }

    public String getNuevoNombre() {
        return nuevoNombre;
    }

    public void setNuevoNombre(String nuevoNombre) {
        this.nuevoNombre = nuevoNombre;
    }

    public String getFotoPerfilSrc() {
        if (this.fotoPerfil != null) {
            return this.imageUtils.upload(fotoPerfil);
        } else {
            return fotoPerfilSrc;
        }
    }

    public void setFotoPerfilSrc(String fotoPerfilSrc) {
        this.fotoPerfilSrc = fotoPerfilSrc;
    }

    public Long getIdUsuarioLogeado() {
        return idUsuarioLogeado;
    }

    public void setIdUsuarioLogeado(Long idUsuarioLogeado) {
        this.idUsuarioLogeado = idUsuarioLogeado;
    }

    public UsuarioDetalles getUd() {
        return ud;
    }

    public void setUd(UsuarioDetalles ud) {
        this.ud = ud;
    }

    public void showInfo(String mensaje) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cambios guardados", mensaje);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
