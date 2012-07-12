/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.view.orders;

import ejb.OrderManagerLocal;
import ejb.util.ReshakaSortOrder;
import entity.Order;
import entity.User;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.model.SortOrder;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ShowOrdersBeanVK {

    @EJB
    OrderManagerLocal orderMan;
    public static final int ORDERS_PER_PAGE = 7;
    private int page = 0;
    private Long userId;
    private int ordersAmount;

    public ShowOrdersBeanVK() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        try {
            userId = user.getId();
        } catch (Exception e) {
            userId = null;
        }
    }

    @PostConstruct
    private void init() {
        this.ordersAmount = orderMan.getOrdersCount(null, null, -1, 0, 1000000, null, ReshakaSortOrder.UNSORTED);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getOrdersAmount() {
        return ordersAmount;
    }

    public void nextPage() {
        if (!shouldShowNextButton()) {
            return;
        }
        this.page += 1;
    }

    public boolean shouldShowNextButton() {
        int r;
        System.out.println("this.ordersAmount = " + this.ordersAmount);
        if (this.ordersAmount % ShowOrdersBeanVK.ORDERS_PER_PAGE == 0) {
            r = 0;
        } else {
            r = 1;
        }
        int k = this.ordersAmount / ShowOrdersBeanVK.ORDERS_PER_PAGE;
        int p = k + r - 1;
        System.out.println("p = " + p);
        if (this.page < p) {
            return true;
        } else {
            return false;
        }
    }

    public void prevPage() {
        if (this.page == 0) {
            return;
        }
        this.page -= 1;
    }

    public List<Order> getOrders() {
        if (userId == null) {
            return null;
        }
        List<Order> data = orderMan.getOrders(null, userId, -1, page * ShowOrdersBeanVK.ORDERS_PER_PAGE, ShowOrdersBeanVK.ORDERS_PER_PAGE, "id", ReshakaSortOrder.DESCENDING);
        return data;
    }

    //!!! delete it!
    public List<Order> getAllOrders() {
//        if (userId == null) {
//            return null;
//        }
        List<Order> data = orderMan.getOrders(null, null, -1, page * ShowOrdersBeanVK.ORDERS_PER_PAGE, ShowOrdersBeanVK.ORDERS_PER_PAGE, "id", ReshakaSortOrder.DESCENDING);
        return data;
    }
}
