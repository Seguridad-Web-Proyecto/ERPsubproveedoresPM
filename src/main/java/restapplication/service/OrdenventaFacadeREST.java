/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.service;

import dao.ClienteJpaController;
import entidades.Cliente;
import entidades.Facturacompra;
import entidades.Facturaventa;
import entidades.Ordenventa;
import entidades.Producto;
import entidades.Ventadetalle;
import entidades.VentadetallePK;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
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
    
    private final ClienteJpaController clienteJpaController = 
            new ClienteJpaController(super.getUserTransaction(), super.getEntityManagerFactory());
    
    @EJB
    private beans.sessions.ProductoFacade productoFacade;
    
    @EJB
    private beans.sessions.VentadetalleFacade ventaDetalleFacade;
    
    @EJB
    private beans.sessions.FacturaventaFacade facturaVentaFacade;

    public OrdenventaFacadeREST() {
        super(Ordenventa.class);
    }

    @POST
    @Override
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Ordenventa entity) {
        // CLIENTE
        Cliente cliente = clienteJpaController.findClienteByEmail(entity.getClienteid().getEmail());
        if(cliente==null){
            return Response.status(Status.BAD_REQUEST).build();
        }
        if(entity.getVentadetalleCollection()!=null){
            String msg = WebServicesUtils.verificarDisponibilidadPedido(entity);
            if(!msg.equals("")){
                return Response.status(Status.BAD_REQUEST).entity(msg).build();
            }
        }
        entity.setClienteid(cliente);
        entity.setSubtotal(0);
        entity.setTotal(0);
        entity.setIva((short)16);
        entity.setFechaVenta(new Date());
        entity.setStatus("pedido pendiente...");
        entity.setVentadetalleCollection(null);
        Ordenventa ordenventaIngresado = null;
        Response response = super.create(entity);
        ordenventaIngresado = (Ordenventa) response.getEntity();
        return response;
    }
    
    @PUT
    @Path("/detalles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarDetalles(Ordenventa venta){
        try{
            //ORDEN VENTA
            // se hace una copia de la orden venta porque de alguna manera el ORM, está cambiando
            // las órdenes de ventas que están asociadas con los productos
            Ordenventa ordenventaQuery = super.find(venta.getOrdenventaid());
             if(ordenventaQuery==null){
                return Response.status(Status.NOT_FOUND).build();
            }
            String msg = WebServicesUtils.verificarDisponibilidadPedido(ordenventaQuery);
            if(!msg.equals("")){
                return Response.status(Status.BAD_REQUEST).entity(msg).build();
            }
            Ordenventa ordenventa = new Ordenventa(ordenventaQuery.getOrdenventaid(), ordenventaQuery.getFechaVenta(),
                    ordenventaQuery.getStatus(), ordenventaQuery.getIva(), ordenventaQuery.getSubtotal(), 
                    ordenventaQuery.getTotal(), ordenventaQuery.getStatus());
            ordenventa.setClienteid(ordenventaQuery.getClienteid());
            if(venta.getVentadetalleCollection()==null) return Response.status(Status.BAD_REQUEST).build();
            ArrayList<Ventadetalle> detalles = new ArrayList<>();
            for(Ventadetalle entity: venta.getVentadetalleCollection()){
                if(entity.getProducto()==null || 
                        entity.getProducto().getProductoid()==null){
                    return Response.status(Status.BAD_REQUEST).build();
                }
                //PRODUCTO
                Producto producto = productoFacade.find(entity.getProducto().getProductoid());
                if(producto==null){
                    return Response.status(Status.NOT_FOUND).build();
                }
                Producto p = Common.aplicarGananciaAlProducto(producto);
                //VENTA DETALLE
                entity.setPrecioUnitario(p.getPrecioUnitario());
                entity.setImporte(p.getPrecioUnitario()*entity.getCantidad());
                // MODIFICAR SUBTOTAL Y TOTAL DE ORDEN DE VENTA
                ordenventa.setSubtotal(ordenventa.getSubtotal()+entity.getImporte());
                long importeIva = (long)16*entity.getImporte()/(long)100;
                importeIva += entity.getImporte();
                ordenventa.setTotal(ordenventa.getTotal()+importeIva);
                // VENTA DETALLE
                entity.setProducto(p);
                entity.setOrdenventa(ordenventa);
                entity.setVentadetallePK(new VentadetallePK(ordenventa.getOrdenventaid(), p.getProductoid()));
                ventaDetalleFacade.create(entity);
                detalles.add(entity);
            }
            ordenventa.setVentadetalleCollection(detalles);
            super.edit(ordenventa);
            return Response.ok().build();
        }catch(Exception ex){
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @POST
    @Path("/solicitar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response realizarPedido(Ordenventa entity){
        Ordenventa ordenventaQuery = super.find(entity.getOrdenventaid());
        Ordenventa ordenventa = new Ordenventa(ordenventaQuery.getOrdenventaid(), ordenventaQuery.getFechaVenta(),
                    ordenventaQuery.getStatus(), ordenventaQuery.getIva(), ordenventaQuery.getSubtotal(), 
                    ordenventaQuery.getTotal(), ordenventaQuery.getStatus());
        ordenventa.setClienteid(ordenventaQuery.getClienteid());
        ordenventa.setVentadetalleCollection((ArrayList<Ventadetalle>) ordenventaQuery.getVentadetalleCollection());
        if(ordenventaQuery==null){
            return Response.status(Status.BAD_REQUEST).build();
        }else{
            ordenventa.setStatus("Pedido realizado!");
            Facturaventa facturaventa = WebServicesUtils.emitirFactura(ordenventaQuery);
            Facturaventa facturaCreada = facturaVentaFacade.createEntity(facturaventa);
            ordenventa.setFacturaid(facturaCreada);
            super.edit(ordenventa);
            return Response.ok(facturaCreada).build();
        }
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