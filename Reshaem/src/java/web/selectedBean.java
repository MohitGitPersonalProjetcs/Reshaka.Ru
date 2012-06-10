package web;

import ejb.UserManagerLocal;
import entity.Order;
import entity.User;
import entity.UserSettings;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import web.utils.HttpUtils;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class selectedBean {

    @EJB
    UserManagerLocal userMan;

    public selectedBean() {
        order = new Order();
        user = new User();
        settings = new UserSettings();
    }
    private Order order;

    private User user;

    private UserSettings settings;

    private Long id;

    @PostConstruct
    private void init() {
        id = null;
        if (HttpUtils.getRequestUrl().indexOf("profile") > 0) {
            String sid = HttpUtils.getRequestParam("id");
            Long mid = null;
            try {
                mid = Long.parseLong(sid);
                id = mid;
                this.user = userMan.getUserById(id);
                return;
            } catch (Exception e) {
            }
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            id = user.getId();
            this.user = userMan.getUserById(id);
        }

    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        System.out.println("setUser occured user = " + user);
        this.user = user;
        this.settings = userMan.getUserSettings(user.getId());
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        System.out.println("selectedBean order = " + order);
        this.order = order;
    }
}
