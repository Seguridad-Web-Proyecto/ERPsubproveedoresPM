package jsf.clases;

import entidades.Compradetalle;
import jsf.clases.util.JsfUtil;
import jsf.clases.util.JsfUtil.PersistAction;
import beans.sessions.CompradetalleFacade;

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

@Named("compradetalleController")
@SessionScoped
public class CompradetalleController implements Serializable {

    @EJB
    private beans.sessions.CompradetalleFacade ejbFacade;
    private List<Compradetalle> items = null;
    private Compradetalle selected;

    public CompradetalleController() {
    }

    public Compradetalle getSelected() {
        return selected;
    }

    public void setSelected(Compradetalle selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getCompradetallePK().setCompraid(selected.getOrdencompra().getOrdencompraid());
        selected.getCompradetallePK().setProductoid(selected.getProducto().getProductoid());
    }

    protected void initializeEmbeddableKey() {
        selected.setCompradetallePK(new entidades.CompradetallePK());
    }

    private CompradetalleFacade getFacade() {
        return ejbFacade;
    }

    public Compradetalle prepareCreate() {
        selected = new Compradetalle();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CompradetalleCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CompradetalleUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CompradetalleDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Compradetalle> getItems() {
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

    public Compradetalle getCompradetalle(entidades.CompradetallePK id) {
        return getFacade().find(id);
    }

    public List<Compradetalle> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Compradetalle> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Compradetalle.class)
    public static class CompradetalleControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CompradetalleController controller = (CompradetalleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "compradetalleController");
            return controller.getCompradetalle(getKey(value));
        }

        entidades.CompradetallePK getKey(String value) {
            entidades.CompradetallePK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new entidades.CompradetallePK();
            key.setCompraid(Long.parseLong(values[0]));
            key.setProductoid(Long.parseLong(values[1]));
            return key;
        }

        String getStringKey(entidades.CompradetallePK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getCompraid());
            sb.append(SEPARATOR);
            sb.append(value.getProductoid());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Compradetalle) {
                Compradetalle o = (Compradetalle) object;
                return getStringKey(o.getCompradetallePK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Compradetalle.class.getName()});
                return null;
            }
        }

    }

}
