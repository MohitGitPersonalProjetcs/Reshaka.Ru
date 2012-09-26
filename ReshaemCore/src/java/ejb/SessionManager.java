package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Singleton;
import org.apache.log4j.Logger;

/**
 * A simple implementation of session container EJB. It is a Singleton EJB which
 * stores all active user session.
 *
 * @author Danon
 */
@Singleton
public class SessionManager implements SessionManagerLocal {

    private final List<String> sessions = Collections.synchronizedList(new ArrayList<String>());
    private static Logger log = Logger.getLogger(SessionManager.class);

    @Override
    public void addSession(String session) {
        sessions.add(session);
        if (log.isDebugEnabled()) {
            log.debug("addSession(): Session added. Online: " + sessions.size());
        }
    }

    @Override
    public void removeSession(String session) {
        sessions.remove(session);
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
    public List<String> getSessions() {
        if (log.isTraceEnabled()) {
            log.trace(">> getSessions() returns " + sessions);
        }
        return sessions;
    }
}
