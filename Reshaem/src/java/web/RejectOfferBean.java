/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.DealManagerLocal;
import ejb.OrderManagerLocal;
import entity.User;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import web.utils.HttpUtils;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class RejectOfferBean {

    @EJB
    DealManagerLocal dealMan;

    @EJB
    OrderManagerLocal orderMan;

    private Long orderId;

    private Long userId;

    private boolean canRejectOffer = false;

    public RejectOfferBean() {
        try {
            orderId = Long.parseLong(HttpUtils.getRequestParam("orderId"));
            userId = ((User) SessionListener.getSessionAttribute("user", false)).getId();
        } catch (Exception e) {
            orderId = null;
            canRejectOffer = false;
        }
    }
}
