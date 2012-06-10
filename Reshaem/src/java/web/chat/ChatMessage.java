package web.chat;

import entity.Message;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Plain Java Object for chat messages
 * <p/>
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long messageId;

    private Long fromUserId;

    private Long toUserId;

    private String fromUserLogin;

    private String toUserLogin;

    private String text;

    private String subject;

    private Long attachmentId;

    private Date dateSent;

    private boolean read;

    public ChatMessage() {
    }

    public ChatMessage(Message msg) {
        if (msg == null) {
            return;
        }
        messageId = msg.getId();
        if (msg.getFromUser() != null) {
            fromUserId = msg.getFromUser().getId();
            fromUserLogin = msg.getFromUser().getLogin();
        }
        if (msg.getToUser() != null) {
            toUserId = msg.getToUser().getId();
            toUserLogin = msg.getToUser().getLogin();
        }
        text = msg.getText();
        subject = msg.getSubject();
        attachmentId = msg.getAttachmentId();
        dateSent = msg.getDateSent();
        read = msg.isRead();
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserLogin() {
        return fromUserLogin;
    }

    public void setFromUserLogin(String fromUserLogin) {
        this.fromUserLogin = fromUserLogin;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserLogin() {
        return toUserLogin;
    }

    public void setToUserLogin(String toUserLogin) {
        this.toUserLogin = toUserLogin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChatMessage other = (ChatMessage) obj;
        if (!Objects.equals(this.messageId, other.messageId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.messageId);
        hash = 89 * hash + Objects.hashCode(this.fromUserLogin);
        hash = 89 * hash + Objects.hashCode(this.toUserLogin);
        hash = 89 * hash + Objects.hashCode(this.text);
        hash = 89 * hash + Objects.hashCode(this.subject);
        hash = 89 * hash + Objects.hashCode(this.dateSent);
        return hash;
    }
    
}
