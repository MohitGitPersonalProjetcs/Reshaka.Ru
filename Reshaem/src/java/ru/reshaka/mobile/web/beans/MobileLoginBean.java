package ru.reshaka.mobile.web.beans;

import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author rogvold
 */
@ManagedBean
public class MobileLoginBean implements Serializable {

    public void loginRedirect(boolean signedIn) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (signedIn) {
            ExternalContext ext = fc.getExternalContext();
            ext.redirect("/mobile/index.xhtml");
        }
    }

    public void redirect(boolean signedIn) throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!signedIn) {
            ExternalContext ext = fc.getExternalContext();
            ext.redirect("/mobile/login.xhtml");
        }
    }
}
