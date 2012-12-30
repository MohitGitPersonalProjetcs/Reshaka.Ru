package ejb.util.rpc;

import ejb.MessageManagerLocal;
import ejb.util.EJBUtils;
import ejb.util.StringUtils;
import entity.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author danon
 */
public class RemoveMassgeXmlRpcHandler implements XmlRpcHandler {

    private static final Logger log = Logger.getLogger(RemoveMassgeXmlRpcHandler.class);
    
    private Context ctx;
    private EntityManager em;

    @Override
    public void init(Context ctx) {
        this.ctx = ctx;
        this.em = (EntityManager) ctx.getParameter(Context.ENTITY_MANAGER);
    }

    @Override
    public Element execute(Element args) {
        Document doc = StringUtils.createDocument();

        Element eResult = doc.createElement("Result");
        Element eStatus = doc.createElement("status");
        Element eStatusMsg = doc.createElement("statusMsg");

        eResult.appendChild(eStatus);
        eResult.appendChild(eStatusMsg);
        
        Long userId = (Long) ctx.getParameter(Context.USER_ID);
        if (userId == null) {
            eStatus.appendChild(doc.createTextNode("FAIL"));
            eStatusMsg.appendChild(doc.createTextNode("You are not authorized for this invocation."));
            log.debug("execute(): userId == null");
            return eResult;
        }
        
        try {
            List<Long> ids = parseMessageIds(args);
            
            MessageManagerLocal mm = EJBUtils.resolve("java:global/ReshaemEE/ReshaemCore/MessageManager!ejb.MessageManagerLocal", MessageManagerLocal.class);
            markMessagesRemoved(mm, userId, ids);

            eStatus.appendChild(doc.createTextNode("SUCCESS"));
            eStatusMsg.appendChild(doc.createTextNode("Messages have been marked removed."));
            
            for(Long id : ids) {
                Element eMessageId = doc.createElement("messageId");
                eMessageId.appendChild(doc.createTextNode(id.toString()));
                eResult.appendChild(eMessageId);
            }
            
            log.debug("execute(): messages have been marked deleted.");
        } catch (Exception ex) {
            eStatus.appendChild(doc.createTextNode("FAIL"));
            eStatusMsg.appendChild(doc.createTextNode("Internal server error."));
            log.error("execute(): Failed to execute operation.", ex);
        }
        return eResult;
    }
    
    private List<Long> parseMessageIds(Element args) {
        if(args == null)
            return Collections.EMPTY_LIST;
        
        NodeList ids = args.getElementsByTagName("MessageId");
        List<Long> result = new ArrayList<>(ids.getLength()+1);
        for(int i = 0; i < ids.getLength(); i++) {
            Long id = StringUtils.getValidLong(ids.item(i).getTextContent());
            if(id != 0)
                result.add(id);
        }
        return result;
    }

    private void markMessagesRemoved(MessageManagerLocal mm, long userId, List<Long> ids) {
        for(Long id : ids) {
            mm.markRemoved(userId, id, Message.REMOVED_BY_BOTH);
        }
    }
}
