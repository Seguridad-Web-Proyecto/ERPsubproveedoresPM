/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restapplication;

import entidades.Facturaventa;
import entidades.Ordenventa;
import entidades.Pagoventa;
import entidades.Producto;
import entidades.Tarjetacreditoventa;
import entidades.Ventadetalle;
import entidades.VentadetallePK;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jcami
 */
public class Common {
    
    public static Producto aplicarGananciaAlProducto(Producto producto){
        int gananciaxproducto = ((int)producto.getGanancia().getPorcentaje())*(int)producto.getPrecioUnitario();
        gananciaxproducto /= 100;
        int nuevoPrecio = (int)producto.getPrecioUnitario() + gananciaxproducto;
        Producto productoQ = new Producto(producto.getProductoid(), producto.getNombre(), 
                producto.getDescripcion(), producto.getUnidadMedida(), nuevoPrecio);
        productoQ.setCategoriaid(producto.getCategoriaid());
        if(producto.getInventarioCollection()!=null){
            productoQ.setInventarioCollection(producto.getInventarioCollection());
        }
        return productoQ;
    }
    
    public static Tarjetacreditoventa limpiarTarjetaCredito(Tarjetacreditoventa tarjeta){
        Tarjetacreditoventa nuevaTarjeta = new Tarjetacreditoventa();
        nuevaTarjeta.setLugarFacturacion(tarjeta.getLugarFacturacion());
        String numeroStr = String.valueOf(tarjeta.getNumero());
        numeroStr = numeroStr.substring(numeroStr.length()-3);
        nuevaTarjeta.setNumero(new BigInteger(numeroStr));
        return nuevaTarjeta;
    }
    
    public static Pagoventa limpiarPagoVenta(Pagoventa pagoventa){
        Pagoventa pago = new Pagoventa(pagoventa.getPagoventaid(), pagoventa.getFechaPago(), pagoventa.getMonto(), pagoventa.getEstado());
        pago.setTarjetacreditoid(Common.limpiarTarjetaCredito(pagoventa.getTarjetacreditoid()));
        return pago;
    }
    
    public static Ordenventa limpiarOrdenVenta(Ordenventa ordenventa){
        Ordenventa returnOrden = new Ordenventa(ordenventa.getOrdenventaid(), 
                ordenventa.getFechaVenta(), ordenventa.getStatus(), ordenventa.getIva(),
                ordenventa.getSubtotal(), ordenventa.getTotal(), ordenventa.getDescripcion());
        List<Ventadetalle> ventadetalleList = new ArrayList<>();
        for(Ventadetalle vd: ordenventa.getVentadetalleCollection()){
            Producto p = Common.aplicarGananciaAlProducto(vd.getProducto());
            VentadetallePK detallePK = new VentadetallePK(vd.getVentadetallePK().getVentaid(),
                    vd.getVentadetallePK().getProductoid());
            Ventadetalle detalle =  new Ventadetalle(detallePK, vd.getCantidad(), p.getPrecioUnitario(), vd.getCantidad()*p.getPrecioUnitario());
            detalle.setProducto(p);
            ventadetalleList.add(detalle);
        }
        returnOrden.setClienteid(ordenventa.getClienteid());
        if(ordenventa.getFacturaid()!=null){
            returnOrden.setFacturaid(Common.limpiarFacturaVenta(ordenventa.getFacturaid()));
        }
        returnOrden.setVentadetalleCollection((ArrayList<Ventadetalle>) ventadetalleList);
        return returnOrden;
    }
    
    public static Facturaventa limpiarFacturaVenta(Facturaventa factura){
        Facturaventa returnFactura = new Facturaventa(factura.getFacturaventaid(), factura.getFechaEmision(), 
                factura.getFechaVencimientoPago(), factura.getDescripcion());
        if(factura.getPagoid()!=null){
            returnFactura.setPagoid(Common.limpiarPagoVenta(factura.getPagoid()));
        }
        return returnFactura;
    }
    
}
