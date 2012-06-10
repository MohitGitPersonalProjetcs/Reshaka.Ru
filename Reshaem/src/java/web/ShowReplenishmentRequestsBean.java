/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;


import ejb.ReplenishmentRequestManagerLocal;
import entity.ReplenishmentRequest;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;



/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class ShowReplenishmentRequestsBean {
    @EJB
    ReplenishmentRequestManagerLocal repMan;

    public List<ReplenishmentRequest> getReps() {
        return repMan.getAllReplenishmentRequests();
    }
    
    public void deleteRep(Long repId){
        repMan.deleteReplenishmentRequest(repId);
    }
    
}
