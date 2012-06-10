/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import entity.ReplenishmentRequest;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author rogvold
 */
@Stateless
public class ReplenishmentRequestManager implements ReplenishmentRequestManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;

    @Override
    public void createReplenishmentRequest(Long userId, double money, String type, String text, Long attId) {
        ReplenishmentRequest req = new ReplenishmentRequest();
        req.setAttachmentId(attId);
        req.setCreationDate(new Date());
        req.setMoney(money);
        req.setType(type);
        req.setUserId(userId);
        req.setText(text);
        em.merge(req);
    }

    @Override
    public void deleteReplenishmentRequest(Long reqId) {
        ReplenishmentRequest req = em.find(ReplenishmentRequest.class, reqId);
        em.remove(req);
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public List<ReplenishmentRequest> getAllReplenishmentRequests() {
        Query q = em.createNamedQuery("getAllReplenishmentRequests");
        List<ReplenishmentRequest> list = q.getResultList();
        if (log.isTraceEnabled()) {
            log.trace("getAllReplenishmentRequests(): list = " + list);
        }
        return list;
    }
}
