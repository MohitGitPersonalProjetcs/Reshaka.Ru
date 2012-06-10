/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Feedback;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface FeedbackManagerLocal {
    
    public void addFeedback(String author , String text);
    
    public void deleteFeedback(Long feedId);
    
    public List<Feedback> getAllFeedbacks();
}
