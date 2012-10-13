package web;

import ejb.RequestManagerLocal;
import ejb.UserManagerLocal;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ShowRequestsBean implements Serializable {

    @EJB
    RequestManagerLocal reqMan;

    @EJB
    UserManagerLocal userMan;

    private List<entity.Request> requests;

    @PostConstruct
    private void init() {
        this.requests = reqMan.getAllFreshRequests();
    }
    
    public int getRequestsNumber() {
        return requests.size();
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
