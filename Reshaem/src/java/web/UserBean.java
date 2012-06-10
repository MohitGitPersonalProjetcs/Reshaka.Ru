package web;

import ejb.AttachmentManagerLocal;
import ejb.MailManagerLocal;
import ejb.SubjectManagerLocal;
import ejb.UserManagerLocal;
import entity.Attachment;
import entity.Comment;
import entity.User;
import entity.UserSettings;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import web.utils.SessionListener;

/**
 * Contains user profile information which is stored in Entity class
 *
 * @author Danon
 */
@ManagedBean
@ViewScoped
public class UserBean implements Serializable {

    @EJB
    UserManagerLocal userMan;

    @EJB
    AttachmentManagerLocal attMan;

    @EJB
    SubjectManagerLocal subjMan;

    @EJB
    MailManagerLocal mailMan;

    private transient HttpSession session = null;

    /**
     * when we fill in the registration login field opposite the field cross or
     * tick appear <br/> so, pathToPicture is a path of tick or cross (it
     * depends on whether such user exists or not)
     */
    private String pathToPicture;

    private entity.User entity;

    private String comment;

    private boolean disableCookies;

    private List<String> selectedSubjects;

    private UserSettings settings;

    private boolean agreed;
    
    private String recoveryEmail;

    public static int COOKIE_AGE = 3600 * 24 * 366;

    public String getRecoveryEmail() {
        return recoveryEmail;
    }

    public void setRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    
    
    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
        entity = new entity.User();
        setPathToPicture("/images/void.jpg");
        settings = new UserSettings();
    }

    public UserBean(User entity) {
        this.entity = entity;
        settings = new UserSettings();
    }

    public List<String> getSelectedSubjects() {
        return selectedSubjects;
    }

    public void setSelectedSubjects(List<String> selectedSubjects) {
        this.selectedSubjects = selectedSubjects;
    }

    public String getAdditionalContacts() {
        return entity.getAdditionalContacts();
    }

    public void setAdditionalContacts(String additionalContacts) {
        entity.setAdditionalContacts(additionalContacts);
    }

    public final void setPathToPicture(String pathToPicture) {
        this.pathToPicture = pathToPicture;
    }

    public String getPathToPicture() {
        return pathToPicture;
    }

    public String getAdditionalInformation() {
        return entity.getAdditionalInformation();
    }

    public void setAdditionalInformation(String additionalInformation) {
        entity.setAdditionalInformation(additionalInformation);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEducation() {
        return entity.getEducation();
    }

    public void setEducation(String education) {
        entity.setEducation(education);
    }

    public Long getAvatarId() {
        return entity.getAvatarId();
    }

    public void setAvatarId(Long avatarId) {
        entity.setAvatarId(avatarId);
    }

    public double getCurrentBalance() {
        return entity.getCurrentBalance();
    }

    public void setCurrentBalance(double currentBalance) {
        entity.setCurrentBalance(currentBalance);
    }

    public String getEmail() {
        return entity.getEmail();
    }

    public void setEmail(String email) {
        entity.setEmail(email);
    }

    public String getIcq() {
        return entity.getIcq();
    }

    public void setIcq(String icq) {
        entity.setIcq(icq);
    }

    public long getId() {
        return entity.getId();
    }

    public void setId(long id) {
        entity.setId(id);
    }

    public String getLogin() {
        return entity.getLogin();
    }

    public void setLogin(String login) {
        entity.setLogin(login);
    }

    public String getPassword() {
        return entity.getPassword();
    }

    public void setPassword(String password) {
        entity.setPassword(password);
    }

    public String getPhone() {
        return entity.getPhone();
    }

    public void setPhone(String phone) {
        entity.setPhone(phone);
    }

    public int getRole() {
        return entity.getUserGroup();
    }

    public void setRole(int role) {
        entity.setUserGroup(role);
    }

    public String getSkype() {
        return entity.getSkype();
    }

    public void setSkype(String skype) {
        entity.setSkype(skype);
    }

    public boolean isDisableCookies() {
        return disableCookies;
    }

    public void setDisableCookies(boolean disableCookies) {
        this.disableCookies = disableCookies;
    }

    /**
     * Registers the underlying user entity
     */
    public void register(ActionEvent actionEvent) {
        entity.setUserGroup(2);

        System.out.println("Entity : " + entity);

        User user = userMan.register(entity);

        System.out.println("registeer: user = " + user);
        FacesContext context = FacesContext.getCurrentInstance();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        //  List<User> users = em.createNamedQuery("findByLogin").setParameter("userLogin", getLogin()).getResultList();
        if (user != null) {
            System.out.println("No users with this login");
            // 
        } else {
            System.out.println("FAIL");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка регистрации!", "Пользователь с данным именем уже есть в системе!"));
            return;
        }
        logIn(actionEvent);
        System.out.println("Registration: " + user.toString());
        System.out.println("success");
    }

    public void registerByEmail(ActionEvent actionEvent) {
        entity.setUserGroup(2);
        User user = userMan.registerByEmail(entity);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext context = FacesContext.getCurrentInstance();
        if (user == null) {
            requestContext.addCallbackParam("registered", false);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка регистрации!", "Пользователь с данным email уже есть в системе либо вы не правильно вбили почту!"));
            return;
        }
        requestContext.addCallbackParam("registered", true);
        logInByEmail(actionEvent);
    }

    public void registerReshaka(ActionEvent actionEvent) {
        System.out.println("this.selectedSubjects = " + this.selectedSubjects);
        if (this.selectedSubjects != null) {
            entity.setUserGroup(2); // like user     
            entity.setSubjects(new ArrayList(subjMan.getSubjectListByStringList(this.selectedSubjects)));
        }

        User user = userMan.registerByEmail(entity);
        FacesContext context = FacesContext.getCurrentInstance();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        //  List<User> users = em.createNamedQuery("findByLogin").setParameter("userLogin", getLogin()).getResultList();
        if (user != null) {
            System.out.println("No users with this email");
        } else {
            System.out.println("FAIL");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка регистрации!", "Пользователь с данным именем уже есть в системе!"));
            return;
        }
        session = (HttpSession) context.getExternalContext().getSession(true);
        SessionListener.setSessionAttribute(session, "user", user);

        requestContext.addCallbackParam("loggedIn", user != null);
        System.out.println(user.toString());
        System.out.println("success");
    }

    public void logIn(ActionEvent actionEvent) {
        User user = userMan.logIn(entity.getLogin(), entity.getPassword());
        entity.setPassword(null);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (user != null) {
            session = (HttpSession) facesContext.getExternalContext().getSession(true);
//            if(session!=null) session.invalidate();
//            session = (HttpSession) facesContext.getExternalContext().getSession(true);
            SessionListener.setSessionAttribute(session, "user", user);
            if (!disableCookies) {
                resetCookies(entity.getLogin(), entity.getPassword());
            }
            // mailMan.sendMail("sha-sabir@yandex.ru","login", entity.getLogin());    
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка авторизации!", "Данные авторизации введены не корректно"));
            return;
        }
        requestContext.addCallbackParam("loggedIn", user != null);
        System.out.println("Login: " + user.toString());
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Авторизация", "Вы успешно авторизованы!"));
    }

    public void logInByEmail(ActionEvent actionEvent) {
//        User user = userMan.logIn(entity.getLogin(), entity.getPassword());
        User user = userMan.logInByEmail(entity.getEmail(), entity.getPassword());

        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (user != null) {
            session = (HttpSession) facesContext.getExternalContext().getSession(true);
//            if(session!=null) session.invalidate();
//            session = (HttpSession) facesContext.getExternalContext().getSession(true);
            SessionListener.setSessionAttribute(session, "user", user);
            SessionListener.setSessionAttribute(session, pathToPicture, user);
            if (!disableCookies) {
                resetCookies(entity.getEmail(), entity.getPassword());
            }
            entity.setPassword(null);
            // mailMan.sendMail("sha-sabir@yandex.ru","login", entity.getLogin()); 
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка авторизации!", "Данные авторизации введены не корректно"));
            requestContext.addCallbackParam("loggedIn", user != null);
            return;
        }

        requestContext.addCallbackParam("loggedIn", user != null);
        System.out.println(user.toString());
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Авторизация", "Вы успешно авторизованы!"));
    }

    public void openIdAuthorisation() {
    }

    public String logOut(ActionEvent actionEvent) {
        entity = null;
        FacesContext context = FacesContext.getCurrentInstance();
        session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        context.getExternalContext().getSession(true);
        flushCookies();
        return "index2.xhtml?faces-redirect=true";
    }

    /**
     * Delete all login data from user cookies
     */
    private void flushCookies() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
        Cookie loginCookie = new Cookie("l", null);
        loginCookie.setMaxAge(3600 * 24 * 366);
        Cookie passwordCookie = new Cookie("p", null);
        passwordCookie.setMaxAge(3600 * 24 * 366);
        Cookie emailCookie = new Cookie("e", null);
        emailCookie.setMaxAge(3600 * 24 * 366);
        response.addCookie(loginCookie);
        response.addCookie(passwordCookie);
        response.addCookie(emailCookie);
    }

    private void resetCookies(String login, String password) {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
        Cookie loginCookie = new Cookie("l", login);
        loginCookie.setMaxAge(3600 * 24 * 366);
        Cookie passwordCookie = new Cookie("p", getMD5(password));
        passwordCookie.setMaxAge(3600 * 24 * 366);
        Cookie emailCookie = new Cookie("e", "true");
        emailCookie.setMaxAge(3600 * 24 * 366);
        response.addCookie(loginCookie);
        response.addCookie(passwordCookie);
        response.addCookie(emailCookie);
    }

    public boolean isValidEmail(String email) {
        return email.matches("[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]");
    }

    public void updatePicturePath() {
        if (getEmail().length() == 0) {
            setPathToPicture("/images/void.jpg");
            return;
        }
        if ((userMan.userExists(getEmail())) || (!isValidEmail(getEmail()))) {
            setPathToPicture("/images/error.jpg");
        } else {
            setPathToPicture("/images/ok.jpg;");
        }
    }

    public double userBalance(Long userId) {
        return userMan.getBalanceByUserId(userId);
    }

    public List<Attachment> allUserAttachments() {
        return attMan.getAttachmentsByUserId(getId());
    }

//    public void addComment(Long userId, Long authorId, String text) {
//        System.out.println("add comment JSF BEAN - userId, authorId, text " + userId + authorId + text);
//        userMan.addComment(userId, authorId, text);
//    }
//
//    public List<Comment> commentsOfUser(Long userId) {
//        System.out.println("get comments occured");
//        return userMan.getComments(userId);
//    }

    public void updateUserSettings(Long userId, UserSettings settings) {
        userMan.updateUserSettings(userId, settings);
    }

    public void testGetUrl() {
        System.out.println(FacesContext.getCurrentInstance().getViewRoot().getViewId());
        System.out.println(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath());
    }

    public String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte byteData[] = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception exc) {
        }

        return "";
    }

    public boolean canChangeNickname(Long userId) {
        return userMan.canChangeNickname(userId);
    }

    public int userGroupById(Long userId) {
        try {
            return userMan.getGroupById(userId);
        } catch (Exception exc) {
        }
        return 0;
    }
    
    public void recoverPassword(String email){
        FacesContext fc = FacesContext.getCurrentInstance();
        RequestContext rc = RequestContext.getCurrentInstance();
        if (userMan.userExists(email) == false) {
            System.out.println("such user does not exist");
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Ошибка", "Пользователь с данным email не зарегистрирован"));
            rc.addCallbackParam("recovery", false);
            return;
        }
        userMan.recoverPassword(email);
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Уведомление","Новый пароль выслан на почту " +email));
        rc.addCallbackParam("recovery", true);
    }
}