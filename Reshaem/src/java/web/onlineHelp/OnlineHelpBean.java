/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.onlineHelp;

import ejb.OnlineHelpManagerLocal;
import entity.OnlineHelp;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class OnlineHelpBean {

    @EJB
    OnlineHelpManagerLocal onlineMan;

    private OnlineHelp selectedOnlineHelp;

    public OnlineHelpBean() {
        selectedOnlineHelp = new OnlineHelp();
    }

    public Long getSelectedId() {
        return (selectedOnlineHelp == null) ? null : selectedOnlineHelp.getId();
    }

    public void setSelectedDstAddr(Long selectedId) {
        if (selectedOnlineHelp != null) {
            selectedOnlineHelp.setId(selectedId);
        }
    }
    
    public String getSelectedDstAddr() {
        return (selectedOnlineHelp == null) ? null : selectedOnlineHelp.getDstAddress();
    }

    public void setSelectedDstAddr(String selectedDstAddr) {
        if (selectedOnlineHelp != null) {
            selectedOnlineHelp.setDstAddress(selectedDstAddr);
        }
    }

    public Date getSelectedEndDate() {
        return (selectedOnlineHelp == null) ? null : selectedOnlineHelp.getEndDate();
    }

    public void setSelectedEndDate(Date selectedEndDate) {
        if (selectedOnlineHelp != null) {
            selectedOnlineHelp.setEndDate(selectedEndDate);
        }
    }

    public String getSelectedSrcAddr() {
        return (selectedOnlineHelp == null) ? null : selectedOnlineHelp.getSrcAdderss();
    }

    public void setSelectedSrcAddr(String selectedSrcAddr) {
        if (selectedOnlineHelp != null) {
            selectedOnlineHelp.setSrcAdderss(selectedSrcAddr);
        }
    }

    public Date getSelectedStartDate() {
        return (selectedOnlineHelp == null) ? null : selectedOnlineHelp.getStartDate();
    }

    public void setSelectedStartDate(Date selectedStartDate) {
        if (selectedOnlineHelp != null) {
            selectedOnlineHelp.setStartDate(selectedStartDate);
        }
    }

    public String getSelectedType() {
        return (selectedOnlineHelp == null) ? null : selectedOnlineHelp.getType();
    }

    public void setSelectedType(String selectedType) {
        if (selectedOnlineHelp != null) {
            selectedOnlineHelp.setType(selectedType);
        }
    }

    public OnlineHelp getSelectedOnlineHelp() {
        return selectedOnlineHelp;
    }

    public void setSelectedOnlineHelp(OnlineHelp selectedOnlineHelp) {
        try {
            this.selectedOnlineHelp = onlineMan.getOnlineHelpById(selectedOnlineHelp.getId());
        } catch (Exception e) {
        }

    }

    public void initSelectedOnlineHelp() {
       // selectedOnlineHelp = new OnlineHelp();
        System.out.println("initSelectedOnlineHelp(): selectedOnlineHelp = " + selectedOnlineHelp);
    }

    public void addOnlineHelp() {
        try {
            System.out.println("addOnlineHelp() occured");
            onlineMan.addNewOnlineHelp(selectedOnlineHelp.getType(), selectedOnlineHelp.getSrcAdderss(), selectedOnlineHelp.getDstAddress(), selectedOnlineHelp.getStartDate(), selectedOnlineHelp.getEndDate());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "error", "не добавлено"));
            System.out.println("Error occured while adiing new OnlineHelp e = " + e.toString());
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "добавлено"));
    }

    public void updateOnlineHelp() {
        OnlineHelp help;

        try {
            help = onlineMan.updateOnlineHelp(selectedOnlineHelp);
            if (help == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "ошибка при обновлении"));
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Данные успешно обновлены"));
        } catch (Exception e) {
        }
    }
    
    public void deleteOnlineHelp(){
        System.out.println("selectedOnlineHelp  " + selectedOnlineHelp);
        System.out.println("deleteing onlineHelp id = " + selectedOnlineHelp.getId());
        onlineMan.deleteOnlineHelp(selectedOnlineHelp.getId());
    }
    
}
