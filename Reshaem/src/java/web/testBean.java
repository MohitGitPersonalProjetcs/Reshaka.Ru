package web;

import ejb.MoneyManagerLocal;
import java.io.*;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author ASUS
 */
@ManagedBean
@RequestScoped
public class testBean implements Serializable {
    @EJB
    MoneyManagerLocal monMan;
    
    
    
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
    public void test(){
        monMan.testWM();
    }

    public testBean() {
        this.number = 1;
    }
    
}
