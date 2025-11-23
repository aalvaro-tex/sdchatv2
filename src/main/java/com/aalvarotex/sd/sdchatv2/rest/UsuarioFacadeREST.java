/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.rest;

import com.aalvarotex.sd.sdchatv2.entities.Usuario;
import java.util.List;
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
@Path("com.aalvarotex.sd.sdchatv2.entities.usuario")
public class UsuarioFacadeREST extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "com.aalvaro-tex.sd_sdChatv2_war_1.0PU")
    private EntityManager em;

    public UsuarioFacadeREST() {
        super(Usuario.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Usuario entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Long id, Usuario entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usuario find(@PathParam("id") Long id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Usuario> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    /* Método propio 
       Recupera un usuario dado su nombre de usuario
     */
    @GET
    @Path("find-by-username/{username}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Usuario findByUsername(@PathParam("username") String username) {
        List<Usuario> all = this.findAll();
        Usuario found = null;
        for (Usuario u : all) {
            if (u.getNombreUsuario().equalsIgnoreCase(username)) {
                found = u;
            }
        }
        System.out.println(found != null ? found.getNombreUsuario() : "no");
        return found;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
