package web;

import ejb.AttachmentManager;
import ejb.ConfigurationManagerLocal;
import ejb.UserManagerLocal;
import entity.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import web.utils.Tools;

/**
 * This bean is needed for changing user personal data
 * <p/>
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
@ManagedBean
@ViewScoped
public class EditProfileBean {

    @EJB
    UserManagerLocal um;

    @EJB
    ConfigurationManagerLocal confMan;

    private User user;

    private OpenId openId;

    private String oldPassword;

    private UploadedFile uploadedAvatar;

    private Long id;

    private UserSettings settings;

    private List<entity.Subject> subjects;

    private boolean newMessage;

    private boolean newStatus;

    /**
     * Creates a new instance of ProfileBean
     */
//    public EditProfileBean() {
//        loadFromSession(null);
//    }
    /**
     * Load all user profile from HTTP Session
     */
//    public final void loadFromSession(ActionEvent evt) {
//        System.out.println("profileBean: constructor");
//        this.user = null;
//        FacesContext fc = FacesContext.getCurrentInstance();
//        User user = (User) SessionListener.getSessionAttribute("user", true);
//        if (user == null) {
//            return;
//        }
//        this.user = new User();
//        this.user.setId(user.getId());
//        this.user.setLogin(user.getLogin());
//        this.user.setPassword(user.getPassword());
//        this.user.setEmail(user.getEmail());
//        this.user.setPhone(user.getPhone());
//        this.user.setIcq(user.getIcq());
//        this.user.setSkype(user.getSkype());
//        this.user.setAvatarId(user.getAvatarId());
//        this.user.setAdditionalContacts(user.getAdditionalContacts());
//        this.user.setAdditionalInformation(user.getAdditionalInformation());
//
//    }
    @PostConstruct
    private void init() {
        System.out.println("profileBean: postconstruct");
        User user1 = (User) SessionListener.getSessionAttribute("user", true);
        if (user1 == null) {
            this.openId = new OpenId();
            return;
        }

        String sid = null;
        Long tid = null;
//        if (user1.getId().equals(confMan.getMainAdminId())) {
        if(user1.getUserGroup() == User.ADMIN){
            sid = HttpUtils.getRequestParam("id");
            try {
                tid = Long.parseLong(sid);
            } catch (Exception e) {
                tid = null;
            }
        }

        id = (tid == null) ? user1.getId() : tid;

        // id = user1.getId();
        this.user = um.getUserById(id);
        System.out.println("loadFromSession in profileBean user id = " + id);
        //this.openId = new OpenId();
        this.openId = um.getOpenIdByUserId(id);
        System.out.println("openId is " + openId);

        this.settings = um.getUserSettingsByUserId(user.getId());
        this.subjects = um.getSubjectsByUserId(user.getId());
//        this.subjects = this.user.getSubjects();
    }

    public boolean isLoginUnchaged() {
        return user == null ? false : ("user" + user.getId()).equals(user.getLogin());
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    public boolean isNewStatus() {
        return newStatus;
    }

    public void setNewStatus(boolean newStatus) {
        this.newStatus = newStatus;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public List<entity.Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<entity.Subject> subjects) {
        this.subjects = subjects;
    }

    public Long getUserId() {
        return user == null ? null : this.user.getId();
    }

    public void setVkontakte(String vkontakte) {
        this.openId.setVkontakte(vkontakte);
    }

    public String getVkontakte() {
        return this.openId.getVkontakte();
    }

    public void setGmail(String gmail) {
        this.openId.setGmail(gmail);
    }

    public String getGmail() {
        return this.openId.getGmail();
    }

    public String getFacebook() {
        return this.openId.getFacebook();
    }

    public void setFacebook(String facebook) {
        this.openId.setFacebook(facebook);
    }

    public String getMailru() {
        return this.openId.getMailru();
    }

    public void setMailru(String mailru) {
        this.openId.setMailru(mailru);
    }

    public String getYandex() {
        return this.openId.getYandex();
    }

    public void setYandex(String yandex) {
        this.openId.setYandex(yandex);
    }

    public String getLogin() {
        return user != null ? user.getLogin() : null;
    }

    public void setLogin(String login) {
        if (user != null) {
            user.setLogin(login);
        }
    }

    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    public void setPassword(String password) {
        if (user != null) {
            user.setPassword(password);
        }
    }

    public String getEmail() {
        return user != null ? user.getEmail() : "-";
    }

    public void setEmail(String email) {
        if (user != null) {
            user.setEmail(email);
        }
    }

    public String getPhone() {
        return (user != null && user.getPhone() != null && !user.getPhone().equals("")) ? user.getPhone() : "-";
    }

    public void setPhone(String phone) {
        if (user != null) {
            user.setPhone(phone);
        }
    }

    public String getIcq() {
        return (user != null && user.getIcq() != null && !user.getIcq().equals("")) ? user.getIcq() : "-";
    }

    public void setIcq(String icq) {
        if (user != null) {
            user.setIcq(icq);
        }
    }

    public String getSkype() {
        return (user != null && user.getSkype() != null && !user.getSkype().equals("")) ? user.getSkype() : "-";
    }

    public void setSkype(String skype) {
        if (user != null) {
            user.setSkype(skype);
        }
    }

    public void setEducation(String education) {
        if (user != null) {
            user.setEducation(education);
        }
    }

    public String getEducation() {
        return (user != null && user.getEducation() != null && !user.getEducation().equals("")) ? user.getEducation() : "-";
    }

    public String getAdditionalInformation() {
        return user != null ? user.getAdditionalInformation() : "-";
    }

    public String getAge() {
        return (user != null && user.getAge() != null && !user.getAge().equals("")) ? user.getAge() : "-";
    }

    public void setAge(String age) {
        if (user != null) {
            user.setAge(age);
        }
    }

    public String getCity() {
        return (user != null && user.getCity() != null && !user.getCity().equals("")) ? user.getCity() : "-";
    }

    public void setCity(String city) {
        if (user != null) {
            user.setCity(city);
        }
    }

    public void setAdditionalInformation(String info) {
        if (user != null) {
            user.setAdditionalInformation(info);
        }
    }

    public String getAdditionalContacts() {
        return user != null ? user.getAdditionalContacts() : "-";
    }

    public void setAdditionalContacts(String contacts) {
        if (user != null) {
            user.setAdditionalContacts(contacts);
        }
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public StreamedContent getCurrentAvatar() {

        if (user == null || user.getAvatarId() == null) {

            return null;
        }

        System.out.println("getCurrentAvatar(): user = " + user + " / avatarId  = " + user.getAvatarId());

        Attachment avatar = um.getUserAvatar(user.getAvatarId());
        if (avatar == null) {
            System.out.println("Avatar == null");
            return null;
        }
        try {
            FileInputStream img = new FileInputStream(new File(AttachmentManager.DEFAULT_UPLOAD_DIRECTORY, avatar.getFileName()));
            try {
                System.out.println(img.getFD());
            } catch (IOException ex) {
                Logger.getLogger(EditProfileBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            return new DefaultStreamedContent(img, avatar.getMimeType(), avatar.getName());
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

//    private String getRandomImageName() {
//        int i = (int) (Math.random() * 10000000);
//        return String.valueOf(i);
//    }
    public void uploadAvatar(FileUploadEvent evt) {
        System.out.println("uploadAvatar occured");

        uploadedAvatar = evt.getFile();
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Avatar uploaded."));
    }

//    public void oncapture(CaptureEvent captureEvent) {
//        String photo = getRandomImageName();
//        this.photo = photo;
//        byte[] data = captureEvent.getData();
//
//        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//        String newFileName = servletContext.getRealPath("") + File.separator + photo + ".png";
//
//        FileImageOutputStream imageOutput;
//        try {
//            imageOutput = new FileImageOutputStream(new File(newFileName));
//            imageOutput.write(data, 0, data.length);
//            imageOutput.close();
//        } catch (Exception e) {
//            throw new FacesException("Error in writing captured image.");
//        }
//    }
    
    public void applyUploadedAvatar() {
        System.out.println("applyUploadedAvatar occured!!!");
        FacesContext fc = FacesContext.getCurrentInstance();
        this.user = um.updateAvatar(user.getId(), Tools.convertUploadedFile(uploadedAvatar));
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(true);
        SessionListener.setSessionAttribute(session, "user", user);
        //loadFromSession(evt);
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Avatar has been updated."));
    }

    public void applyPassword() {
        User u = um.updatePassword(user.getId(), oldPassword, getPassword());
        FacesContext fc = FacesContext.getCurrentInstance();
        if (u != null) {
            HttpSession session = (HttpSession) fc.getExternalContext().getSession(true);
            SessionListener.setSessionAttribute(session, "user", u);
            //loadFromSession(evt);
            this.user = u;
            RequestContext.getCurrentInstance().addCallbackParam("ok", true);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Password has been updated."));
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Password update failed."));
        }
    }

    public void apply() {
        // user.setSettings(settings);

        System.out.println("settings : newMessage/newStatus  = " + settings.isNewMessage() + "/" + settings.isNewStatus());

        user.setSubjects(subjects);
        User u = um.updateProfile(user, settings);
        FacesContext fc = FacesContext.getCurrentInstance();
        if (u != null) {
            HttpSession session = (HttpSession) fc.getExternalContext().getSession(true);
            SessionListener.setSessionAttribute(session, "user", u);
            // loadFromSession(evt);
            this.user = u;
            RequestContext.getCurrentInstance().addCallbackParam("ok", true);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Profile has been updated."));
        } else {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Profile update failed."));
        }
    }

    public void cancel() {
        this.user = um.getUserById(((User) SessionListener.getSessionAttribute("user", true)).getId());

        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Modifications canceled."));
        RequestContext.getCurrentInstance().addCallbackParam("ok", true);
    }
}
