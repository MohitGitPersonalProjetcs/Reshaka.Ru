package web;

import ejb.UserManagerLocal;
import entity.User;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import web.utils.SessionListener;
import web.utils.SessionUtils;

/**
 *
 * @author Danon and Sabir
 */
@ManagedBean
@SessionScoped
public class WebSession implements Serializable {

    @EJB
    UserManagerLocal um;

    private static Logger log = Logger.getLogger(WebSession.class);

    private transient HttpSession session = null;

    public boolean isSignedIn() {
        return SessionUtils.isSignedIn();
            
    }

    public String getLogin() {
        if (isSignedIn() == false) {
            return "you are guest! ";
        }
        return ((User) SessionListener.getSessionAttribute("user", true)).getLogin().toString();
    }

    public User getUser() {
        if (isSignedIn() == false) {
            return null;
        }
        return (User) SessionListener.getSessionAttribute("user", true);
    }

    public Long getId() {
        return SessionUtils.getId();
    }

    public String getRole() {
        String s = "";
        int a = getGroup();

        switch (a) {
            case 0:
                s = "guest";
                break;
            case 1:
                s = "admin";
                break;
            case 2:
                s = "user";
                break;
            case 3:
                s = "reshaka";
                break;
            case 4:
                s = "banned";
                break;
            case 5:
                s = "banned";
                break;
        }


        return s;
    }

    public int getGroup() {
        int a = 0;
        if (isSignedIn() == false) {
            return 0;
        } else {
            User u = (User) SessionListener.getSessionAttribute("user", true);
            if (u != null) {
                a = u.getUserGroup();
            }
        }
        return a;
    }

    public double getBalance() {
        if (isSignedIn() == false) {
            return 0.0;
        } else {
            return ((User) SessionListener.getSessionAttribute("user", true)).getCurrentBalance();
        }
    }

    public void updateParameters() {
        if (isSignedIn() == false) {
            return;
        }
        Long uid = ((User) SessionListener.getSessionAttribute("user", true)).getId();
        session = SessionListener.getCurrentSession(true);
        SessionListener.setSessionAttribute(session, "user", um.getUserById(uid));
    }

    public void resetSession() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession mysession = (HttpSession) fc.getExternalContext().getSession(true);
        mysession.invalidate();
        fc.getExternalContext().getSession(true);
    }

    public boolean thisId(String StringId) {
        return getId().equals(Long.parseLong(StringId));
    }
}
