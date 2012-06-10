package events;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Danon
 */
public class EventManager {

    private static volatile EventManager instance;
    private Set<ReshakaEventListener> listeners;

    private EventManager() {
        listeners = Collections.synchronizedSet(new HashSet<ReshakaEventListener>());
    }

    public static EventManager getInstance() {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }

    void postEvent() {
        
    }

    public void processMessage(ReshakaEvent evt) {
        try {
            if (evt.getStatus() == ReshakaEvent.STATUS_DISABLED) {
                return;
            }

            for (ReshakaEventListener l : listeners) {
                try {
                    if (l.getTypes().contains(ReshakaEvent.TYPE_ANY) || l.getTypes().contains(evt.getType())) {
                        l.processEvent(evt);
                    }
                } catch (Exception ex) {
                    // listener couldn't process event
                }
            }
        } catch (Exception ex) {
            // unsupported message type or invalid message!
        }
    }

    public void registerListener(ReshakaEventListener lst) {
        if (lst == null) {
            return;
        }
        try {
            lst.init();
        } catch (Exception ex) {
            // listener initialization failed
            return;
        }
        listeners.add(lst);
    }

    public void unregisterListener(ReshakaEventListener lst) {
        try {
            lst.detach();
        } catch (Exception ex) {
            // exception!
        }
        listeners.remove(lst);
    }
}
