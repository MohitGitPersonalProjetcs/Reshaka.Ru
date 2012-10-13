package ejb;

import entity.Request;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for RequestManager EJB which is used for 
 * working with user "reshaka-requests".
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

    public int getFreshRequestsNumber();
}
