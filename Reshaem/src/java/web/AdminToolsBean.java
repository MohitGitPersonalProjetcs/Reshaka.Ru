/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.ConfigurationManagerLocal;
import ejb.MessageManagerLocal;
import ejb.MoneyManagerLocal;
import ejb.UserManagerLocal;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class AdminToolsBean {

    @EJB
    UserManagerLocal userMan;

    @EJB
    MoneyManagerLocal monMan;

    @EJB
    ConfigurationManagerLocal confMan;

    @EJB
    MessageManagerLocal messMan;
    
    private double plusMoney;

    private double minusMoney;
    
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    

    public double getMinusMoney() {
        return minusMoney;
    }

    public void setMinusMoney(double minusMoney) {
        this.minusMoney = minusMoney;
    }

    public double getPlusMoney() {
        return plusMoney;
    }

    public void setPlusMoney(double plusMoney) {
        this.plusMoney = plusMoney;
    }

    public void deleteUser(Long userId) {
        userMan.deleteUser(userId);
    }

    public void replenishMoney(Long userId, double money) {
        System.out.println("adminToolsaBean -> replenishMoney -> " + userId);
        monMan.replenishMoneyToAdmin(money);
        monMan.transferMoney(null, null, userId, money, "Ручное пополнение счета");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Счет пополнен", "Счет пополнен"));
    }

    public void withdrawMoney(Long userId, double money) {
        System.out.println("adminToolsaBean -> withdrawMoney -> " + userId);
        Long adminId = confMan.getMainAdminId();
        monMan.transferMoney(adminId, userId, adminId, money, "Ручное списание средств");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Деньги списаны", "Деньги списаны"));
    }
    
    public void sendMessage(Long toId, String message){
        Long adminId = confMan.getMainAdminId();
        messMan.sendMessage(adminId, toId,"Сообщение адмигимтрации сервиса.", message, null);
    }
    
    public void makeReshakaFromUser(Long userId){
        userMan.makeReshakaFromUser(userId);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Пользователь теперь решака", "Пользователь теперь решака"));
        messMan.sendMessage(confMan.getMainAdminId(),userId,"Уведомление","Вы теперь решающий", null);
    }
    
    public void banUser(Long userId){
        int gr = userMan.getGroupById(userId);
        if (gr == 2)
        userMan.banUser(userId);
        if (gr == 3)
            userMan.banReshaka(userId);
    }
    
//    public void 
}
