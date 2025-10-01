/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alvar
 */
@Entity
@Table(name = "usuario_grupo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UsuarioGrupo.findAll", query = "SELECT u FROM UsuarioGrupo u"),
    @NamedQuery(name = "UsuarioGrupo.findByNombreUsuario", query = "SELECT u FROM UsuarioGrupo u WHERE u.nombreUsuario = :nombreUsuario"),
    @NamedQuery(name = "UsuarioGrupo.findByNombreRol", query = "SELECT u FROM UsuarioGrupo u WHERE u.nombreRol = :nombreRol")})
public class UsuarioGrupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "nombre_usuario")
    private String nombreUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "nombre_grupo")
    private String nombreRol;

    /**
     *
     */
    public UsuarioGrupo() {
    }

    /**
     *
     * @param nombreUsuario
     */
    public UsuarioGrupo(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     *
     * @param nombreUsuario
     * @param nombreRol
     */
    public UsuarioGrupo(String nombreUsuario, String nombreRol) {
        this.nombreUsuario = nombreUsuario;
        this.nombreRol = nombreRol;
    }

    /**
     *
     * @return
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     *
     * @param nombreUsuario
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     *
     * @return
     */
    public String getNombreRol() {
        return nombreRol;
    }

    /**
     *
     * @param nombreRol
     */
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nombreUsuario != null ? nombreUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsuarioGrupo)) {
            return false;
        }
        UsuarioGrupo other = (UsuarioGrupo) object;
        return !((this.nombreUsuario == null && other.nombreUsuario != null) || (this.nombreUsuario != null && !this.nombreUsuario.equals(other.nombreUsuario)));
    }

    @Override
    public String toString() {
        return "[ idUsuario=" + nombreUsuario + " ]";
    }


    
}
