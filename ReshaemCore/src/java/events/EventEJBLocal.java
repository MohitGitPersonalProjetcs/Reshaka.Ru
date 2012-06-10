package events;

import javax.ejb.Local;

/**
 *
 * @author Danon
 */
@Local
public interface EventEJBLocal {
     public void postEvent(ReshakaEvent evt);
}
