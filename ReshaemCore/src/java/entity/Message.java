package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * Entity of the Message
 * @author Danon
 */

@Entity @Table(name="Messages")
@NamedQueries({
    @NamedQuery(name="Message.findIncomingBetween",
                query="SELECT m FROM Message m WHERE m.toUser = :owner and m.dateSent > :afterDate and m.dateSent < :beforeDate order by m.dateSent"),
    @NamedQuery(name="Message.findIncomingFromUserBetween",
                query="SELECT m FROM Message m WHERE m.toUser = :owner and m.fromUser = :fromUser and m.dateSent > :afterDate and m.dateSent < :beforeDate order by m.dateSent"),
    @NamedQuery(name="Message.findOutcomingBetween",
                query="SELECT m FROM Message m WHERE m.fromUser = :owner and m.dateSent > :afterDate and m.dateSent < :beforeDate order by m.dateSent"),
    @NamedQuery(name="Message.findOutcomingToUserBetween",
                query="SELECT m FROM Message m WHERE m.fromUser = :owner and m.toUser = :toUser and m.dateSent > :afterDate and m.dateSent < :beforeDate order by m.dateSent"),
    @NamedQuery(name="Message.findAllBetween",
                query="SELECT m FROM Message m WHERE m.dateSent > :afterDate and m.dateSent < :beforeDate order by m.dateSent"),
    @NamedQuery(name="Message.findAllOfUserBetween",
                query="SELECT m FROM Message m WHERE (m.fromUser = :owner or m.toUser = :owner) and m.dateSent > :afterDate and m.dateSent < :beforeDate order by m.dateSent"),
    @NamedQuery(name="Message.findAllOfUser",
                query="SELECT m FROM Message m WHERE (m.fromUser = :owner or m.toUser = :owner)"),
})
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="from_user", referencedColumnName="id")
    private User fromUser;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="to_user", referencedColumnName="id")
    private User toUser;
    
    @Column(name="subject")
    private String subject;
    
    @Column(name="text",nullable=false,length=4000)
    private String text;
    
    @Column(name="date_sent")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSent;
    
    @Column(name="attachment_id")
    private Long attachmentId;
    
    @Column(name="read_status")
    private boolean read;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachment) {
        this.attachmentId = attachment;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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
        if (!(object instanceof Message)) {
            return false;
        }
        Message other = (Message) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Message.class.getName()).append(" {\n");
        sb.append("\tid=").append(id).append(",\n");
        sb.append("\tfromUser=").append(fromUser).append(",\n");
        sb.append("\ttoUser=").append(toUser).append(",\n");
        sb.append("\tsubject=").append("***").append(",\n");
        sb.append("\ttext=").append("***").append(",\n");
        sb.append("\tdateSent=").append(dateSent).append(",\n");
        sb.append("\tattachmentId=").append(attachmentId).append(",\n");
        sb.append("\ttread=").append(read);
        sb.append("\n}");
        return sb.toString();
    }
    
}
