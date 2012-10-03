package web.utils;

import ejb.FeedbackManagerLocal;
import ejb.MailManagerLocal;
import entity.Feedback;
import entity.User;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class FeedbacksBean implements Serializable {

    @EJB
    MailManagerLocal mailMan;

    @EJB
    FeedbackManagerLocal feedMan;

    private transient HttpSession session = null;

    private String username;

    private String text;

    private List<Feedback> feedbacks;

    public FeedbacksBean() {
        this.text = "";
        FacesContext fc = FacesContext.getCurrentInstance();
        session = (HttpSession) fc.getExternalContext().getSession(true);
        User user = ((User) session.getAttribute("user"));
        if (user != null) {
            this.username = user.getLogin();
        } else {
            this.username = "";
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addComment() {
        feedMan.addFeedback(this.username, this.text);
        mailMan.sendMailToAdmin("Отзыв о системе", text);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Отзыв добавлен", "Спасибо!"));
        
    }
}
