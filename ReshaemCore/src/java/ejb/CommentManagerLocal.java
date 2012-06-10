/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Comment;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface CommentManagerLocal {
    
    public List<Comment> getCommentsByOwnerId(Long ownerId);
    
    public Comment addComment(Long ownerId, Long authorId, String text);
    
    public void deleteComment(Long commentId);
    
}
