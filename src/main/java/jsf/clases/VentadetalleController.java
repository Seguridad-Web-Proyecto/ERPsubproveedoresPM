package jsf.clases;

import entidades.Ventadetalle;
import jsf.clases.util.JsfUtil;
import jsf.clases.util.JsfUtil.PersistAction;
import beans.sessions.VentadetalleFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("ventadetalleController")
@SessionScoped
public class VentadetalleController implements Serializable {

    @EJB
    private beans.sessions.VentadetalleFacade ejbFacade;
    private List<Ventadetalle> items = null;
    private Ventadetalle selected;

    public VentadetalleController() {
    }

    public Ventadetalle getSelected() {
        return selected;
    }

    public void setSelected(Ventadetalle selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getVentadetallePK().setProductoid(selected.getProducto().getProductoid());
        selected.getVentadetallePK().setVentaid(selected.getOrdenventa().getOrdenventaid());
    }

    protected void initializeEmbeddableKey() {
        selected.setVentadetallePK(new entidades.VentadetallePK());
    }

    private VentadetalleFacade getFacade() {
        return ejbFacade;
    }

    public Ventadetalle prepareCreate() {
        selected = new Ventadetalle();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("VentadetalleCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("VentadetalleUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("VentadetalleDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ventadetalle> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Ventadetalle getVentadetalle(entidades.VentadetallePK id) {
        return getFacade().find(id);
    }

    public List<Ventadetalle> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ventadetalle> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Ventadetalle.class)
    public static class VentadetalleControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VentadetalleController controller = (VentadetalleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "ventadetalleController");
            return controller.getVentadetalle(getKey(value));
        }

        entidades.VentadetallePK getKey(String value) {
            entidades.VentadetallePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entidades.VentadetallePK();
            key.setVentaid(Long.parseLong(values[0]));
            key.setProductoid(Long.parseLong(values[1]));
            return key;
        }

        String getStringKey(entidades.VentadetallePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getVentaid());
            sb.append(SEPARATOR);
            sb.append(value.getProductoid());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Ventadetalle) {
                Ventadetalle o = (Ventadetalle) object;
                return getStringKey(o.getVentadetallePK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ventadetalle.class.getName()});
                return null;
            }
        }

    }

}
