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
import entidades.Producto;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

/**
 *
 * @author David Villeda
 */
class ClienteHTTP {
    
    public static void main(String[] args) {
        List<Producto> productos = productosProveedor();
    }
    
    public static List<Producto> productosProveedor(){
        List<Producto> productoList = new ArrayList<>();
        String url = "http://localhost:8080/ERPsubproveedoresPM/webresources/productos";
        String respuesta = "";
        try {
            respuesta = peticionHttpGet(url);
            System.out.println("La respuesta es:\n" + respuesta);
            String jsonString = new String(respuesta.getBytes("ISO-8859-1"), "UTF-8");
            //JsonObject jsonObject = new JsonParser().parse(respuesta).getAsJsonObject();
            /*JSONArray array = new JSONArray(respuesta);
            Gson gson = new Gson();
            for(int i=0; i<array.length(); i++){
                JSONObject jsonObject = array.getJSONObject(i);
                //System.out.println(jsonObject);
                Producto producto = gson.fromJson(jsonObject.toString(), Producto.class);
                System.out.println(producto);
            }*/
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
    
    public static Producto obtenerProductoXId(String productoid){
        String url = "http://localhost:8080/ERPsubproveedoresPM/webresources/productos";
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
}
