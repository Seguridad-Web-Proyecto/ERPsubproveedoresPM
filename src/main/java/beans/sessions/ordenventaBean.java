/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.sessions;

import entidades.Ordenventa;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.ChartSeries;

/**
 *
 * @author gusta
 */
@Named(value = "ordenventaBean")
@ViewScoped
public class ordenventaBean {

    @EJB
    private OrdenventaFacade ordenvenFacade;
    private List<Ordenventa> listado;
    private BarChartModel barra;
    
    public ordenventaBean() {
    }
    public void listar(){
                listado=ordenvenFacade.Listar();
                graficar();
                
            }
    
      public void graficar(){
                barra=new BarChartModel();
                
                for (int i = 0; i <ordenvenFacade.Listar().size(); i++) {
                    ChartSeries serie=new BarChartSeries();
                    
                    serie.setLabel(ordenvenFacade.Listar().get(i).getDescripcion());
                    serie.set(ordenvenFacade.Listar().get(i).getFechaVenta(), ordenvenFacade.Listar().get(i).getTotal());
                    barra.addSeries(serie);
                    
                }
                
                barra.setTitle("Ventas del mes  ");
                barra.setLegendPosition("ne ");
                barra.setAnimate(true);
                Axis xAxis=barra.getAxis(AxisType.X);
                xAxis.setLabel("Estado ");
                Axis yAxis=barra.getAxis(AxisType.Y);
                xAxis.setLabel("monto ");
                yAxis.setMin(50);
                yAxis.setMin(500);
            }

    public List<Ordenventa> getListado() {
        return listado;
    }

    public void setListado(List<Ordenventa> listado) {
        this.listado = listado;
    }

    public BarChartModel getBarra() {
        return barra;
    }

    public void setBarra(BarChartModel barra) {
        this.barra = barra;
    }
      
      
    
}
