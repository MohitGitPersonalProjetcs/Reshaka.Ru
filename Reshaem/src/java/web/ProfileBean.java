package web;

import ejb.AttachmentManager;
import ejb.OrderManagerLocal;
import ejb.UserManagerLocal;
import entity.Attachment;
import entity.OpenId;
import entity.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import web.utils.HttpUtils;
import web.utils.SessionListener;

/**
 * This bean is needed for changing user personal data
 * <p/>
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
@ManagedBean
@ViewScoped
public class ProfileBean {

    @EJB
    UserManagerLocal userMan;

    @EJB
    OrderManagerLocal orderMan;
    
    private User user;

    private OpenId openId;

    private Long id;

    @PostConstruct
    private void init() {
        System.out.println("profileBean: postconstruct");
        User user1 = (User) SessionListener.getSessionAttribute("user", true);
        if (user1 == null) {
            this.openId = new OpenId();
            return;
        }

        User u2 = null;

        if (HttpUtils.getRequestParam("id") != null) {
            id = Long.parseLong(HttpUtils.getRequestParam("id"));
            u2 = userMan.getUserById(id);
        }

        if (u2 != null) {
            this.user = u2;
            this.openId = userMan.getOpenIdByUserId(id);
            return;
        }

        id = user1.getId();
        this.user = userMan.getUserById(id);
        System.out.println("loadFromSession in profileBean user id = " + id);
        //this.openId = new OpenId();
        this.openId = userMan.getOpenIdByUserId(id);
        System.out.println("openId is " + openId);
    }

    public boolean canViewCommentsTab() {
        if (readOnlyComments()) {
            return true;
        }
        User us = userMan.getUserById(id);
        if (us != null) {
            if (us.getUserGroup() == 3) {
                System.out.println("can view comments tab");
                return true;
            }
        }

        return false;
    }

    public boolean readOnlyComments() {
        User us = userMan.getUserById(id);
        User user1 = (User) SessionListener.getSessionAttribute("user", true);
        if ((user1 != null) && (us != null)) {
            if ((user1.getId().equals(this.id)) && (us.getUserGroup() == 3)) {
                System.out.println("read only comments");
                return true;
            }
        }
        return false;
    }

    public int getSolvedAmount() {
//        return (user != null) ? orderMan.getSolvedOrdersAmountOfReshaka(user.getId()) : -1;
        
        return (user != null) ? user.getSolvedAmount() : -1;
    }

    public int getOrderedAmount() {
        return (user != null) ? user.getOrderedAmount() : -1;
    }

    public int getUserGroup() {
        return (user != null) ? user.getUserGroup() : 0;
    }

    public Date getRegistrationDate() {
        return (user != null) ? user.getRegistrationDate() : null;
    }

    public Date getLastActivityDate() {
        return (user != null) ? user.getLastActivityDate() : null;
    }

    public String getEducation() {
        return (user != null) ? user.getEducation() : null;
    }

    public String getCity() {
        return (user != null) ? user.getCity() : null;
    }

    public String getAge() {
        return (user != null) ? user.getAge() : null;
    }

    public Long getUserId() {
        return user == null ? null : this.user.getId();
    }

    public String getLogin() {
        return user != null ? user.getLogin() : null;
    }

    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : "-";
    }

    public String getPhone() {
        return (user != null && user.getPhone() != null && !user.getPhone().equals("")) ? user.getPhone() : "-";
    }

    public String getIcq() {
        return (user != null && user.getIcq() != null && !user.getIcq().equals("")) ? user.getIcq() : "-";
    }

    public String getSkype() {
        return (user != null && user.getSkype() != null && !user.getSkype().equals("")) ? user.getSkype() : "-";
    }

    public String getAdditionalInformation() {
        return user != null ? user.getAdditionalInformation() : "-";
    }

    public String getAdditionalContacts() {
        return user != null ? user.getAdditionalContacts() : "-";
    }

    public StreamedContent getCurrentAvatar() {
        if (user == null || user.getAvatarId() == null) {
            return null;
        }
        Attachment avatar = userMan.getUserAvatar(user.getAvatarId());
        if (avatar == null) {
            System.out.println("Avatar == null");
            return null;
        }
        try {
            FileInputStream img = new FileInputStream(new File(AttachmentManager.DEFAULT_UPLOAD_DIRECTORY, avatar.getFileName()));
            return new DefaultStreamedContent(img, avatar.getMimeType(), avatar.getName());
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    public Long getId() {
        return id;
    }
}
