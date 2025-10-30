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
@Table(name = "usuario_detalles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UsuarioDetalles.findAll", query = "SELECT u FROM UsuarioDetalles u"),
    @NamedQuery(name = "UsuarioDetalles.findByIdUsuario", query = "SELECT u FROM UsuarioDetalles u WHERE u.idUsuario = :idUsuario"),
    @NamedQuery(name = "UsuarioDetalles.findByFotoPerfil", query = "SELECT u FROM UsuarioDetalles u WHERE u.fotoPerfil = :fotoPerfil"),
    @NamedQuery(name = "UsuarioDetalles.findByColorPreferente", query = "SELECT u FROM UsuarioDetalles u WHERE u.colorPreferente = :colorPreferente")})
public class UsuarioDetalles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_usuario")
    private Long idUsuario;
    @Column(name = "foto_perfil", length=10485760)
    private String fotoPerfil;
    @Column(name = "color_preferente")
    private String colorPreferente;

    public UsuarioDetalles() {
    }

    // por defecto, color y foto son NE (no especificado)
    public UsuarioDetalles(Long idUsuario) {
        this.idUsuario = idUsuario;
        this.colorPreferente = "NE";
        this.fotoPerfil = "NE";
    }
    

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getColorPreferente() {
        return colorPreferente;
    }

    public void setColorPreferente(String colorPreferente) {
        this.colorPreferente = colorPreferente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsuarioDetalles)) {
            return false;
        }
        UsuarioDetalles other = (UsuarioDetalles) object;
        if ((this.idUsuario == null && other.idUsuario != null) || (this.idUsuario != null && !this.idUsuario.equals(other.idUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.aalvarotex.sd.sdchatv2.entities.UsuarioDetalles[ idUsuario=" + idUsuario + " ]";
    }
    
}
