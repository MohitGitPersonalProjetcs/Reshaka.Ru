/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.ReplenishmentRequest;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface ReplenishmentRequestManagerLocal {
 
    public void createReplenishmentRequest(Long userId, double money, String type, String text, Long attId);
    
    public void deleteReplenishmentRequest(Long reqId);
    
    public List<ReplenishmentRequest> getAllReplenishmentRequests();
            
    
}
