package ru.reshaka.mobile.web.beans;

import ejb.OrderManagerLocal;
import entity.Order;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class MobileOrderViewBean {

    private static final Logger log = Logger.getLogger(MobileOrderViewBean.class);

    private static final String ID_FIELD = "id";

    @EJB
    OrderManagerLocal orderMan;

    private Long orderId;

    private Order order;

    @PostConstruct
    private void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> map = fc.getExternalContext().getRequestParameterMap();
        String s = "";
        try {
            s = map.get(ID_FIELD);
            orderId = Long.parseLong(s);
            if (log.isTraceEnabled()) {
                log.trace("init(): orderId = " + orderId);
            }
            this.order = orderMan.getOrderById(orderId);
        } catch (Exception e) {
            orderId = null;
            order = null;
            if (log.isTraceEnabled()) {
                log.trace("init(): exception occured while receiving orderId: exc = " + e.toString() + " ; s = " + s);
            }
        }
    }

    public Long getOrderId() {
        return orderId;
    }

    public Order getOrder() {
        return order;
    }

    public Order orderById(Long oId) {
        try {
            return orderMan.getOrderById(oId);
        } catch (Exception e) {
            return null;
        }
    }
}
