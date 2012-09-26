package web.orders;

import ejb.ConfigurationManagerLocal;
import ejb.DealManagerLocal;
import ejb.OrderManagerLocal;
import ejb.UserManagerLocal;
import entity.Offer;
import entity.Order;
import entity.User;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import web.utils.Tools;

/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class OrderUtilsBean {

    @EJB
    OrderManagerLocal orderMan;

    @EJB
    DealManagerLocal dealMan;

    @EJB
    UserManagerLocal userMan;

    @EJB
    ConfigurationManagerLocal confMan;

    private Map<Integer, String> statusDescription;

    private Long currentOrderId;

    private double plug;

    public Long getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(Long currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public double getPlug() {
        return plug;
    }

    public void setPlug(double plug) {
        this.plug = plug;
    }

    public OrderUtilsBean() {
    }

    @PostConstruct
    private void init() {
        statusDescription = confMan.getOrderStatusDescription();
    }

    public String statusDescription(int status) {
        try {
            return statusDescription.get(status);
        } catch (Exception e) {
        }
        return null;
    }

    public double minPrice() {
        if (currentOrderId == null) {
            return 0.0;
        }
        return orderMan.getMinPrice(currentOrderId);
    }

    public double minPrice(Long orderId) {
        if (orderId == null) {
            return 0.0;
        }
        double min = orderMan.getMinPrice(orderId);
        setPlug(min);
        return min;
    }

    public void deleteOrder(Long orderId) {
        orderMan.deleteOrder(orderId);
    }

    public void initOverlayPanel(Long orderId) {
        setCurrentOrderId(orderId);
        setPlug(minPrice());
        System.out.println("current order id is " + getCurrentOrderId());
    }

    
    
    public void addOffer(Long orderId, Long userId, double price) {
        System.out.println("plug is " + getPlug());
        System.out.println("Current order id is " + getCurrentOrderId());
        System.out.println("try to add offer orderId = " + orderId + " userID = " + userId + " price = " + price);
        FacesContext context = FacesContext.getCurrentInstance();

        orderMan.addOffer(orderId, userId, price);

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Уведомление.", "Заявка отправлена!"));
    }

    public void deleteOffer(Long offerId , Long orderId){
        //there is no cheking in EJB!!!
        System.out.println("deleteOffer(): offerId = " + offerId);
        orderMan.deleteOffer(offerId , orderId);
    }
    
    public List<User> employeesList(Long orderId) {
        return orderMan.getEmployees(orderId);
    }

//    public String employeesLinkName(Long orderId) {
//        List<String> list = employeesList(orderId);
//        if (list.size() == 1) {
//            return list.get(0);
//        }
//        if (list.size() > 1) {
//            return "список (" + Integer.toString(list.size()) + ")";
//        }
//
//        return "-";
//    }
    public String loginById(Long userId) {
        return orderMan.getLoginById(userId);
    }

    public ArrayList<Offer> offersByOrderId() {
        System.out.println("offerByOrderId occured" + 5 + 6);
        ArrayList<Offer> offers = new ArrayList();
        if (currentOrderId == null) {
            return offers;
        }
        System.out.println("currentOfferId is = " + getCurrentOrderId());
        ArrayList<Offer> list = new ArrayList(orderMan.getOffers(currentOrderId));
        return list;
    }

    public ArrayList<Offer> offersByOrderId(Long orderId) {
        System.out.println("offerByOrderId occured selectedBean.order.id = " + orderId);
//        ArrayList<Offer> offers = new ArrayList();
        ArrayList<Offer> list = new ArrayList(orderMan.getOffers(orderId));
        return list;
    }

    public String tooltipTextForRatedOrderStatus(Long orderId) {
        System.out.println("tooltipTextForRatedOrderStatus occured selectedBean.order.id = " + orderId);
        List<Offer> list = orderMan.getOffers(orderId);
        System.out.println("tooltipTextForRatedOrderStatus(orderId = " + orderId  +  "): list = " + list);
        String text = "Заказ оценен следующими решающими: ";
        int k = 0;
        for (Offer off : list) {
//            System.out.println("off = " + off);
            if (k == 0) {
                text = text + loginById(off.getUserId());
            } else {
                text = text+ ", " + loginById(off.getUserId());
            }
//            System.out.println("text = " + text);
            k++;
        }
        System.out.println("text = " + text);
        return text;
    }

    public String bracketsForRatedOrderStatus(Long orderId) {
        System.out.println("offerByOrderId occured selectedBean.order.id = " + orderId);
        List<Offer> list = orderMan.getOffers(orderId);
        String text = "(" + list.size() + ")";
        return text;
    }
//    public ArrayList<String> employeesList(Long orderId) {
//        return new ArrayList(employeesSet(orderId));
//    }

    public boolean offered(Long orderId, Long userId) {
        if (currentOrderId == null) {
            return false;
        }
        return orderMan.offerExists(currentOrderId, userId);
    }

    public String subjectById(Long subjectId) {
        return orderMan.getSubjectById(subjectId);
    }

    public String orderStatus(Long orderId) {
        return dealMan.getCurrentStatus(orderId);
    }

    public String russianOrderStatus(Long orderId) {
        return dealMan.getRussianCurrentStatus(orderId);
    }

    public User userById(Long userId) {
        System.out.println("userById() userId = : " + userId);
        User user = userMan.getUserById(userId);
        return user;
    }

    public void updateOrder(Order order) {
        System.out.println("updating order from OrderUtilsBean");
        order = orderMan.updateOrder(order.getId(), order);
        FacesContext fc = FacesContext.getCurrentInstance();
        if (order != null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Информация успешно обновлена"));
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ошибка обновления"));
        }
    }

    public int orderedAmountOfUser(Long userId) {
        return orderMan.getOrderedAmount(userId);
    }
    
    public int timePassed(Date date){
        return Tools.getPassedHours(date);
    }
    
}
