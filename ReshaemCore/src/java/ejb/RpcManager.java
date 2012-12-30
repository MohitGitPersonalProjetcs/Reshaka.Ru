package ejb;

import ejb.util.rpc.Context;
import ejb.util.rpc.XmlRpcHandler;
import javax.ejb.Asynchronous;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.w3c.dom.Element;

/**
 *
 * @author danon
 */
@Stateless
public class RpcManager implements RpcManagerLocal {

    public static final String EXECUTE_METHOD_NAME = "execute";
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Element invokeMethodXml(Context ctx, String className, Element args) {
        try {
            Class<XmlRpcHandler> clazz = (Class<XmlRpcHandler>)Class.forName(className);
            XmlRpcHandler handler = clazz.newInstance();
            ctx.setParameter(Context.ENTITY_MANAGER, em);
            handler.init(ctx);
            return handler.execute(args);
        } catch (Exception ex) {
            System.err.println("invokeMethod(): invocation failed for "+className);
            ex.printStackTrace(System.err);
            throw new EJBException(ex);
        }
    }
    
    @Override @Asynchronous
    public void invokeMethodXmlAsync(Context ctx, String className, Element args) {
        try {
            invokeMethodXml(ctx, className, args);
        } catch (Exception ex) {
            System.err.println("invokeMethodAsync(): invocation failed for "+className);
        }
    }
}
