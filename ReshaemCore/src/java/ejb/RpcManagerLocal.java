package ejb;

import ejb.util.rpc.Context;
import javax.ejb.Local;
import org.w3c.dom.Element;

/**
 *
 * @author danon
 */
@Local
public interface RpcManagerLocal {
    
    Element invokeMethodXml(Context ctx, String className, Element args);
    
    void invokeMethodXmlAsync(Context ctx, String className, Element args);
    
}
