package events;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Danon
 */
public class ReshakaEventAdapter implements ReshakaEventListener {

    @Override
    public void init() throws Exception {
        
    }

    @Override
    public void processEvent(ReshakaEvent evt) {
        
    }

    @Override
    public void detach() {
        
    }

    @Override
    public Set<Integer> getTypes() {
        Set<Integer> s = new HashSet<>();
        s.add(ReshakaEvent.TYPE_ANY);
        return s;
    }
    
}
