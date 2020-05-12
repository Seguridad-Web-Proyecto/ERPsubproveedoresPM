/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.service;

import entidades.Ordenventa;
import entidades.Producto;
import entidades.Ventadetalle;
import entidades.VentadetallePK;
import java.util.ArrayList;
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
import restapplication.Common;

/**
 *
 * @author jcami
 */
@Stateless
@Path("pedidos")
public class OrdenventaFacadeREST extends AbstractFacade<Ordenventa> {

    @PersistenceContext(unitName = "com.mycompany_ERPsubprovee_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public OrdenventaFacadeREST() {
        super(Ordenventa.class);
    }

    @POST
    @Override
    //@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    public Ordenventa create(Ordenventa entity) {
        return super.create(entity);
    }

    @GET
    @Path("{id}")
    //@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public Ordenventa find(@PathParam("id") Long id) {
        return Common.limpiarOrdenVenta(super.find(id));
    }

    @GET
    @Override
    //@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ordenventa> findAll() {
        List<Ordenventa> ordenes = super.findAll();
        List<Ordenventa> returnList = new ArrayList<>();
        for(Ordenventa orden: ordenes){
            returnList.add(Common.limpiarOrdenVenta(orden));
        }
        return returnList;
    }

    @GET
    @Path("{from}/{to}")
    //@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ordenventa> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        List<Ordenventa> ordenes = super.findRange(new int[]{from, to});
        List<Ordenventa> returnList = new ArrayList<>();
        for(Ordenventa orden: ordenes){
            returnList.add(Common.limpiarOrdenVenta(orden));
        }
        return returnList;
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
