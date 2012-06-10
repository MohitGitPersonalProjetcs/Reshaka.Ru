
package ejb;

import entity.Notification;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author danon
 */
@Local
public interface NotificationManagerLocal {

    /**
     * Returns the list of unread notifications, which satisfy specified parameters.
     * @param userId ID of the user who requests the notification list. If null, the method will return the list of notifications addressed to any user.
     * @param type type of notification
     * @param minPriority minimal priority (0 is the lowest priority). The notifications are ordered by date, priority and ID
     * @param markRead if true user.lastNotificationDate will be set to the creation date of the newest notification.
     * @return list of notifications, or null if error occurred
     */
    List<Notification> getNotifications(Long userId, Integer type, Integer minPriority, boolean markRead);

    /**
     * Sends the notification.
     * @param type notification type
     * @param priority notification priority (0 - the lowest, 100 - the highest)
     * @param filter selection filter (should be a valid JPQL predicate, e.g. "u.login LIKE 'danon%'")
     * @param title title of the notification (very short description)
     * @param content full content (XML, java object or just plain text)
     * @param ttl time to live in milliseconds (default is 1 hour)
     * @return created notification
     */
    Notification postNotification(int type, int priority, String filter, String title, String content, Long ttl);
    
    /**
     * Deletes notification with specified ID
     * @param userId ID of the user who performs the operation (should have admin rights)
     * @param notificationId ID of the notification to delete
     */
    void deleteNotification(Long userId, long notificationId);
    
    /**
     * Disables or enables notification with specified ID
     * @param userId ID of the user who performs the operation (should have admin rights)
     * @param notificationId ID of the notification to disable
     * @param enabled target value
     */
    void enableNotification(Long userId, long notificationId, boolean enabled);
    
}
