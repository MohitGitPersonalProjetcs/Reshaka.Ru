/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import entity.Feedback;
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
public class FeedbackManager implements FeedbackManagerLocal {
    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());
    
    @PersistenceContext(unitName="ReshaemCorePU")
    EntityManager em;
    
    @Override
    public void addFeedback(String author, String text) {
        if (log.isTraceEnabled()) {
            log.trace("addFeedback(): author/text : " + author + " / " + text);
        }
        Feedback fb = new Feedback();
        fb.setCreationDate(new Date());
        fb.setOwner(author);
        fb.setText(text);
        fb.setType(0);
        Feedback merge = em.merge(fb);
    }

    @Override
    public void deleteFeedback(Long feedId) {
        Feedback fb = em.find(Feedback.class, feedId);
        em.remove(fb);
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Feedback> getAllFeedbacks() {
        Query q = em.createNamedQuery("getAllFeedbacks") ;
        return (List<Feedback>)q.getResultList();
    }
    
    
}
