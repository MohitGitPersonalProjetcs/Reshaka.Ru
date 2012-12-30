package ejb.util.rpc;

import org.w3c.dom.Element;

/**
 * Generic interface for handling XML remote request handler.
 *  
 * @author danon
 */
public interface XmlRpcHandler {
    
    /**
     * Is invoked before execute()
     * @param ctx Context object that contains parameters about the environment (e.g. HttpServletRequest object). 
     */
    void init(Context ctx);
    
    Element execute(Element args);
    
}