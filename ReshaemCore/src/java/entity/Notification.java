package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author Danon
 */
@Entity @Table(name="NTIFICATIONS")
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final int TYPE_NOTIFICATION = 0;
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_ORDER = 2;
    public static final int TYPE_ONLINE_ORDER = 3;
    public static final int TYPE_USER_SESSION = 4;
    public static final int TYPE_COMMENT = 5;
    public static final int TYPE_FEEDBACK = 6;
    public static final int TYPE_NEWS = 7;
    public static final int TYPE_MONEY = 8;
    public static final int TYPE_COMMAND = 9;
    
    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DISABLED = 0;
    
    public static final int PRIORITY_LOWEST = 0;
    public static final int PRIORITY_LOW = 25;
    public static final int PRIORITY_MEDIUM = 50;
    public static final int PRIORITY_HIGH = 75;
    public static final int PRIORITY_HIGHEST = 100;
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    @Column(name="type")
    private int type;
    
    @Column(name="filter")
    private String filter;
    
    @Column(name="title")
    private String title;
    
    @Column(name="content")
    private String content;
    
    @Column(name="created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @Column(name="expires")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
    
    @Column(name="status")
    private int status;
    
    @Column(name="priority")
    private int priority;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilter() {
        return filter;
    }

    /**
     * Set user selection filter.
     * For example, "u.userGroup = 2"
     * @param filter a valid JPQL predicate
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Notification[ id=" + id + " ]";
    }
    
}
