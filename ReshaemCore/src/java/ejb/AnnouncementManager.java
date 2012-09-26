/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import entity.Announcement;
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
public class AnnouncementManager implements AnnouncementManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class);
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;

    @Override
    public void createAnnouncement(String theme, String text) {
        Announcement ann = new Announcement();
        ann.setText(text);
        ann.setTheme(theme);
        ann.setCreationDate(new Date());
        em.merge(ann);
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateAnnouncement(Long annId, String theme, String text) {
        Announcement ann = em.find(Announcement.class, annId);
        ann.setText(text);
        ann.setTheme(theme);
        em.merge(ann);
    }

    @Override
    public void deleteAnnouncement(Long annId) {
        Announcement ann = em.find(Announcement.class, annId);
        em.remove(ann);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Announcement> getAllAnnouncements() {

        Query q = em.createNamedQuery("getAllAnnouncements");
        List<Announcement> list = q.getResultList();
        if (log.isTraceEnabled()) {
            log.trace("getAllAnnouncements(): amount of news = " + list.size());
        }
        return list;
    }
}
