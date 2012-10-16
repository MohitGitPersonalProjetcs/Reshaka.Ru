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
    private ReplenishmentRequestManagerLocal repMan;
    
    private List<ReplenishmentRequest> requests;
    
    @PostConstruct
    private void init() {
        requests = repMan.getAllReplenishmentRequests();
    }

    public List<ReplenishmentRequest> getReps() {
        return requests;
    }
    
    public int getAllRepNumber() {
        return requests.size();
    }
    
    public void deleteRep(Long repId){
        repMan.deleteReplenishmentRequest(repId);
        requests = repMan.getAllReplenishmentRequests();
    }
    
}
