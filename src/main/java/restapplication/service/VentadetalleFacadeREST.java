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
import java.util.List;
import javax.ejb.EJB;
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
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import restapplication.Common;

/**
 *
 * @author jcami
 */
@Stateless
@Path("pedidos/detalles")
public class VentadetalleFacadeREST extends AbstractFacade<Ventadetalle> {

    @PersistenceContext(unitName = "com.mycompany_ERPsubprovee_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @EJB
    private beans.sessions.ProductoFacade productoFacade;
    
    @EJB
    private beans.sessions.OrdenventaFacade ordenventaFacade;

    private VentadetallePK getPrimaryKey(PathSegment pathSegment) {
        /*
         * pathSemgent represents a URI path segment and any associated matrix parameters.
         * URI path part is supposed to be in form of 'somePath;ventaid=ventaidValue;productoid=productoidValue'.
         * Here 'somePath' is a result of getPath() method invocation and
         * it is ignored in the following code.
         * Matrix parameters are used as field names to build a primary key instance.
         */
        entidades.VentadetallePK key = new entidades.VentadetallePK();
        javax.ws.rs.core.MultivaluedMap<String, String> map = pathSegment.getMatrixParameters();
        java.util.List<String> ventaid = map.get("ventaid");
        if (ventaid != null && !ventaid.isEmpty()) {
            key.setVentaid(new java.lang.Long(ventaid.get(0)));
        }
        java.util.List<String> productoid = map.get("productoid");
        if (productoid != null && !productoid.isEmpty()) {
            key.setProductoid(new java.lang.Long(productoid.get(0)));
        }
        return key;
    }

    public VentadetalleFacadeREST() {
        super(Ventadetalle.class);
    }

    /*@POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response create(Ventadetalle entity) {
        Response response = super.create(entity);
        Ventadetalle ventadetalle = (Ventadetalle) response.getEntity();
        return response;
    }*/
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDetalles(List<Ventadetalle> entities){
        try{
            for(Ventadetalle entity: entities){
                if(entity.getOrdenventa()==null ||
                        entity.getOrdenventa().getOrdenventaid()==null ||
                        entity.getProducto()==null || 
                        entity.getProducto().getProductoid()==null){
                    return Response.status(Status.BAD_REQUEST).build();
                }
                //PRODUCTO
                Producto producto = productoFacade.find(entity.getProducto().getProductoid());
                if(producto==null){
                    return Response.status(Status.NOT_FOUND).build();
                }
                Producto p = Common.aplicarGananciaAlProducto(producto);
                //ORDEN VENTA
                Ordenventa ordenventa = ordenventaFacade.find(entity.getOrdenventa().getOrdenventaid());
                if(ordenventa==null){
                    return Response.status(Status.NOT_FOUND).build();
                }
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
                
                Response response = super.create(entity);
                Ventadetalle ventadetalle = (Ventadetalle) response.getEntity();
                
            }
            return Response.ok().build();
        }catch(Exception ex){
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") PathSegment id, Ventadetalle entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") PathSegment id) {
        entidades.VentadetallePK key = getPrimaryKey(id);
        super.remove(super.find(key));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Ventadetalle find(@PathParam("id") PathSegment id) {
        entidades.VentadetallePK key = getPrimaryKey(id);
        return super.find(key);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Ventadetalle> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Ventadetalle> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
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
