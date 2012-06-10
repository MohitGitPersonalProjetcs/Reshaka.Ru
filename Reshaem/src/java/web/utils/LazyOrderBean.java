/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.utils;

import ejb.OrderManagerLocal;
import entity.Order;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class LazyOrderBean {

    @EJB
    OrderManagerLocal orderMan;

    LazyDataModel<Order> lazyModel;

    public LazyDataModel<Order> getLazyModel() {
        return lazyModel;
    }
    
    

    public LazyOrderBean() {
    }
    
    @PostConstruct
    private void init()
    {
       if (orderMan != null) System.out.println("Not null orderMan in LazyOrderBean!!");
       this.lazyModel = new LazyOrderDataModel(orderMan);
    }
    
}
