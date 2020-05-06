package jsf.clases;

import entidades.Tarjetacreditoventa;
import jsf.clases.util.JsfUtil;
import jsf.clases.util.JsfUtil.PersistAction;
import beans.sessions.TarjetacreditoventaFacade;

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

@Named("tarjetacreditoventaController")
@SessionScoped
public class TarjetacreditoventaController implements Serializable {

    @EJB
    private beans.sessions.TarjetacreditoventaFacade ejbFacade;
    private List<Tarjetacreditoventa> items = null;
    private Tarjetacreditoventa selected;

    public TarjetacreditoventaController() {
    }

    public Tarjetacreditoventa getSelected() {
        return selected;
    }

    public void setSelected(Tarjetacreditoventa selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TarjetacreditoventaFacade getFacade() {
        return ejbFacade;
    }

    public Tarjetacreditoventa prepareCreate() {
        selected = new Tarjetacreditoventa();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TarjetacreditoventaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TarjetacreditoventaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TarjetacreditoventaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Tarjetacreditoventa> getItems() {
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

    public Tarjetacreditoventa getTarjetacreditoventa(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Tarjetacreditoventa> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Tarjetacreditoventa> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Tarjetacreditoventa.class)
    public static class TarjetacreditoventaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TarjetacreditoventaController controller = (TarjetacreditoventaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tarjetacreditoventaController");
            return controller.getTarjetacreditoventa(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Tarjetacreditoventa) {
                Tarjetacreditoventa o = (Tarjetacreditoventa) object;
                return getStringKey(o.getTarjetacreditoventaid());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Tarjetacreditoventa.class.getName()});
                return null;
            }
        }

    }

}
