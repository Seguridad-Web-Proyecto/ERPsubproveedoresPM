/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import static com.sun.org.apache.bcel.internal.classfile.Utility.toHexString;
import java.io.UnsupportedEncodingException;
import static java.lang.Integer.toHexString;
import static java.lang.Long.toHexString;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.swing.text.Utilities;
import jsf.clases.UsuarioController;
import static org.eclipse.persistence.internal.oxm.XPathFragment.CHARSET;
import static sun.security.pkcs11.wrapper.Functions.toHexString;

/**
 *
 * @author Dann
 */
public class Encoding
{

    public static String convertPass(String base)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex)
        {
            throw new RuntimeException(ex);

        }
    }

    
    
}
