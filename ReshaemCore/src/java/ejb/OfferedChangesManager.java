package ejb;

import entity.OfferedChanges;
import entity.Order;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 *
 * @author rogvold
 */
@Stateless
public class OfferedChangesManager implements OfferedChangesManagerLocal {
    
    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    
    @EJB
    MessageManagerLocal messMan;
    
    @Override
    public void createPriceChangeOffer(Long orderId, double price) {
        //TODO(Sabir): email notification 
        //TODO(Sabir): message
        OfferedChanges oc = new OfferedChanges(price, orderId);
        em.persist(oc);
//        System.out.println("new offer for price change created; oc = " + oc);
    }
    
    @Override
    public void createDeadlineChangeOffer(Long orderId, Date deadline) {
        //TODO(Sabir): email notification 
        //TODO(Sabir): message
        OfferedChanges offC = new OfferedChanges(deadline, orderId);
        em.persist(offC);
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void approveDeadlineChangeOffer(Long ocId) {
        OfferedChanges oc = em.find(OfferedChanges.class, ocId);
        if (oc.getStatus() == OfferedChanges.STATUS_APPROVED){
            if (log.isTraceEnabled()) {
                log.trace("approveDeadlineChangeOffer(): is olready approved; ocId = " + ocId+"; orderId = " + oc.getOrderId());
            }
            return;
        }
        
        Order order = em.find(Order.class, oc.getOrderId());
        order.setDeadline(oc.getOfferedDate());
        //TODO: check if the order date is expired..
        em.merge(order);
        oc.setStatus(OfferedChanges.STATUS_APPROVED);
        oc.setConfirmationDate(new Date());
        em.merge(oc);
        //TODO: send message and email to employee
        
    }
}
