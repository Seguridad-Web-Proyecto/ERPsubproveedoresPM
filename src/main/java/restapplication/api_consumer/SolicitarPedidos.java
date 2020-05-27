/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.api_consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import entidades.Cliente;
import entidades.Ordenventa;
import entidades.Producto;
import entidades.Ventadetalle;
import entidades.VentadetallePK;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;

/**
 *
 * @author jcami
 */
public class SolicitarPedidos {

    public static void main(String[] args) throws JsonProcessingException {
        try {
            /*Ordenventa ordenventa = pruebaGenerarPedido("Orden de venta realizada a las 7:23pm 19/05/2020");
            System.out.println(ordenventa);
            Response response = pruebaAgregarDetallesAlPedido(ordenventa);
            System.out.println("Solicitando pedido...");
            Response responseSolicitar = APIConsumer.concluirPedido(ordenventa);
            System.out.println("Respuesta: "+responseSolicitar.getStatus());*/
            //pruebaProductosNoDisponibles();
            ArrayList<Ventadetalle> detalles = new ArrayList<>();
            for(long i=25; i<27; i++){
                Producto producto = new Producto();
                producto.setProductoid(i);
                Ventadetalle ventadetalle = new Ventadetalle();
                ventadetalle.setProducto(producto);
                ventadetalle.setCantidad(10);
                detalles.add(ventadetalle);
            }
            APIConsumer.generarPedidoCompleto("Solicitando productos para walmart", detalles);
        } catch (Exception ex) {
            //ex.printStackTrace();
            Logger.getLogger(SolicitarPedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void pruebaProductosNoDisponibles(){
        ArrayList<Ventadetalle> detalles = new ArrayList<>();
        for(long i=20; i<22; i++){
            Producto producto = new Producto(); 
            producto.setProductoid(i);
            Ventadetalle ventadetalle = new Ventadetalle();
            ventadetalle.setProducto(producto); 
            ventadetalle.setCantidad(2000);
            detalles.add(ventadetalle);
        }
        Response responseOrdenVentaError = pruebaGenerarPedido("Prueba generar pedido con productos insuficientes", detalles);
        if(responseOrdenVentaError.getStatus()==200){
            Ordenventa ordenventa = responseOrdenVentaError.readEntity(Ordenventa.class);
        }else{
            System.out.println(responseOrdenVentaError.readEntity(String.class));
            for(Ventadetalle ventadetalle: detalles){
                ventadetalle.setCantidad(20);
            }
            Response responseOrdenVenta = pruebaGenerarPedido("Prueba generar pedido con productos disponibles", detalles);
            if(responseOrdenVenta.getStatus()==200){
                Ordenventa ordenventa = responseOrdenVenta.readEntity(Ordenventa.class);
                System.out.println("Agregando detalles al pedido...");
                ordenventa.setVentadetalleCollection(detalles);
                Response responseDetalles = APIConsumer.agregarDetallesAlPedido(ordenventa);
                System.out.println("Respuesta: "+responseDetalles.getStatus());
                if(responseDetalles.getStatus()==200){
                    System.out.println("Concluyendo pedido...");
                    Response responseConcluir = APIConsumer.concluirPedido(ordenventa);
                    System.out.println("Respuesta: "+responseConcluir.getStatus());
                }
            }
        }
    }
    
    public static Response pruebaGenerarPedido(String descripcion, ArrayList<Ventadetalle> ventadetalleList) {
        Ordenventa ordenventa = new Ordenventa();
        Cliente cliente = new Cliente();
        cliente.setEmail("compras@walmart.com.mx");
        ordenventa.setClienteid(cliente);
        ordenventa.setDescripcion(descripcion);
        if(ventadetalleList!=null){
            ordenventa.setVentadetalleCollection(ventadetalleList);
        }
        System.out.println("Realizando pedido...");
        Response responseOrdenVenta = APIConsumer.realizarPedido(ordenventa);
        System.out.println("Respuesta: "+responseOrdenVenta.getStatus());
        return responseOrdenVenta;
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
