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
import entidades.Ventadetalle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jcami
 */
public class SolicitarPedidos {
    
    private static final String pathPedidos = "http://localhost:8080/ERPsubproveedoresPM/webresources/pedidos";

    public static void main(String[] args) {
        Ordenventa ordenventa = new Ordenventa();
        Cliente cliente = new Cliente();
        cliente.setArea("Inventarios");
        cliente.setDomicilioFiscal("Nextengo 78 Santa Cruz Acayucan, Azcapotzalco, C.P. 02770, México D.F.");
        cliente.setEmail("compras@walmart.com.mx");
        cliente.setEmpresa("walmart");
        cliente.setNombreContacto("Administrador. Federico Gonzales");
        cliente.setRfc("MELM8305281H0");
        cliente.setTelefono("+52-722-098-5670");
        ordenventa.setClienteid(cliente);
        ordenventa.setDescripcion("Esta orden de venta presenta los productos que se están vendiendo a walmart méxico");
        Ordenventa pedidoIngresado = generarPedido(ordenventa);

        List<Ventadetalle> detalles = new ArrayList<>();
        //agregarDetallesAlPedido(ordenventa, detalles);
    }
    
    public static Ordenventa generarPedido(Ordenventa ordenventa){
        ObjectMapper mapper = new ObjectMapper();
        Ordenventa ordenIngresada = new Ordenventa();
        try { 
            String jsonStr = mapper.writeValueAsString(ordenventa);
            System.out.println(jsonStr);
            String jsonStringResult = ClienteHTTP.httpPOST(pathPedidos, jsonStr);
            ordenIngresada = mapper.readValue(jsonStringResult, new TypeReference<Ordenventa>(){});
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SolicitarPedidos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SolicitarPedidos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ordenIngresada;
    }
    
    public static List<Ventadetalle> agregarDetallesAlPedido(Ordenventa ordenventa, List<Ventadetalle> detalles){
        return null;
    }
    
}
