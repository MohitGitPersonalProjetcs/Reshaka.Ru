/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.FeedbackManagerLocal;
import entity.Feedback;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class ShowFeedbacksBean implements Serializable{
    
    @EJB
    FeedbackManagerLocal feedMan;
   
    private List<Feedback> feedbacks ;

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }
    
    
    
    @PostConstruct
    private void init(){
        this.feedbacks = feedMan.getAllFeedbacks();
    }
    
    public void deleteFeedback(Long feedId){
        feedMan.deleteFeedback(feedId);
    }
    
}
