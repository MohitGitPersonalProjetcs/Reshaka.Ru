package web.chat;

import data.SimpleUser;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author danon
 */
public class ChatDialog implements Serializable {
    
    private SimpleUser user1;
    private SimpleUser user2;
    private Date lastMessageDate;
    private ChatMessage lastMessage;
    private int newMessages;
    private int status;

    public SimpleUser getUser1() {
        return user1;
    }

    public void setUser1(SimpleUser user1) {
        this.user1 = user1;
    }

    public SimpleUser getUser2() {
        return user2;
    }

    public void setUser2(SimpleUser user2) {
        this.user2 = user2;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getNewMessages() {
        return newMessages;
    }

    public void setNewMessages(int newMessages) {
        this.newMessages = newMessages;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
