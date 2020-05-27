/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.api_consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import entidades.Categoria;
import entidades.Cliente;
import entidades.Facturaventa;
import entidades.Ordenventa;
import entidades.Producto;
import entidades.Ventadetalle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.ws.rs.client.ClientBuilder;

import static restapplication.api_consumer.ClienteHTTP.peticionHttpGet;

/**
 *
 * @author jcami
 */
public class APIConsumer {
    
    private static final String pathProductos = "http://localhost:8080/ERPsubproveedoresPM/webresources/productos";
    private static final String pathCategorias = "http://localhost:8080/ERPsubproveedoresPM/webresources/categorias";
    
    private static final String USER_AGENT = "Mozilla/5.0";
    
    public static List<Producto> productos(String path){
        List<Producto> productoList = new ArrayList<>();
        String url = pathProductos+path;
        String respuesta = "";
        try {
            respuesta = peticionHttpGet(url);
            System.out.println("La respuesta es:\n" + respuesta);
            String jsonString = new String(respuesta.getBytes("ISO-8859-1"), "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            productoList = mapper.readValue(jsonString, new TypeReference<List<Producto>>(){});
            for(Producto p: productoList){
                System.out.println("-------------------");
                System.out.println("productoid: "+p.getProductoid());
                System.out.println("nombre: "+p.getNombre());
                System.out.println("descripcion: "+p.getDescripcion());
                System.out.println("unidad de medida: "+p.getUnidadMedida());
                System.out.println("categoría[ ");
                System.out.println("categoriaid: "+p.getCategoriaid());
                System.out.println("categoría nombre: "+p.getCategoriaid().getNombre());
                System.out.println("]\n-------------------");
            }
        } catch (Exception e) {
            // Manejar excepción
            e.printStackTrace();
        }
        return productoList;
    }
    
    public static Producto obtenerProductoXId(Long productoid){
        String url = pathProductos+"/"+productoid.toString();
        String respuesta = "";
        Producto producto= new Producto();
        try {
            respuesta = peticionHttpGet(url);
            System.out.println("La respuesta es:\n" + respuesta);
            String jsonString = new String(respuesta.getBytes("ISO-8859-1"), "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            producto = mapper.readValue(jsonString, new TypeReference<Producto>(){});
            System.out.println("-------------------");
            System.out.println("productoid: "+producto.getProductoid());
            System.out.println("nombre: "+producto.getNombre());
            System.out.println("descripcion: "+producto.getDescripcion());
            System.out.println("unidad de medida: "+producto.getUnidadMedida());
            System.out.println("categoría[ ");
            System.out.println("categoriaid: "+producto.getCategoriaid());
            System.out.println("categoría nombre: "+producto.getCategoriaid().getNombre());
            System.out.println("]\n-------------------");
        } catch (Exception e) {
            // Manejar excepción
            e.printStackTrace();
        }
        return producto;
    }
    
    public static List<Categoria> categorias(String path){
        List<Categoria> categoriaList = new ArrayList<>();
        String url = pathCategorias+path;
        String respuesta = "";
        try {
            respuesta = peticionHttpGet(url);
            System.out.println("La respuesta es:\n" + respuesta);
            String jsonString = new String(respuesta.getBytes("ISO-8859-1"), "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            categoriaList = mapper.readValue(jsonString, new TypeReference<List<Categoria>>(){});
            for(Categoria categoria: categoriaList){
                System.out.println("-------------------");
                System.out.println("categoría[ ");
                System.out.println("categoriaid: "+categoria.getCategoriaid());
                System.out.println("categoría nombre: "+categoria.getNombre());
                System.out.println("]\n-------------------");
            }
        } catch (Exception e) {
            // Manejar excepción
            e.printStackTrace();
        }
        return categoriaList;
    }
    
    public static Categoria obtenerCategoriaXId(Long categoriaid){
        String url = pathCategorias+"/"+categoriaid.toString();
        String respuesta = "";
        Categoria categoria = new Categoria();
        try {
            respuesta = peticionHttpGet(url);
            String jsonString = new String(respuesta.getBytes("ISO-8859-1"), "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            categoria = mapper.readValue(jsonString, new TypeReference<Categoria>(){});
            System.out.println("-------------------");
            System.out.println("categoria[ ");
            System.out.println("categoriaid: "+categoria.getCategoriaid());
            System.out.println("categoría nombre: "+categoria.getNombre());
            System.out.println("]\n-------------------");
        } catch (Exception e) {
            // Manejar excepción
            e.printStackTrace();
        }
        return categoria;
    }
    
    public static String productosCOUNT(){
        String url = pathProductos+"/count";
        String respuesta="";
        try{
            respuesta = peticionHttpGet(url);
        }catch (Exception e) {
            // Manejar excepción
            e.printStackTrace();
        }
        return respuesta;
    }
    
    public static String categoriasCOUNT(){
        String url = pathCategorias+"/count";
        String respuesta="";
        try{
            respuesta = peticionHttpGet(url);
        }catch (Exception e) {
            // Manejar excepción
            e.printStackTrace();
        }
        return respuesta;
    }
    
    private static final String URL_BASE = "http://localhost:8080/ERPsubproveedoresPM/webresources";
    private static WebTarget webTarget;
    private static Client clientHttp;
    private static Invocation.Builder invocationBuilder;
    
    public static Response realizarPedido(Ordenventa ordenventa){
        System.out.println("Realizando pedido...");
        clientHttp = ClientBuilder.newClient();
        webTarget = clientHttp.target(URL_BASE).path("/pedidos");
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(
                Entity.entity(ordenventa, MediaType.APPLICATION_JSON));
        System.out.println("Respuesta: "+response.getStatus());
        return response;
    }
    
    public static Response agregarDetallesAlPedido(Ordenventa ordenventa){
        if(ordenventa.getVentadetalleCollection()==null) return null;
        System.out.println("Agregando detalles al pedido...");
        clientHttp = ClientBuilder.newClient();
        webTarget = clientHttp.target(URL_BASE).path("/pedidos/detalles");
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.put(Entity.entity(ordenventa, 
                MediaType.APPLICATION_JSON));
        System.out.println("Respuesta: "+response.getStatus());
        return response;
    }
    
    public static Response concluirPedido(Ordenventa ordenventa){
        System.out.println("Concluyendo pedido...");
        clientHttp = ClientBuilder.newClient();
        webTarget = clientHttp.target(URL_BASE).path("/pedidos/solicitar");
        invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.post(Entity.entity(ordenventa, MediaType.APPLICATION_JSON));
        System.out.println("Respuesta: "+response.getStatus());
        return response;
    }
    
    public static Facturaventa generarPedidoCompleto(String descripcion, ArrayList<Ventadetalle> ventaDetalleList) throws Exception{
        Ordenventa ordenventa = new Ordenventa();
        Cliente cliente = new Cliente();
        cliente.setEmail("compras@walmart.com.mx");
        ordenventa.setClienteid(cliente);
        ordenventa.setDescripcion(descripcion);
        ordenventa.setVentadetalleCollection(ventaDetalleList);
        Response responseOrdenVenta = APIConsumer.realizarPedido(ordenventa);
        if(responseOrdenVenta.getStatus()!=200){
            String msg = responseOrdenVenta.readEntity(String.class);
            throw new Exception("Whoops!!. Error al realizar un pedido!\n"+msg);
        }
        // DETALLES
        Ordenventa ordenVentaResult = responseOrdenVenta.readEntity(Ordenventa.class);
        ordenVentaResult.setVentadetalleCollection(ventaDetalleList);
        Response responseDetalles = APIConsumer.agregarDetallesAlPedido(ordenVentaResult);

        if(responseDetalles.getStatus()!=200){
            throw new Exception("Whoops!!. Error al añadir los detalles al pedido!"); 
        }
        // CONLUYENDO PEDIDO Y RECIBIENDO LA FACTURA
        Response responseCompletarPedido = APIConsumer.concluirPedido(ordenVentaResult);
        Facturaventa facturaVenta = responseCompletarPedido.readEntity(Facturaventa.class);
        if(responseCompletarPedido.getStatus()!=200){
            throw new Exception("Whoops!!. Error al concluir el pedido!");
        }
        return facturaVenta;
    }
    
}
