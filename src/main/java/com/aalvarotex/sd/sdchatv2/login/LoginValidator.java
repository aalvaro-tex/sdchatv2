/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.login;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.validation.constraints.Size;

/**
 *
 * @author alvar
 */
@Named
@RequestScoped
public class LoginValidator {

    @Size(min = 8)
    private String nombreUsuario;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

}
