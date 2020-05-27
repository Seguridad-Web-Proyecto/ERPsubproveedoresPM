/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication.service;

import entidades.Facturaventa;
import entidades.Inventario;
import entidades.Ordenventa;
import entidades.Producto;
import entidades.Ventadetalle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author jcami
 */
public class WebServicesUtils {
    
    public static Facturaventa emitirFactura(Ordenventa ordenventa){
        Facturaventa facturaVenta = new Facturaventa();
        facturaVenta.setDescripcion("Factura de las ordenes de compra de "+ordenventa.getClienteid().getEmpresa());
        Date fechaActual  = new Date();
        Date fechaPago = addMonthToDate(fechaActual, 2);
        facturaVenta.setFechaEmision(fechaActual);
        facturaVenta.setFechaVencimientoPago(fechaPago);
        List<Ordenventa> ordenes = new ArrayList<>();
        ordenes.add(ordenventa);
        facturaVenta.setOrdenventaCollection(ordenes);
        return facturaVenta;
    }
    
    public static Date addMonthToDate(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }
    
    
}
