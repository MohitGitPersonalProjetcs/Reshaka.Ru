package ejb;

import data.SimpleUser;
import ejb.util.ReshakaUploadedFile;
import entity.Message;
import entity.User;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for SessionManager EJB.
 * @author Danon
 */

@Local
public interface MessageManagerLocal {
    
    Message sendMessage(long userFrom, Long userTo, String subject, String text, Long attachment);
    
    Message sendMessageAndUpload(long userFrom, Long userTo, String subject, String text, List<ReshakaUploadedFile> files);
    
    List<Message> getIncomingMessages(long owner, Long fromUser, Date afterDate, Date beforeDate);
    
    List<Message> getOutcomingMessages(long owner, Long toUser, Date afterDate, Date beforeDate);
    
    List<Message> getIncomingMessages(long owner, Long fromUser, Date afterDate, Date beforeDate, boolean markRead);
    
    Set<SimpleUser> getRecentUsers(long owner);
    
    Set<SimpleUser> getRecentUsers(long owner, int limit); 
    
    List<SimpleUser> filterUsersByLogin(long owner, String login);

    /**
     * Returns any incoming and outcoming messages for specified user.
     * Only administrator can perform this operation.
     * @param id the ID of requestor
     * @param mailBoxOwner the ID of user whose messages are requested
     * @param afterDate 
     * @param beforeDate
     * @return list of messages
     */
    public List<Message> getAnyMessages(long id, Long mailBoxOwner, Date afterDate, Date beforeDate);

    /**
     * Checks whether the user with specified ID has unread messages
     * @param id the ID of user
     * @return true if there is at least one unread message, false otherwise
     */
    public boolean hasUnreadMessages(Long id);
    
    /**
     * Returns the number of user's unread messages received from specified user.
     * @param id the ID of user whose messages are requested
     * @param fromUserId if it is null, the method returns the total number of unread messages
     * @return number of unread messages
     */
    public int getUnreadMessagesNumber(long id, Long fromUserId);

    /**
     * Obtains the last messages between two users
     */
    public Message getLastMessage(Long iuserId1, Long userId2);

    /**
     * Obtains the list of users whose messages to the user with specified userId are not read.
     * @return 
     */
    public List<User> getUnreadMessagesUsers(Long userId);

    public void markRemoved(long userId, Long id, String removedBy);
    
}
