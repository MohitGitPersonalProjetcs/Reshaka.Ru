/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.CommentManagerLocal;
import entity.Comment;
import entity.User;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import web.utils.HttpUtils;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ShowCommentsBean {

    @EJB
    CommentManagerLocal commMan;

    private Long id;

    private String text;

    @PostConstruct
    private void init() {
        id = null;
        if (HttpUtils.getRequestUrl().indexOf("profile") > 0) {
            String sid = HttpUtils.getRequestParam("id");
            Long mid = null;
            try {
                mid = Long.parseLong(sid);
                id = mid;
                return;
            } catch (Exception e) {
            }
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            id = user.getId();
        }

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Comment> getComments() {
        return commMan.getCommentsByOwnerId(id);
    }

    public void addComment(Long authorId) {
        System.out.println("adding comment ");
        Comment c = commMan.addComment(id, authorId, this.text);
        FacesContext fc = FacesContext.getCurrentInstance();
        if (c == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Комментарий не добавлен"));
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Информация", "Комментарий добавлен"));
        }
    }
    
    public void deleteComment(Long commentId){
        System.out.println("try to delete comment id = " + commentId);
        commMan.deleteComment(commentId);
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"info","Комментарий удален"));
        
    }
}
