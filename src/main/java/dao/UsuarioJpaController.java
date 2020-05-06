/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.exceptions.IllegalOrphanException;
import dao.exceptions.NonexistentEntityException;
import dao.exceptions.PreexistingEntityException;
import dao.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Rol;
import entidades.Persona;
import entidades.Usuario;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Saul
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (usuario.getPersonaCollection() == null) {
            usuario.setPersonaCollection(new ArrayList<Persona>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Rol rol = usuario.getRol();
            if (rol != null) {
                rol = em.getReference(rol.getClass(), rol.getEmail());
                usuario.setRol(rol);
            }
            Collection<Persona> attachedPersonaCollection = new ArrayList<Persona>();
            for (Persona personaCollectionPersonaToAttach : usuario.getPersonaCollection()) {
                personaCollectionPersonaToAttach = em.getReference(personaCollectionPersonaToAttach.getClass(), personaCollectionPersonaToAttach.getPersonaid());
                attachedPersonaCollection.add(personaCollectionPersonaToAttach);
            }
            usuario.setPersonaCollection(attachedPersonaCollection);
            em.persist(usuario);
            if (rol != null) {
                Usuario oldUsuarioOfRol = rol.getUsuario();
                if (oldUsuarioOfRol != null) {
                    oldUsuarioOfRol.setRol(null);
                    oldUsuarioOfRol = em.merge(oldUsuarioOfRol);
                }
                rol.setUsuario(usuario);
                rol = em.merge(rol);
            }
            for (Persona personaCollectionPersona : usuario.getPersonaCollection()) {
                Usuario oldEmailOfPersonaCollectionPersona = personaCollectionPersona.getEmail();
                personaCollectionPersona.setEmail(usuario);
                personaCollectionPersona = em.merge(personaCollectionPersona);
                if (oldEmailOfPersonaCollectionPersona != null) {
                    oldEmailOfPersonaCollectionPersona.getPersonaCollection().remove(personaCollectionPersona);
                    oldEmailOfPersonaCollectionPersona = em.merge(oldEmailOfPersonaCollectionPersona);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUsuario(usuario.getEmail()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getEmail());
            Rol rolOld = persistentUsuario.getRol();
            Rol rolNew = usuario.getRol();
            Collection<Persona> personaCollectionOld = persistentUsuario.getPersonaCollection();
            Collection<Persona> personaCollectionNew = usuario.getPersonaCollection();
            List<String> illegalOrphanMessages = null;
            if (rolOld != null && !rolOld.equals(rolNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Rol " + rolOld + " since its usuario field is not nullable.");
            }
            for (Persona personaCollectionOldPersona : personaCollectionOld) {
                if (!personaCollectionNew.contains(personaCollectionOldPersona)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Persona " + personaCollectionOldPersona + " since its email field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (rolNew != null) {
                rolNew = em.getReference(rolNew.getClass(), rolNew.getEmail());
                usuario.setRol(rolNew);
            }
            Collection<Persona> attachedPersonaCollectionNew = new ArrayList<Persona>();
            for (Persona personaCollectionNewPersonaToAttach : personaCollectionNew) {
                personaCollectionNewPersonaToAttach = em.getReference(personaCollectionNewPersonaToAttach.getClass(), personaCollectionNewPersonaToAttach.getPersonaid());
                attachedPersonaCollectionNew.add(personaCollectionNewPersonaToAttach);
            }
            personaCollectionNew = attachedPersonaCollectionNew;
            usuario.setPersonaCollection(personaCollectionNew);
            usuario = em.merge(usuario);
            if (rolNew != null && !rolNew.equals(rolOld)) {
                Usuario oldUsuarioOfRol = rolNew.getUsuario();
                if (oldUsuarioOfRol != null) {
                    oldUsuarioOfRol.setRol(null);
                    oldUsuarioOfRol = em.merge(oldUsuarioOfRol);
                }
                rolNew.setUsuario(usuario);
                rolNew = em.merge(rolNew);
            }
            for (Persona personaCollectionNewPersona : personaCollectionNew) {
                if (!personaCollectionOld.contains(personaCollectionNewPersona)) {
                    Usuario oldEmailOfPersonaCollectionNewPersona = personaCollectionNewPersona.getEmail();
                    personaCollectionNewPersona.setEmail(usuario);
                    personaCollectionNewPersona = em.merge(personaCollectionNewPersona);
                    if (oldEmailOfPersonaCollectionNewPersona != null && !oldEmailOfPersonaCollectionNewPersona.equals(usuario)) {
                        oldEmailOfPersonaCollectionNewPersona.getPersonaCollection().remove(personaCollectionNewPersona);
                        oldEmailOfPersonaCollectionNewPersona = em.merge(oldEmailOfPersonaCollectionNewPersona);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getEmail();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getEmail();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Rol rolOrphanCheck = usuario.getRol();
            if (rolOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Rol " + rolOrphanCheck + " in its rol field has a non-nullable usuario field.");
            }
            Collection<Persona> personaCollectionOrphanCheck = usuario.getPersonaCollection();
            for (Persona personaCollectionOrphanCheckPersona : personaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Persona " + personaCollectionOrphanCheckPersona + " in its personaCollection field has a non-nullable email field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
