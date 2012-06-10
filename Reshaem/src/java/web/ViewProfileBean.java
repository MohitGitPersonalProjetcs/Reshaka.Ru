/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.UserManagerLocal;
import entity.User;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import web.utils.HttpUtils;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class ViewProfileBean {

    @EJB
    UserManagerLocal userMan;

    public User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ViewProfileBean() {
    }

    @PostConstruct
    private void init() {
        String ids = HttpUtils.getRequestParam("id");
        if (ids == null) {
            return;
        }
        Long id = Long.parseLong(ids);
        User us1 = userMan.getUserById(id);
        User us2 = (User) SessionListener.getSessionAttribute("user", true);

        if (us1 == null) {
            this.user = userMan.getUserById(us2.getId());
            return;
        }

        this.user = userMan.getUserById(id);
    }
}
