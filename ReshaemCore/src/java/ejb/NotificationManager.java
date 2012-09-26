package ejb;

import entity.Notification;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 * This
 *
 * @author danon
 */
@Stateless
public class NotificationManager implements NotificationManagerLocal {

    private static Logger log = Logger.getLogger(NotificationManager.class);
    @PersistenceContext(unitName = "ReshaemCorePU")
    private EntityManager em;

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Notification> getNotifications(Long userId, Integer type, Integer minPriority, boolean markRead) {
        if (log.isTraceEnabled()) {
            log.trace(">> enclosing_method(): getting notifications for " + userId);
        }
        User u = null;
        if (userId != null) {
            u = em.find(User.class, userId);
            if (u == null) {
                if (log.isTraceEnabled()) {
                    log.trace("<< (): null // no such user");
                }
                return null;
            }
        }

        String sql = "select n from Notification n where n.status=" + Notification.STATUS_ENABLED
                + " and :now between n.creationDate and n.expirationDate and n.creationDate > :minDate and";
        if (type != null) {
            sql += " n.type = :type and";
        }
        if (minPriority != null) {
            sql += " n.priority >= :minPriority";
        } else {
            sql += " n.priority >= 0";
        }

        sql += " order by n.creationDate asc, n.priority desc, n.id";

        if (log.isTraceEnabled()) {
            log.trace("getNotifications(): sql=" + sql);
        }

        Query q = em.createQuery(sql, Notification.class);
        if (type != null) {
            q.setParameter("type", type);
        }
        if (minPriority != null) {
            q.setParameter("minPriority", minPriority);
        }
        q.setParameter("now", new Date());
        if (u != null && u.getLastNotificationDate() != null) {
            q.setParameter("minDate", u.getLastNotificationDate());
        } else {
            q.setParameter("minDate", new Date(0));
        }

        List<Notification> n = q.getResultList();
        List<Notification> result = new ArrayList<>();

        Date maxDate = new Date(0);

        for (Notification notify : n) {
            String filter = notify.getFilter();
            if (filter == null || filter.isEmpty()) {
                filter = "1=1";
            }
            sql = "select u from User u where (" + filter + ")";
            if (u != null) {
                sql += " and u.id = " + userId;
            }
            try {
                q = em.createQuery(sql, User.class);
                List r = q.getResultList();
                if (r != null && !r.isEmpty()) {
                    result.add(notify);
                    if (maxDate.before(notify.getCreationDate())) {
                        maxDate = notify.getCreationDate();
                    }
                }
            } catch (Exception ex) {
                if (log.isTraceEnabled()) {
                    log.trace("getNotifications(): exception while processing notification "+notify);
                    log.trace("getNotifications(): status is set to DISABLED");
                }
                try {
                    notify.setStatus(Notification.STATUS_DISABLED);
                    em.merge(notify);
                } catch (Exception e) {}
            }
        }
        if (markRead && u != null) {
            if (u.getLastNotificationDate() != null) {
                if (maxDate.before(u.getLastNotificationDate())) {
                    maxDate = u.getLastNotificationDate();
                }
            }
            u.setLastNotificationDate(maxDate);
            em.merge(u);
        }
        if (log.isTraceEnabled()) {
            log.trace("<< getNotifications(): got list of notifications");
        }
        return result;
    }

    @Override
    public Notification postNotification(int type, int priority, String filter, String title, String content, Long ttl) {
        if (log.isTraceEnabled()) {
            log.trace(">> postNotification()");
        }
        Notification n = new Notification();
        n.setType(type);
        n.setPriority(priority);
        n.setFilter(filter);
        n.setStatus(Notification.STATUS_ENABLED);
        n.setTitle(title);
        n.setContent(content);
        long currentTime = System.currentTimeMillis();
        n.setCreationDate(new Date(currentTime));
        if (ttl == null) {
            ttl = 1000L * 60 * 60; // 1 hour
        }
        n.setExpirationDate(new Date(currentTime + ttl));
        em.persist(n);

        if (log.isTraceEnabled()) {
            log.trace("<< postNotification(): success ID = " + n.getId());
        }
        return n;
    }

    @Override
    public void deleteNotification(Long userId, long notificationId) {
        if (log.isTraceEnabled()) {
            log.trace(">> deleteNotification(): userId= " + userId + ", id=" + notificationId);
        }
        if (userId == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< deleteNotification(): operation not permitted");
            }
            return;
        }
        User u = em.find(User.class, userId);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< deleteNotification(): operation not permitted // no such user");
            }
            return;
        }
        if (u.getUserGroup() != 1) {
            if (log.isTraceEnabled()) {
                log.trace("<< deleteNotification(): operation not permitted // not admin");
            }
            return;
        }
        Notification n = em.find(Notification.class, notificationId);
        if (n != null) {
            em.remove(n);
        }
        if (log.isTraceEnabled()) {
            log.trace("<< deleteNotification(): deleted");
        }
    }

    @Override
    public void enableNotification(Long userId, long notificationId, boolean enabled) {
        if (log.isTraceEnabled()) {
            log.trace(">> enableNotification(): userId= " + userId + ", id=" + notificationId);
        }
        if (userId == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< enableNotification(): operation not permitted");
            }
            return;
        }
        User u = em.find(User.class, userId);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< enableNotification(): operation not permitted // no such user");
            }
            return;
        }
        if (u.getUserGroup() != 1) {
            if (log.isTraceEnabled()) {
                log.trace("<< denableNotification(): operation not permitted // not admin");
            }
            return;
        }
        Notification n = em.find(Notification.class, notificationId);
        n.setStatus(enabled ? Notification.STATUS_ENABLED : Notification.STATUS_DISABLED);
        if (log.isTraceEnabled()) {
            log.trace("<< deleteNotification(): "+(enabled ? "ENABLED" : "DISABLED"));
        }
    }
}
