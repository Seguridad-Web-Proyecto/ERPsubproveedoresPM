/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.service;

import dao.ClienteJpaController;
import entidades.Cliente;
import entidades.Ordenventa;
import entidades.Producto;
import entidades.Ventadetalle;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import restapplication.Common;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * El cliente está emitiendo una orden de compra o haciendo un pedido.
 * En éste caso él va a indicar qué productos necesita. Pero para nosotros esto sería siendo una orden de venta.
 * Ya que le estamos vendiendo productos al cliente.
 * @author jcami
 */
@Stateless
@Path("pedidos")
public class OrdenventaFacadeREST extends AbstractFacade<Ordenventa> {

    @PersistenceContext(unitName = "com.mycompany_ERPsubprovee_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private beans.sessions.ProductoFacade productoFacade;
    
    private ClienteJpaController clienteJpaController = 
            new ClienteJpaController(super.getUserTransaction(), super.getEntityManagerFactory());

    public OrdenventaFacadeREST() {
        super(Ordenventa.class);
    }

    @POST
    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Ordenventa entity) {
        //Ordenventa ordenventa = super.create(entity);
        // CLIENTE
        Cliente cliente = clienteJpaController.findClienteByEmail(entity.getClienteid().getEmail());
        if(cliente==null){
            return Response.status(Response.Status.BAD_REQUEST).entity("El cliente ingresado no existe").build();
        }
        entity.setClienteid(cliente);
        // VENTADETALLE
        Long subtotal = 0L;
        Collection<Ventadetalle> detalles = entity.getVentadetalleCollection();
        for(Ventadetalle ventaDetalle: detalles){
            Long productoId = ventaDetalle.getProducto().getProductoid();
            Producto producto = productoFacade.find(productoId);
            if(producto==null){
                return Response.status(Response.Status.BAD_REQUEST).entity("El producto "+productoId+" no existe").build();
            }
            Producto p = Common.aplicarGananciaAlProducto(producto);
            ventaDetalle.setPrecioUnitario(p.getPrecioUnitario());
            ventaDetalle.setProducto(p);
            Long importe = (long)p.getPrecioUnitario()*ventaDetalle.getCantidad();
            ventaDetalle.setImporte(importe);
            subtotal += importe;
        }
        Long total = subtotal*16/100 + subtotal;
        entity.setSubtotal(subtotal);
        entity.setTotal(total);
        entity.setIva((short)16);
        entity.setFechaVenta(new Date());
        entity.setStatus("pedido pendiente...");
        entity.setVentadetalleCollection(null);
        Ordenventa ordenventaIngresado = null;
        
        return Response.ok().build();
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
