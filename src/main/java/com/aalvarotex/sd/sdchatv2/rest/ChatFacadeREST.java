/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.rest;

import com.aalvarotex.sd.sdchatv2.entities.Chat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author alvar
 */
@Stateless
@Path("com.aalvarotex.sd.sdchatv2.entities.chat")
public class ChatFacadeREST extends AbstractFacade<Chat> {

    @PersistenceContext(unitName = "com.aalvaro-tex.sd_sdChatv2_war_1.0PU")
    private EntityManager em;

    public ChatFacadeREST() {
        super(Chat.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Chat entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Long id, Chat entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @DELETE
    @Path("delete-chat/{id}")
    public void deleteChatById(@PathParam("id") String id) {
        List<Chat> all = this.findAll();
        for (Chat c : all) {
            if (c.getIdConversacion().equalsIgnoreCase(id)) {
                this.remove(c);
            }
        }
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Chat find(@PathParam("id") Long id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Chat> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Chat> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    /* Método propio */
    // Busca si una posible conversación ya existe o no
    @GET
    @Path("exists/{idConversacion}")
    public boolean existeConversacion(@PathParam("idConversacion") String idConversacion) {
        boolean existe = false;
        List<Chat> all = this.findAll();
        if (!all.isEmpty()) {
            for (Chat c : all) {
                if (c.getIdConversacion().equalsIgnoreCase(idConversacion)) {
                    existe = true;
                    System.out.println("Existe la conversación " + idConversacion);
                }
            }
        }
        return existe;
    }

    // Busca todos los chats en los que participa un usuario
    /**
     *
     * @param idUsuario
     * @return
     */
    @GET
    @Path("my-chats/{idUsuario}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Chat> getChatsByUser(@PathParam("idUsuario") Long idUsuario) {
        try {
            // Trae los chats (ideal: que la named query ya los ordene por fecha DESC o ASC según necesites)
            List<Chat> chats = em.createNamedQuery("Chat.findChatsByUser", Chat.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();

            if (chats.isEmpty()) {
                return Collections.emptyList();
            }

            // Deduplicar por idConversacion sin modificar la lista mientras se itera
            HashSet<Object> seen = new HashSet<Object>();
            List<Chat> unique = new ArrayList<>();

            for (Chat c : chats) {
                String idConv = c.getIdConversacion();
                // normaliza para case-insensitive y evita NPE
                String key = (idConv == null) ? null : idConv.toLowerCase(Locale.ROOT);
                if (seen.add(key)) { // add devuelve true la primera vez que aparece
                    unique.add(c);
                }
            }
            return unique;
        } catch (Exception e) {
            // Loguea y devuelve lista vacía (o relanza según tu política)
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Busca los 4 últimos mensajes enviados en una conversación concreta
    /**
     *
     * @param idConversacion
     * @return
     */
    @GET
    @Path("last-message/{idConversacion}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Chat> getLastMessageByChatAndUser(@PathParam("idConversacion") String idConversacion) {
        List<Chat> chats = null;
        try {
            chats = em.createNamedQuery("Chat.findLatestByChat", Chat.class)
                    .setParameter("idConversacion", idConversacion)
                    .setMaxResults(4)
                    .getResultList();
        } catch (Exception e) {
            System.out.println(e);
        }
        return chats;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
