/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import java.util.Date;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface OfferedChangesManagerLocal {
    
    public void createPriceChangeOffer(Long orderId, double price); 
    
    public void createDeadlineChangeOffer(Long orderId, Date deadline);
    
    public void approveDeadlineChangeOffer(Long ocId);
    
}
