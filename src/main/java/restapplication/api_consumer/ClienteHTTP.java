/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.api_consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entidades.Categoria;
import entidades.Producto;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

/**
 *
 * @author David Villeda
 */
public class ClienteHTTP {

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

    public static String peticionHttpGet(String urlParaVisitar) throws Exception {
        // Esto es lo que vamos a devolver
        StringBuilder resultado = new StringBuilder();
        // Crear un objeto de tipo URL
        URL url = new URL(urlParaVisitar);
        // Abrir la conexión e indicar que será de tipo GET
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        // Búferes para leer
        BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        String linea;
        // Mientras el BufferedReader se pueda leer, agregar contenido a resultado
        while ((linea = rd.readLine()) != null) {
                resultado.append(linea);
        }
        // Cerrar el BufferedReader
        rd.close();
        // Regresar resultado, pero como cadena, no como StringBuilder
        
        return resultado.toString();
    }
    
    public static String httpPOST(String url, String jsonInputString) throws Exception{
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //Set the Request Method
        con.setRequestMethod("POST");
        //Set the Request Content-Type Header Parameter
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        //Set Response Format Type
        con.setRequestProperty("Accept", "application/json");
        //Ensure the Connection Will Be Used to Send Content
        con.setDoOutput(true);
        // Create the Request Body
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);           
        }
        // Read the Response from Input Stream
        StringBuilder response = new StringBuilder();
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }
        return response.toString();
    }
    
}
