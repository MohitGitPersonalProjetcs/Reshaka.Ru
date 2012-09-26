/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import entity.Request;
import entity.User;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQueries;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author rogvold
 */
@Stateless
public class RequestManager implements RequestManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class);
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    @EJB
    ConfigurationManagerLocal confMan;
    @EJB
    UserManagerLocal userMan;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public void createRequest(String email, String additionalInfo, String additionalContacts) {
        Request req = new Request();
        req.setRequestDate(new Date());
        req.setEmail(email);
        req.setAdditionalInfo(additionalInfo);
        req.setAdditionalContacts(additionalContacts);
        req.setType(0);
        em.merge(req);
        if (log.isTraceEnabled()) {
            log.trace("createRequest(): created new request from user with email = " + email);
        }
    }

    @Override
    public void deleteRequest(Long requestId) {
        Request req = em.find(Request.class, requestId);
        em.remove(req);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public boolean canCreateRequest(String email) {
        Pattern p = Pattern.compile("[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]");
        Matcher m = p.matcher(email);
        if (m.matches() == false) {
            return false;
        }


        Query query = em.createNamedQuery("getRequestByEmail").setParameter("email", email);
        List list = query.getResultList();
        if (!list.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Request> getAllRequests(Long ownerId) {
        List<User> admins = userMan.getAdministrators();
        boolean b = false;
        for (User u : admins) {
            if (u.getId().equals(ownerId)) {
                b = true;
            }
        }
        Query q = em.createNamedQuery("getAllRequests");
        return q.getResultList();
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Request> getAllFreshRequests() {
        Query q = em.createNamedQuery("getAllFreshRequests").setParameter("type", 0);
        return q.getResultList();
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public String getEmailByRequestId(Long reqId) {
        Request request = em.find(Request.class, reqId);
        return request.getEmail();
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public void setViewedRequest(Long reqId) {
        Request req = em.find(Request.class, reqId);
        req.setType(1);
        em.merge(req);
    }
}
