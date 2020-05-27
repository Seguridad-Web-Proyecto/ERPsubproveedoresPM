/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.api_consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entidades.Cliente;
import entidades.Ordenventa;
import entidades.Producto;
import entidades.Ventadetalle;
import entidades.VentadetallePK;
import java.util.ArrayList;
import javax.ws.rs.core.Response;

/**
 *
 * @author jcami
 */
public class SolicitarPedidos {

    public static void main(String[] args) throws JsonProcessingException {
        /*Ordenventa ordenventa = pruebaGenerarPedido("Orden de venta realizada a las 7:23pm 19/05/2020");
        System.out.println(ordenventa);
        Response response = pruebaAgregarDetallesAlPedido(ordenventa);
        System.out.println("Solicitando pedido...");
        Response responseSolicitar = APIConsumer.concluirPedido(ordenventa);
        System.out.println("Respuesta: "+responseSolicitar.getStatus());*/
        pruebaProductosNoDisponibles();
    }
    
    public static void pruebaProductosNoDisponibles(){
        Ordenventa ordenventa = pruebaGenerarPedido("Prueba generar pedido 27/05/2020");
        ArrayList<Ventadetalle> detallesV1 = new ArrayList<>();
        for(int i=7; i<9; i++){
            Producto producto = new Producto(); producto.setProductoid((long)i);
            Ventadetalle ventadetalle = new Ventadetalle();
            ventadetalle.setProducto(producto); ventadetalle.setCantidad(2000);
            detallesV1.add(ventadetalle);
        }
        ordenventa.setVentadetalleCollection(detallesV1);
        System.out.println("Venta detalles primera versión");
        Response respuestaDetallesV1 = APIConsumer.agregarDetallesAlPedido(ordenventa);
        System.out.println("Respuesta: "+respuestaDetallesV1.getStatus());
        ArrayList<Ventadetalle> detallesV2 = new ArrayList<>();
        for(int i=8; i<10; i++){
            Producto producto = new Producto(); producto.setProductoid((long)i);
            Ventadetalle ventadetalle = new Ventadetalle();
            ventadetalle.setProducto(producto); ventadetalle.setCantidad(100);
            detallesV2.add(ventadetalle);
        }
        ordenventa.setVentadetalleCollection(detallesV2);
        System.out.println("Venta detalles segunda versión");
        Response respuestaDetallesV2 = APIConsumer.agregarDetallesAlPedido(ordenventa);
        System.out.println("Respuesta: "+respuestaDetallesV2.getStatus());
        System.out.println("Concluyendo pedido");
        Response respuesta = APIConsumer.concluirPedido(ordenventa);
        System.out.println("Respuesta: "+respuesta.getStatus());
    }
    
    public static Ordenventa pruebaGenerarPedido(String descripcion) {
        Ordenventa ordenventa = new Ordenventa();
        Cliente cliente = new Cliente();
        cliente.setEmail("compras@walmart.com.mx");
        ordenventa.setClienteid(cliente);
        ordenventa.setDescripcion(descripcion);
        //agregarDetallesAlPedido(ordenventa, detalles);
        System.out.println("Realizando pedido...");
        Response responseOrdenVenta = APIConsumer.realizarPedido(ordenventa);
        System.out.println("Respuesta: "+responseOrdenVenta.getStatus());
        if(responseOrdenVenta.getStatus()!=200){
            return null;
        }
        Ordenventa entityResult = responseOrdenVenta.readEntity(Ordenventa.class);
        return entityResult;
    }
    
    public static Response pruebaAgregarDetallesAlPedido(Ordenventa ordenventa){
        System.out.println("Haciendo petición POST para insertar los detalles de la orden...");
        ArrayList<Ventadetalle> ventadetalleList = new ArrayList<>();
        for(long i=7; i<9; i++){
            Producto producto = new Producto();
            producto.setProductoid(i);
            Ventadetalle ventadetalle = new Ventadetalle(new VentadetallePK(ordenventa.getOrdenventaid(), producto.getProductoid()));
            ventadetalle.setOrdenventa(ordenventa);
            ventadetalle.setProducto(producto);
            ventadetalle.setCantidad(100);
            ventadetalleList.add(ventadetalle);
        }
        ordenventa.setVentadetalleCollection(ventadetalleList);
        Response responseDetalles = APIConsumer.agregarDetallesAlPedido(ordenventa);
        System.out.println("Respuesta: "+responseDetalles.getStatus());
        return responseDetalles;
    }
    
}
