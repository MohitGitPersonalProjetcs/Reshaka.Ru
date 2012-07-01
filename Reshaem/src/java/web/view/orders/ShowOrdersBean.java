package web.view.orders;

import ejb.OrderManagerLocal;
import ejb.UserManagerLocal;
import entity.Order;
import entity.User;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

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

    private LazyOrderDataModel<Order> lazyModel;

    private int radioNumber = -1;

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

    public String shortTags(Order order) {
        String tags = order.getSubject().getSubjectName();
//        String tags = order.getTags();
//        String s = StringUtils.getFirstWord(tags);
//        if (tags.indexOf(",") > 0) {
//            s = s + ",...";
//        }
        if (order.getTags() != null) {
            tags += ",...";
        }
        return tags;
    }

    public void radioListener() {
        lazyModel.setOrderType(this.radioNumber);
    }
}
