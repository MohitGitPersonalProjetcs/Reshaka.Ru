package data;

import entity.User;
import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a user without its attachments, orders and other stuff
 *
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
public class SimpleUser implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String login;
    private String password;
    private double currentBalance;
    private String email;
    private String icq;
    private String skype;
    private String phone;
    private String additionalContacts;
    private String additionalInformation;
    private boolean online;
    private int unreadMessages;
    
    /**
     * userGroup - it's a status of user (1 - if he is the administrator; 2 - if
     * he is an employer; 3 - if he is a "reshaka")
     */
    private int userGroup;
    private Long avatarId;

    public SimpleUser() {
    }

    public SimpleUser(User u) {
        id = u.getId();
        avatarId = u.getAvatarId();
        login = u.getLogin();
        password = u.getPassword();
        phone = u.getPhone();
        skype = u.getSkype();
        email = u.getEmail();
        additionalContacts = u.getAdditionalContacts();
        additionalInformation = u.getAdditionalInformation();
        userGroup = u.getUserGroup();
        icq = u.getIcq();
        currentBalance = u.getCurrentBalance();
    }

    public String getAdditionalContacts() {
        return additionalContacts;
    }

    public void setAdditionalContacts(String additionalContacts) {
        this.additionalContacts = additionalContacts;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcq() {
        return icq;
    }

    public void setIcq(String icq) {
        this.icq = icq;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public int getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(int userGroup) {
        this.userGroup = userGroup;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleUser other = (SimpleUser) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
