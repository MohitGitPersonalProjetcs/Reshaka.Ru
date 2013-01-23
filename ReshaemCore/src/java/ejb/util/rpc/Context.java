package ejb.util.rpc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author danon
 */
public class Context {
    
    public static final String ENTITY_MANAGER = "entity_manager";
    public static final String USER_ID = "user_id";

    private final Map<String, Object> params;
    
    public Context() {
        params = Collections.synchronizedMap(new HashMap<String, Object>());
    }
   
    public void setParameter(String name, Object value) {
        params.put(name, value);
    }
    
    public Object getParameter(String name) {
        return params.get(name);
    }
    
}