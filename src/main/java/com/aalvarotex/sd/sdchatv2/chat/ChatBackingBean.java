/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.chat;

import com.aalvarotex.sd.sdchatv2.login.LoginBackingBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author alvar
 */
@Named
@SessionScoped
public class ChatBackingBean implements Serializable {
    
    private static final Logger logger = Logger.getLogger(ChatBackingBean.class.getName());
    
    private String newChatUsername;
    private String idConversacionSelected;
    
    private Long idUserLogeado;
 

    public String getNewChatUsername() {
        return newChatUsername;
    }

    public void setNewChatUsername(String newChatUsername) {
        this.newChatUsername = newChatUsername;
    }

    public String getIdConversacionSelected() {
        return idConversacionSelected;
    }

    public void setIdConversacionSelected(String idConversacionSelected) {
        this.idConversacionSelected = idConversacionSelected;
        logger.log(Level.INFO, "Id de la conversaci\u00f3n seleccionada: {0}", this.idConversacionSelected);
    }

    public Long getIdUserLogeado() {
        return idUserLogeado;
    }

    public void setIdUserLogeado(Long idUserLogeado) {
        this.idUserLogeado = idUserLogeado;
    }

    
    
    public void setIdConversacionSelectedRefresh(String idConversacionSelected) throws IOException{
            this.setIdConversacionSelected(idConversacionSelected);
                FacesContext.getCurrentInstance().getExternalContext()
                .redirect("chat.xhtml?idConversacion=" + this.idConversacionSelected + "&user=" + this.idUserLogeado);
                System.out.println("Id de la conversación seleccionada: " + this.idConversacionSelected);
    }
    
    
    
        /**
     *
     * @param mensaje
     */
    public void showError(String mensaje) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    
    
}
