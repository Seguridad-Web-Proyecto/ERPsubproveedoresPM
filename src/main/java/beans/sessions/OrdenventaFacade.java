/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.sessions;

import entidades.Ordenventa;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Saul
 */
@Stateless
public class OrdenventaFacade extends AbstractFacade<Ordenventa> {

    @PersistenceContext(unitName = "com.mycompany_ERPsubprovee_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrdenventaFacade() {
        super(Ordenventa.class);
    }
    public List<Ordenventa> Listar(){
        Query q= em.createNativeQuery("SELECT ordenventaid, fecha_venta, status, iva, subtotal, total, descripcion, clienteid, facturaid\n" +
"	FROM public.ordenventa;",Ordenventa.class);
        List<Ordenventa> lst=q.getResultList();
        return lst;
     }

    
}
