/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.view.orders;

import web.utils.StringUtils;
import web.utils.HttpUtils;
import ejb.BusinessLogicLocal;
import ejb.DealManagerLocal;
import ejb.OrderManagerLocal;
import ejb.UserManagerLocal;
import entity.Order;
import entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import web.view.subjects.LazyOrderDataModel;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped // request scoped - нет проблем с апдейтом таблицы после добавления заказа, но не работает setPropertyActionListener
public class ShowOrdersBean {

    @EJB
    OrderManagerLocal orderMan;

    @EJB
    UserManagerLocal userMan;

    private static final Logger log = Logger.getLogger(ShowOrdersBean.class.getName());

    private LazyOrderDataModel<Order> lazyModel;

    private List<Order> orders;

    private int radioNumber = -1;

    private int orderType;

    private Long id;

    @PostConstruct
    private void init() {
        lazyModel = new LazyOrderDataModel(orderMan, userMan);
    }

    private int getCurrentRole() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return 0;
        }
        return user.getUserGroup();
    }

    public int getRadioNumber() {
        return radioNumber;
    }

    public void setRadioNumber(int radioNumber) {
        this.radioNumber = radioNumber;
    }

    public int getOrderType() {
        return lazyModel.getOrderType();
    }

    public void setOrderType(int orderType) {
        lazyModel.setOrderType(orderType);
    }

    public LazyOrderDataModel<Order> getLazyModel() {
        return lazyModel;
    }

    public String shortTags(String tags) {
        String s = StringUtils.getFirstWord(tags);
        if (tags.indexOf(",") > 0) {
            s = s + ",...";
        }
        return s;
    }

    public void radioListener() {
        lazyModel.setOrderType(this.radioNumber);
    }
}
