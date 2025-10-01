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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "chat")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Chat.findAll", query = "SELECT c FROM Chat c"),
    @NamedQuery(name = "Chat.findById", query = "SELECT c FROM Chat c WHERE c.id = :id"),
    @NamedQuery(name = "Chat.findByIdConversacion", query = "SELECT c FROM Chat c WHERE c.idConversacion = :idConversacion"),
    @NamedQuery(name = "Chat.findByIdEmisor", query = "SELECT c FROM Chat c WHERE c.idEmisor = :idEmisor"),
    @NamedQuery(name = "Chat.findByIdReceptor", query = "SELECT c FROM Chat c WHERE c.idReceptor = :idReceptor"),
    @NamedQuery(name = "Chat.findByTimestamp", query = "SELECT c FROM Chat c WHERE c.timestamp = :timestamp"),
    @NamedQuery(name = "Chat.findByMensaje", query = "SELECT c FROM Chat c WHERE c.mensaje = :mensaje"),
    @NamedQuery(name = "Chat.findLatestByChat", query = "SELECT c FROM Chat c WHERE c.idConversacion = :idConversacion ORDER BY c.timestamp DESC"),
    @NamedQuery(name = "Chat.findChatsByUser", query = "SELECT c FROM Chat c WHERE c.idEmisor = :idUsuario OR c.idReceptor = :idUsuario")})
public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "id_conversacion")
    private String idConversacion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_emisor")
    private long idEmisor;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_receptor")
    private long idReceptor;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "timestamp")
    private String timestamp;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2147483647)
    @Column(name = "mensaje")
    private String mensaje;

    public Chat() {
    }

    public Chat(Long id) {
        this.id = id;
    }

    public Chat(Long id, String idConversacion, long idEmisor, long idReceptor, String timestamp, String mensaje) {
        this.id = id;
        this.idConversacion = idConversacion;
        this.idEmisor = idEmisor;
        this.idReceptor = idReceptor;
        this.timestamp = timestamp;
        this.mensaje = mensaje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdConversacion() {
        return idConversacion;
    }

    public void setIdConversacion(String idConversacion) {
        this.idConversacion = idConversacion;
    }

    public long getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(long idEmisor) {
        this.idEmisor = idEmisor;
    }

    public long getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(long idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Chat)) {
            return false;
        }
        Chat other = (Chat) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.aalvarotex.sd.sdchatv2.entities.Chat[ id=" + id + " ]";
    }

}
