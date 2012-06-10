package entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author rogvold
 */
@Entity
public class UserSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "new_problem")
    private boolean newProblem;
    @Column(name = "new_message")
    
    private boolean newMessage;
    @Column(name = "new_status")
    private boolean newStatus;
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public UserSettings() {
//        setNewMessage(true);
//        setNewProblem(true);
//        setNewStatus(true);
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    
    
    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    public boolean isNewProblem() {
        return newProblem;
    }

    public void setNewProblem(boolean newProblem) {
        this.newProblem = newProblem;
    }

    public boolean isNewStatus() {
        return newStatus;
    }

    public void setNewStatus(boolean newStatus) {
        this.newStatus = newStatus;
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

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserSettings)) {
            return false;
        }
        UserSettings other = (UserSettings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.UserSettings[ id=" + id + " ]";
    }
}
