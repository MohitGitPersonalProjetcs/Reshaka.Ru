/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.OrderManagerLocal;
import entity.Order;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class orderEditorBean {
    @EJB 
    OrderManagerLocal orderMan;
    
    private Order order;

    public orderEditorBean() {
    }
    
    
    
}
