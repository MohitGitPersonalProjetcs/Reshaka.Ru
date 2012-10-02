package ejb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 * A simple implementation of session container EJB. It is a Singleton EJB which
 * stores all active user session.
 *
 * @author Danon
 */
@Singleton
public class SessionManager implements SessionManagerLocal {

    private final Map<String, Long> sessions = new HashMap<String, Long>();
    private static Logger log = Logger.getLogger(SessionManager.class);
    
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;

    @Override
    public void addSession(String session, Long userId) {
        if(userId != null)
            sessions.put(session, userId);
        if (log.isDebugEnabled()) {
            log.debug("addSession(): Session added. userId = "+userId+" Online: " + sessions.size());
        }
    }

    @Override
    public void removeSession(String session) {
        Long id = sessions.remove(session);
        if(id != null) {
            Query q = em.createQuery("update User u set u.lastActivityDate = :currentDate where u.id = :id");
            q.setParameter("currentDate", new Date());
            q.setParameter("id", id);
            q.executeUpdate();
        }
        if (log.isDebugEnabled()) {
            log.debug("removeSession(): Session removed. Online: " + sessions.size());
        }
    }

    @Override
    public void removeAll() {
        sessions.clear();
        if (log.isDebugEnabled()) {
            log.debug("removeAll(): Cleared. Online: " + sessions.size());
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Set<String> getSessions() {
        if (log.isTraceEnabled()) {
            log.trace(">> getSessions() returns " + sessions);
        }
        return sessions.keySet();
    }

    @Override
    public boolean isOnline(Long id) {
        return sessions.values().contains(id);
    }
}
