/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import entity.Comment;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author rogvold
 */
@Stateless
public class CommentManager implements CommentManagerLocal {
    
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    
    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public List<Comment> getCommentsByOwnerId(Long ownerId) {
        if (ownerId == null)  return null;
        
        Query q = em.createNamedQuery("getCommentsByOwnerId").setParameter("ownerId", ownerId);
        return q.getResultList();
    }
    
    @Override
    public Comment addComment(Long ownerId, Long authorId, String text) {
        Comment comment = new Comment();
        comment.setAuthorId(authorId);
        comment.setOwnerId(ownerId);
        comment.setText(text);
        comment.setType(0);
        comment.setCreationDate(new Date());
        return em.merge(comment);
    }
    
    @Override
    public void deleteComment(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        em.remove(comment);
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
