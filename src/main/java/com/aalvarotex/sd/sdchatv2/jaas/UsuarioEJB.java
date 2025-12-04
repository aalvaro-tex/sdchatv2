/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.jaas;

import com.aalvarotex.sd.sdchatv2.entities.Usuario;
import com.aalvarotex.sd.sdchatv2.entities.UsuarioGrupo;
import com.aalvarotex.sd.sdchatv2.utils.AutenticacionUtils;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author alvar
 */
@Stateless
public class UsuarioEJB {

    @PersistenceContext
    private EntityManager em;

    /**
     *
     * @param user
     * @return
     * @throws Exception
     */
    public void createUser(Usuario user) throws Exception {
        Long idUsuarioCreado = -1L;
        try {
            String hashAlmacenado = AutenticacionUtils.encodeSHA256(user.getPassword());
            user.setPassword(hashAlmacenado);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UsuarioGrupo group = new UsuarioGrupo();
        group.setNombreUsuario(user.getNombreUsuario());
        group.setNombreRol("usuario");
        try {
            if (em.createNamedQuery("Usuario.findByNombreUsuario", Usuario.class)
                    .setParameter("nombreUsuario", user.getNombreUsuario())
                    .getResultList().size() < 1) {
                em.persist(user);
                System.out.println(user.getNombreUsuario());
            } else {
                throw new Exception();
            }
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(err -> System.out.println(err.toString()));
        }
        try {
            em.persist(group);
            System.out.println("Usuario guardado en grupo: " + group.getNombreRol());
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(err -> System.out.println(err.toString()));
        }
    }

    /**
     *
     * @param nombreUsuario
     * @return
     * @throws Exception
     */
    public Usuario findByNombreUsuario(String nombreUsuario) throws Exception {
        TypedQuery<Usuario> query = em.createNamedQuery("Usuario.findByNombreUsuario",
                Usuario.class);
        query.setParameter("nombreUsuario", nombreUsuario);
        Usuario user = null;
        try {
            user = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();

        }
        return user;
    }

}
