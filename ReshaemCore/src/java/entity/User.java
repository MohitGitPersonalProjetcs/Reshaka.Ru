package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 * @author Sabir
 *
 * userGroup - it's a status of user (1 - if he is the administrator; 2 - if he
 * is an employer; 3 - if he is a "reshaka")
 */
@Entity
@Table(name = "Users")
@NamedQueries({
    @NamedQuery(name = "findByLoginAndPassword", query = "select u from User u where u.login=:userLogin and u.password=:userPassword"),
    @NamedQuery(name = "findByLogin", query = "select u from User u where u.login=:userLogin"),
    @NamedQuery(name = "findAdmins", query = "select u from User u where u.userGroup=1"),
    @NamedQuery(name = "findByEmail", query = "select u from User u where u.email=:userEmail"),
    @NamedQuery(name = "findByEmailAndPassword", query = "select u from User u where u.email=:userEmail and u.password=:userPassword")
})
public class User implements Serializable {

    public static final int USER = 2;
    public static final int RESHAKA = 3;
    public static final int ADMIN = 1;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(name = "login")
    private String login;
    @Column(name = "password")
    private String password;
    @Column(name = "current_balance")
    private double currentBalance;
    @Column(name = "email")
    private String email;
    @Column(name = "icq")
    private String icq;
    @Column(name = "skype")
    private String skype;
    @Column(name = "phone")
    private String phone;
    @Column(name = "additional_contacts")
    private String additionalContacts;
    @Column(name = "additional_information")
    private String additionalInformation;
    /**
     * userGroup - it's a status of user (1 - if he is the administrator; 2 - if
     * he is an employer; 3 - if he is a "reshaka")
     */
    @Column(name = "user_group")
    private int userGroup;
    @Column(name = "avatar_id")
    private Long avatarId;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "User_Attachments", joinColumns = {
        @JoinColumn(name = "user_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "attachment_id")
    })
    private Set<Attachment> attachments;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "User_transactions", joinColumns = {
        @JoinColumn(name = "user_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "transaction_id")
    })
    private Set<MoneyTransaction> transactions;
//    @OneToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "User_comments", joinColumns = {
//        @JoinColumn(name = "user_id")
//    },
//    inverseJoinColumns = {
//        @JoinColumn(name = "comment_id")
//    })
//    private Set<Comment> comments;
    @Column(name = "registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;
    @Column(name = "last_activity_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActivityDate;
    @Column(name = "last_notification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastNotificationDate;
    @Column(name = "ordered_amount")
    private int orderedAmount;
    @Column(name = "solved_amount")
    private int solvedAmount;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "User_subjects", joinColumns = {
        @JoinColumn(name = "user_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "subject_id")
    })
    private List<Subject> subjects;
    @Column(name = "education")
    private String education;
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_settings_id")
    private UserSettings settings;
    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private OpenId openId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "User_external_transactions", joinColumns = {
        @JoinColumn(name = "user_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "transaction_id")
    })
    private Set<ExternalMoneyTransaction> externalTransactions;
    @Column(name = "age")
    private String age;
    @Column(name = "city")
    private String city;

    public Set<ExternalMoneyTransaction> getExternalTransactions() {
        return externalTransactions;
    }

    public void setExternalTransactions(Set<ExternalMoneyTransaction> externalTransactions) {
        this.externalTransactions = externalTransactions;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public OpenId getOpenId() {
        return openId;
    }

    public void setOpenId(OpenId openId) {
        this.openId = openId;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

//    public Set<Comment> getComments() {
//        return comments;
//    }
//
//    public void setComments(Set<Comment> comments) {
//        this.comments = comments;
//    }
    public int getOrderedAmount() {
        return orderedAmount;

    }

    public void setOrderedAmount(int orderedAmount) {
        this.orderedAmount = orderedAmount;
    }

    public int getSolvedAmount() {
        return solvedAmount;
    }

    public void setSolvedAmount(int solvedAmount) {
        this.solvedAmount = solvedAmount;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<MoneyTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<MoneyTransaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public void setAdditionalContacts(String additionalContacts) {
        this.additionalContacts = additionalContacts;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIcq(String icq) {
        this.icq = icq;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getAdditionalContacts() {
        return additionalContacts;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public Long getAvatarId() {
        return avatarId;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public String getEmail() {
        return email;
    }

    public String getIcq() {
        return icq;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public void setUserGroup(int userGroup) {
        this.userGroup = userGroup;
    }

    public int getUserGroup() {
        return userGroup;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSkype() {
        return skype;
    }

    public Date getLastNotificationDate() {
        return lastNotificationDate;
    }

    public void setLastNotificationDate(Date lastNotificationDate) {
        this.lastNotificationDate = lastNotificationDate;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.User[ id=" + id + " ]";
    }
}
