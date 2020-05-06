package jsf.clases;

import entidades.Tarjetacreditocompra;
import jsf.clases.util.JsfUtil;
import jsf.clases.util.JsfUtil.PersistAction;
import beans.sessions.TarjetacreditocompraFacade;

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

@Named("tarjetacreditocompraController")
@SessionScoped
public class TarjetacreditocompraController implements Serializable {

    @EJB
    private beans.sessions.TarjetacreditocompraFacade ejbFacade;
    private List<Tarjetacreditocompra> items = null;
    private Tarjetacreditocompra selected;

    public TarjetacreditocompraController() {
    }

    public Tarjetacreditocompra getSelected() {
        return selected;
    }

    public void setSelected(Tarjetacreditocompra selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TarjetacreditocompraFacade getFacade() {
        return ejbFacade;
    }

    public Tarjetacreditocompra prepareCreate() {
        selected = new Tarjetacreditocompra();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TarjetacreditocompraCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TarjetacreditocompraUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TarjetacreditocompraDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Tarjetacreditocompra> getItems() {
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

    public Tarjetacreditocompra getTarjetacreditocompra(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Tarjetacreditocompra> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Tarjetacreditocompra> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Tarjetacreditocompra.class)
    public static class TarjetacreditocompraControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TarjetacreditocompraController controller = (TarjetacreditocompraController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tarjetacreditocompraController");
            return controller.getTarjetacreditocompra(getKey(value));
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
            if (object instanceof Tarjetacreditocompra) {
                Tarjetacreditocompra o = (Tarjetacreditocompra) object;
                return getStringKey(o.getTarjetacreditocompraid());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Tarjetacreditocompra.class.getName()});
                return null;
            }
        }

    }

}
