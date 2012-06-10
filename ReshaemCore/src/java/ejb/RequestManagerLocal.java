/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Request;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface RequestManagerLocal {
    
    public void createRequest(String email, String additionalInfo, String additionalContacts);
    
    public void deleteRequest(Long requestId);
    
    public boolean canCreateRequest(String email);

    public List<Request> getAllRequests(Long ownerId);
    
    public List<Request> getAllFreshRequests();
    
    public String getEmailByRequestId(Long reqId);
    
    public void setViewedRequest(Long reqId);
}
