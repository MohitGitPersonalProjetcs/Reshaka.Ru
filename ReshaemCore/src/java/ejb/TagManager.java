/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Tag;
import java.util.ArrayList;
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
public class TagManager implements TagManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Tag addTag(String text) {
        if (tagExixts(text)) {
            if (log.isTraceEnabled()) {
                log.trace("addTag(): tag " + text + " already exists in database");
            }
            return getTagByText(text);
        }

        Tag tag = new Tag();
        tag.setText(text);
        tag.setType(0);
        em.persist(tag);
        if (log.isTraceEnabled()) {
            log.trace("addTag(): new tag = " + text + " added to database");
        }
        return tag;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public boolean tagExixts(String text) {
        Query q = em.createNamedQuery("getTagByText").setParameter("text", text);
        List l = q.getResultList();
        return (( l == null ) || (l.isEmpty())) ? false : true;
    }

    @Override
    public void deleteTag(Long tagId) {
        try {
            Tag tag = em.find(Tag.class, tagId);
            em.remove(tag);
        } catch (Exception e) {
        }

    }

    @Override
    public void deleteTag(String text) {
        Query q = em.createNamedQuery("getTagByText").setParameter("text", text);
        List l = q.getResultList();
        if (l == null) {
            return;
        }
        em.remove((Tag) l.get(0));
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Tag> completeTagList(String query) {
        if (log.isTraceEnabled()) {
            log.trace(">> completeTagList(): query = " + query);
        }
        List<Tag> lst = new ArrayList();
        if ("".equals(query)) {
            return lst;
        }

        lst = em.createQuery("SELECT t FROM Tag t WHERE t.text LIKE :query").setParameter("query", query + "%").getResultList();
        if (log.isTraceEnabled()) {
            log.trace("<< completeTagList(): lst = " + lst);
        }
        return lst;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Tag getTagByText(String text) {
        if (log.isTraceEnabled()) {
            log.trace("getTagByText(): text = " + text);
        }
        Query q = em.createNamedQuery("getTagByText").setParameter("text", text);
        List l = q.getResultList();
        return  (Tag) (((l == null) || (l.isEmpty() ) ) ? null : l.get(0));
    }

    @Override
    public void addTags(String query) {
        String[] strs = query.split("\\,");
        for (String s : strs) {
            Tag addTag = addTag(s.trim());
        }
    }
}
