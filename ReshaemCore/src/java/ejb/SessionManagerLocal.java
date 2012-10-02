package ejb;

import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for SessionManager EJB.
 * @author Danon
 */

@Local
public interface SessionManagerLocal {
    
    void addSession(String sessionId, Long userId);
    void removeSession(String session);
    void removeAll();
    
    Set<String> getSessions();

    public boolean isOnline(Long id);
    
}
