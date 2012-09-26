package ejb;

import data.SimpleUser;
import ejb.util.ReshakaUploadedFile;
import entity.Attachment;
import entity.Message;
import entity.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 * A simple implementation of Message manager EJB. It provides some methods for
 * sending and getting messages.
 *
 * @author Danon
 */
@Stateless
public class MessageManager implements MessageManagerLocal {

    private static Logger log = Logger.getLogger(MessageManager.class);
    
    @PersistenceContext(unitName = "ReshaemCorePU")
    private EntityManager em;
    
    @EJB
    AttachmentManagerLocal am;

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.REQUIRED)
    public List<Message> getIncomingMessages(long owner, Long fromUser, Date afterDate, Date beforeDate) {
        if (log.isTraceEnabled()) {
            log.trace(">> getIncomingMessages(): owner=" + owner);
        }
        List<Message> messages = null;

        Query q = null;
        Query setReadQuery = null;
        if (fromUser == null) {
            if (log.isTraceEnabled()) {
                log.trace("getIncomingMessages(): using query Message.findIncomingBetween");
            }
            q = em.createNamedQuery("Message.findIncomingBetween");
            setReadQuery = em.createNamedQuery("Message.updateReadIncomingBetween");
        } else {
            q = em.createNamedQuery("Message.findIncomingFromUserBetween");
            setReadQuery = em.createNamedQuery("Message.updateReadIncomingFromUserBetween");
            if (log.isTraceEnabled()) {
                log.trace("getIncomingMessages(): using query Message.findIncomingFromUserBetween");
            }
        }

        User u = em.find(User.class, owner);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< getIncomingMessages(): no user with ID=" + owner);
            }
            return Collections.EMPTY_LIST;
        }
        q.setParameter("owner", u);
        setReadQuery.setParameter("owner", u);

        if (fromUser != null) {
            u = em.find(User.class, fromUser);
            if (u == null) {
                if (log.isTraceEnabled()) {
                    log.trace("<< getIncomingMessages(): no user with ID=" + fromUser);
                }
                return Collections.EMPTY_LIST;
            }
            q.setParameter("fromUser", u);
            setReadQuery.setParameter("fromUser", u);
        }

        if (afterDate == null) {
            afterDate = new Date(0);
        }
        q.setParameter("afterDate", afterDate);
        setReadQuery.setParameter("afterDate", afterDate);
        if (beforeDate == null) {
            beforeDate = new Date();
        }
        q.setParameter("beforeDate", beforeDate);
        setReadQuery.setParameter("beforeDate", beforeDate);
        setReadQuery.setParameter("isRead", true);
        
        messages = (List<Message>) q.getResultList();
        setReadQuery.executeUpdate();

        if (log.isTraceEnabled()) {
            log.trace("<< getIncomingMessages(): " + messages);
        }
        return messages;
    }

    @Override
    public Message sendMessage(long userFrom, Long userTo, String subject, String text, Long attachment) {
        if (log.isTraceEnabled()) {
            log.trace(">> sendMessage()");
        }

        Message msg = new Message();
        try {
            msg.setDateSent(new Date());
            msg.setFromUser(em.find(User.class, userFrom));
            if (userTo != null) {
                msg.setToUser(em.find(User.class, userTo));
            } else {
                msg.setToUser(null);
            }
            msg.setRead(false);
            msg.setSubject(subject);
            msg.setText(text);
            msg.setAttachmentId(attachment);

            em.persist(msg);
        } catch (Exception ex) {
            log.warn("Failed to send message.", ex);
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace("sendMessage(): message sent > " + msg);
        }
        return msg;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Message> getOutcomingMessages(long owner, Long toUser, Date afterDate, Date beforeDate) {
        if (log.isTraceEnabled()) {
            log.trace(">> getOutcomingMessages(): owner=" + owner);
        }

        List<Message> messages = null;

        Query q = null;
        if (toUser == null) {
            if (log.isTraceEnabled()) {
                log.trace("getOutcomingMessages(): using query Message.findOutcomingBetween");
            }
            q = em.createNamedQuery("Message.findOutcomingBetween");
        } else {
            if (log.isTraceEnabled()) {
                log.trace("getOutcomingMessages(): using query Message.findOutcomingToUserBetween");
            }
            q = em.createNamedQuery("Message.findOutcomingToUserBetween");
        }

        User u = em.find(User.class, owner);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< getOutcomingMessages(): no user with ID=" + owner);
            }
            return Collections.EMPTY_LIST;
        }
        q.setParameter("owner", u);

        if (toUser != null) {
            u = em.find(User.class, toUser);
            if (u == null) {
                if (log.isTraceEnabled()) {
                    log.trace("<< getOutcomingMessages(): no user with ID=" + toUser);
                }
                return Collections.EMPTY_LIST;
            }
            q.setParameter("toUser", u);
        }

        if (afterDate == null) {
            afterDate = new Date(0);
        }
        q.setParameter("afterDate", afterDate);
        if (beforeDate == null) {
            beforeDate = new Date();
        }
        q.setParameter("beforeDate", beforeDate);

        messages = (List<Message>) q.getResultList();

        if (log.isTraceEnabled()) {
            log.trace("<< getOutcomingMessages(): " + messages);
        }
        return messages;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Set<SimpleUser> getRecentUsers(long owner) {
        if (log.isTraceEnabled()) {
            log.trace(">> getRecentUsers(): id=" + owner);
        }

        try {
            User u = em.getReference(User.class, owner);
            Query q = em.createQuery("SELECT DISTINCT m.toUser "
                    + "FROM Message m WHERE m.fromUser = :owner ", User.class);
            q.setParameter("owner", u);
            q.setMaxResults(10);
            
            List<User> users = q.getResultList();
            if (users == null) {
                return Collections.EMPTY_SET;
            }
            Set<SimpleUser> lst = new HashSet<>(users.size()*2);
            for (User user : users) {
                lst.add(new SimpleUser(user));
            }
            
            q = em.createQuery("SELECT DISTINCT m.fromUser "
                    + "FROM Message m WHERE m.toUser = :owner ", User.class);
            q.setParameter("owner", u);
            q.setMaxResults(10);
            users = q.getResultList();
            if (users == null) {
                return lst;
            }
            
            for (User user : users) {
                lst.add(new SimpleUser(user));
            }
            
            return lst;
        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("getRecentUsers(): no such user > ", ex);
            }
            return Collections.EMPTY_SET;
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<SimpleUser> filterUsersByLogin(long owner, String login) {
        if (log.isTraceEnabled()) {
            log.trace(">> filterUsersByLogin(): owner=" + owner + "; login=" + login);
        }
        List<SimpleUser> lst = new ArrayList<>();
        try {
            em.getReference(User.class, owner);
        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("filterUsersByLogin(): permission denied");
            }
            return Collections.EMPTY_LIST;
        }
        
        List<User> usrs = em.createQuery("SELECT u FROM User u WHERE u.login LIKE :login").setParameter("login", login + "%").setMaxResults(15).getResultList();
        for (User u : usrs) {
            lst.add(new SimpleUser(u));
        }
        return lst;
    }

    @Override
    public Message sendMessageAndUpload(long userFrom, Long userTo, String subject, String text, List<ReshakaUploadedFile> files) {
        if (log.isTraceEnabled()) {
            log.trace(">> sendMessageAndUpload()");
        }

        Message msg = new Message();
        try {
            msg.setDateSent(new Date());
            User uf = em.find(User.class, userFrom);
            msg.setFromUser(uf);
            Attachment att = am.uploadFiles(uf, files, "message");
            if (att != null) {
                am.shareFile(att.getId(), userFrom, userTo);
                msg.setAttachmentId(att.getId());
            } else if(files!=null&&!files.isEmpty()) {
                text += " (ошибка прикрепления файла)";
                sendMessage(userFrom, userTo, "chat-error", "Невозможно отправить файл. Возможно слишком большой размер.", null);
            }
            User ut = userTo != null ? em.find(User.class, userTo) : null;
            msg.setToUser(ut);
            msg.setRead(false);
            msg.setSubject(subject);
            msg.setText(text);

            em.persist(msg);
        } catch (Exception ex) {
            log.warn("Failed to send message.", ex);
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace("sendMessageAndUpload(): message sent > " + msg);
        }
        return msg;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Message> getAnyMessages(long id, Long mailBoxOwner, Date afterDate, Date beforeDate) {
        if (log.isTraceEnabled()) {
            log.trace(">> getAnyMessages(): id = " + id + ", userID = " + mailBoxOwner);
        }
        User adm = null;
        try {
            adm = em.getReference(User.class, id);
            if (adm.getUserGroup() != 1) {
//                if (log.isTraceEnabled()) {
//                    log.trace("getAnyMessages(): operation not permited!");
//                }
                return Collections.EMPTY_LIST;
            }
        } catch (Exception ex) {
//            if (log.isTraceEnabled()) {
//                log.trace("getAnyMessages(): operation not permited!", ex);
//            }
            return null;
        }
        List<Message> messages = Collections.EMPTY_LIST;

        Query q = null;
        if (mailBoxOwner == null) {
//            if (log.isTraceEnabled()) {
//                log.trace("getAnyMessages()): using query Message.findAllBetween");
//            }
            q = em.createNamedQuery("Message.findAllBetween");
        } else {
//            if (log.isTraceEnabled()) {
//                log.trace("getAnyMessages(): using query Message.findAllOfUserBetween");
//            }
            q = em.createNamedQuery("Message.findAllOfUserBetween");
        }

        if (mailBoxOwner != null) {
            User u = em.find(User.class, mailBoxOwner);
            if (u == null) {
//                if (log.isTraceEnabled()) {
//                    log.trace("<< getAnyMessages(): no user with ID=" + mailBoxOwner);
//                }
                return Collections.EMPTY_LIST;
            }
            q.setParameter("owner", u);
        }


        if (afterDate == null) {
            afterDate = new Date(0);
        }
        q.setParameter("afterDate", afterDate);
        if (beforeDate == null) {
            beforeDate = new Date();
        }
        q.setParameter("beforeDate", beforeDate);

        messages = (List<Message>) q.getResultList();

//        if (log.isTraceEnabled()) {
//            log.trace("<< getAnyMessages(): " + messages);
//        }
        return messages;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public boolean hasUnreadMessages(Long id) {
        if (log.isTraceEnabled()) {
            log.trace(">> hasUnreadMessages(): id = " + id);
        }
        int r = getUnreadMessagesNumber(id, null);
        if(r==0) {
            return false;
        }   
        if (log.isTraceEnabled()) {
            log.trace("<< hasUnreadMessages()");
        }
        return true;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public int getUnreadMessagesNumber(long id, Long fromUserId) {
//        if (log.isTraceEnabled()) {
//            log.trace(">> getUnreadMessagesNumber(): id = " + id);
//        }
        User u = em.find(User.class, id);
        if (u == null) {
//            if (log.isTraceEnabled()) {
//                log.trace("<< getUnreadMessagesNumber(): 0 // user not found");
//            }
            return 0;
        }
        Query q;
        if (fromUserId != null) {
            q = em.createQuery("select count(m) from Message m where m.toUser = :u and m.fromUser = :fromUser and m.read = false",
                    Message.class);
            q.setParameter("fromUser", em.find(User.class, fromUserId));
        } else {
            q = em.createQuery("select count(m) from Message m where m.toUser = :u and m.read = false",
                    Message.class);
        }
        q.setParameter("u", u);
        Number n = (Number) q.getSingleResult();
//        if (log.isTraceEnabled()) {
//            log.trace("<< getUnreadMessagesNumber(): "+n);
//        }
        return n.intValue();
    }
}
