/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.RequestManagerLocal;
import ejb.UserManagerLocal;
import entity.Request;
import entity.User;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ShowRequestsBean implements Serializable {

    private transient HttpSession session = null;

    @EJB
    RequestManagerLocal reqMan;

    @EJB
    UserManagerLocal userMan;

    private List<entity.Request> requests;

    @PostConstruct
    private void init() {
        Long adId = ((User) SessionListener.getSessionAttribute("user",true)).getId();
        this.requests = reqMan.getAllFreshRequests();
    }

    public List<entity.Request> getRequests() {
        return requests;
    }

    public void registerReshaka(Long requestId) {
        String email = reqMan.getEmailByRequestId(requestId);
        userMan.registerReshaka(email);
        reqMan.setViewedRequest(requestId);
        this.requests = reqMan.getAllFreshRequests();
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Решака зареган","Ему на почту отправилось письмо с паролем для входа в систему"));
    }
}