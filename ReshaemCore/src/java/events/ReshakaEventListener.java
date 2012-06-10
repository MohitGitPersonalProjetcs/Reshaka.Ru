package events;

import java.util.Set;

/**
 *
 * @author Danon
 */
public interface ReshakaEventListener {
    
    Set<Integer> getTypes();
    
    /**
     * Initialization of the event listener.
     * The method is invoked in EventManager#registerEvent method.
     * @throws Exception 
     */
    void init() throws Exception;
    
    /**
     * Main logic of event handler.
     * IMPORTANT: For long operations you should process event in a separate thread.
     * @param evt Event to be processed
     */
    void processEvent(ReshakaEvent evt);
    
    /**
     * The method is invoked in EventManager#unregisterListener method.
     */
    void detach() throws Exception;
}
