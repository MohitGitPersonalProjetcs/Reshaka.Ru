package web.money;

import ejb.MoneyManagerLocal;
import entity.User;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ReplenishmentBean implements Serializable {

    private transient HttpSession session = null;

    @EJB
    MoneyManagerLocal monMan;

    private Long userId;

    private double money;

    private String yandexLink;

    private String paymentSystem;

    private String test;

    private String getLinkForYandexPayment(Long userId, double money) {
        return monMan.getLinkForYandexPayment(userId, money);
    }

    public String getLinkForWebmoneyPayment() {
        return "https://merchant.webmoney.ru/lmi/payment.asp";
    }

    public String paymentLink(Long userId, double money) {
        if (money == 0.0) {
            return "";
        }
        return getLinkForYandexPayment(userId, money);
    }

    public boolean isSignedIn() {
        if (SessionListener.getSessionAttribute("user", true) != null) {
            return true;
        }
        return false;
    }

    public Long getId() {
        if (isSignedIn() == false) {
            return null;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        session = (HttpSession) context.getExternalContext().getSession(true);
        return ((User) SessionListener.getSessionAttribute("user", true)).getId();
    }

    public void test() {
        System.out.println("test///////////////...........///////////");
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(String paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getYandexLink() {
        return getLinkForYandexPayment(getId(), getMoney());
    }

    public void setYandexLink(String yandexLink) {
        this.yandexLink = yandexLink;
    }
}
