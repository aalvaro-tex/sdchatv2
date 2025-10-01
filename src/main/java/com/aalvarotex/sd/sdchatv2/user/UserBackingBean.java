/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.user;

import com.aalvarotex.sd.sdchatv2.utils.ImageUtils;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.component.colorpicker.ColorPicker;
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
    private String colorPreferente;
    private String fotoPerfilSrc;
    
    @Inject
    private ImageUtils imageUtils;

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

    public String getColorPreferente() {
        return colorPreferente;
    }

    public void setColorPreferente(String colorPreferente) {
        this.colorPreferente = colorPreferente;
    }

    public String getFotoPerfilSrc() {
        return fotoPerfilSrc;
    }

    public void setFotoPerfilSrc(String fotoPerfilSrc) {
        this.fotoPerfilSrc = fotoPerfilSrc;
    }
    
}
