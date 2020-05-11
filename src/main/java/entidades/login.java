/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import dao.UsuarioJpaController;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;
import jsf.clases.util.JsfUtil;

/**
 *
 * @author jaker
 */
@Named(value = "login")
@SessionScoped
public class login implements Serializable
{

    private String usuario, contrasenia;
    private String errorMessage;
    private int errorsCounter;

    public String getUsuario()
    {
        return usuario;
    }

    public void setUsuario(String usuario)
    {
        this.usuario = usuario;
    }

    public String getContrasenia()
    {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia)
    {
        this.contrasenia = contrasenia;
    }

    public String entrar()
    {

        if (usuario.equals("cargo") && contrasenia.equals("cargo"))
        {
            return "/pages/cargo/List";
        } else if (usuario.equals("categoria") && contrasenia.equals("categoria"))
        {
            return "/pages/categoria/List";
        } else if (usuario.equals("cliente") && contrasenia.equals("cliente"))
        {
            return "/pages/cliente/List";
        } else if (usuario.equals("compradetalle") && contrasenia.equals("compradetalle"))
        {
            return "/pages/compradetalle/List";
        } else if (usuario.equals("empleado") && contrasenia.equals("empleado"))
        {
            return "/pages/empleado/List";
        } else if (usuario.equals("facturacompra") && contrasenia.equals("facturacompra"))
        {
            return "/pages/facturacompra/List";
        } else if (usuario.equals("facturaventa") && contrasenia.equals("facturaventa"))
        {
            return "/pages/facturaventa/List";
        } else if (usuario.equals("ganancia") && contrasenia.equals("ganancia"))
        {
            return "/pages/ganancia/List";
        } else if (usuario.equals("historialtrabajo") && contrasenia.equals("historialtrabajo"))
        {
            return "/pages/historialtrabajo/List";
        } else if (usuario.equals("ordencompra") && contrasenia.equals("ordencompra"))
        {
            return "/pages/ordencompra/List";
        } else if (usuario.equals("ordenventa") && contrasenia.equals("ordenventa"))
        {
            return "/pages/ordenventa/List";
        } else if (usuario.equals("pagocompra") && contrasenia.equals("pagocompra"))
        {
            return "/pages/pagocompra/List";
        } else if (usuario.equals("pagoventa") && contrasenia.equals("pagoventa"))
        {
            return "/pages/pagoventa/List";
        } else if (usuario.equals("persona") && contrasenia.equals("persona"))
        {
            return "/pages/persona/List";
        } else if (usuario.equals("producto") && contrasenia.equals("producto"))
        {
            return "/pages/producto/List";
        } else if (usuario.equals("proveedor") && contrasenia.equals("proveedor"))
        {
            return "/pages/proveedor/List";
        } else if (usuario.equals("rol") && contrasenia.equals("rol"))
        {
            return "/pages/rol/List";
        } else if (usuario.equals("tarjetacreditocompra") && contrasenia.equals("tarjetacreditocompra"))
        {
            return "/pages/tarjetacreditocompra/List";
        } else if (usuario.equals("tarjetacreditoventa") && contrasenia.equals("tarjetacreditoventa"))
        {
            return "/pages/tarjetacreditoventa/List";
        } else if (usuario.equals("usuario") && contrasenia.equals("usuario"))
        {
            return "/pages/usuario/List";
        } else if (usuario.equals("usuariosw") && contrasenia.equals("usuariosw"))
        {
            return "/pages/usuariosw/List";
        } else if (usuario.equals("ventadetalle") && contrasenia.equals("ventadetalle"))
        {
            return "/pages/ventadetalle/List";
        } else if (usuario.equals("admin") && contrasenia.equals("admin"))
        {
            return "index";
        }
        JsfUtil.addSuccessMessage("Usuario o contraseña incorrectos. Verifique la información");
        return "login.xhtml";
    }

    public String login3()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try
        {
            request.login(usuario, contrasenia);
        } catch (ServletException ex)
        {
            System.out.println(ex.getMessage());
            if (ex.getMessage().contains("Login failed"))
            {
                errorMessage = "login.failed";
            }
            return "/faces/login.xhtml";
        }
        Principal user = request.getUserPrincipal();
//    setPersons(new UsersJpaController(utx,emf).findUsers(usuario));
//    context.getExternalContext().getSessionMap().put("persons", persons);

        if (request.isUserInRole("ADMINS"))
        {
            return "/secured/admin/menu.xhtml";
        } else if (request.isUserInRole("COMPRAS"))
        {
            return "/secured/compras/menu.xhtml";
        
        } else if (request.isUserInRole("USERS"))
        {
            return "/secured/user/menu.xhtml";
        }else
        {
            return "/faces/login.xhtml";
        }
        
    }
    
    public boolean isRolCompras()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        return request.isUserInRole("COMPRAS");
    }
    
    public boolean isRoUsers()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        return request.isUserInRole("USERS");
    }
    
    public boolean isRolAdmin()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        return request.isUserInRole("ADMINS");
    }

    public String logout()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try
        {
            externalContext.invalidateSession();
            request.logout();
        } catch (ServletException ex)
        {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }

        errorMessage = "";
        errorsCounter = 0;
        return "/login.xhtml?faces-redirect=true";
    }

}
