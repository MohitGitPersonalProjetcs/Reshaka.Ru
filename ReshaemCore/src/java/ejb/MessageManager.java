package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import data.SimpleUser;
import ejb.util.ReshakaUploadedFile;
import entity.Attachment;
import entity.Message;
import entity.User;
import java.util.ArrayList;
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
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public List<Message> getIncomingMessages(long owner, Long fromUser, Date afterDate, Date beforeDate) {
        if (log.isTraceEnabled()) {
            log.trace(">> getIncomingMessages(): owner=" + owner);
        }
        List<Message> messages = null;

        Query q = null;
        if (fromUser == null) {
            if (log.isTraceEnabled()) {
                log.trace("getIncomingMessages(): using query Message.findIncomingBetween");
            }
            q = em.createNamedQuery("Message.findIncomingBetween");
        } else {
            q = em.createNamedQuery("Message.findIncomingFromUserBetween");
            if (log.isTraceEnabled()) {
                log.trace("getIncomingMessages(): using query Message.findIncomingFromUserBetween");
            }
        }

        User u = em.find(User.class, owner);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< getIncomingMessages(): no user with ID=" + owner);
            }
            return null;
        }
        q.setParameter("owner", u);

        if (fromUser != null) {
            u = em.find(User.class, fromUser);
            if (u == null) {
                if (log.isTraceEnabled()) {
                    log.trace("<< getIncomingMessages(): no user with ID=" + fromUser);
                }
                return null;
            }
            q.setParameter("fromUser", u);
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

        // set READ = TRUE
        for (Message msg : messages) {
            msg.setRead(true);
            em.merge(msg);
        }

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
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public List<Message> getOutcomingMessages(long owner, Long toUser, Date afterDate, Date beforeDate) {
        if (log.isTraceEnabled()) {
            log.trace(">> getOutMessages(): owner=" + owner);
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
            return null;
        }
        q.setParameter("owner", u);

        if (toUser != null) {
            u = em.find(User.class, toUser);
            if (u == null) {
                if (log.isTraceEnabled()) {
                    log.trace("<< getOutcomingMessages(): no user with ID=" + toUser);
                }
                return null;
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
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public Set<SimpleUser> getRecentUsers(long owner) {
        if (log.isTraceEnabled()) {
            log.trace(">> getRecentUsers(): id=" + owner);
        }
        Set<SimpleUser> lst = new HashSet<>();

        try {
            User u = em.getReference(User.class, owner);
            Query q = em.createNamedQuery("Message.findAllOfUser");
            q.setParameter("owner", u);
            List<Message> msgs = q.getResultList();

            if (msgs == null) {
                return lst;
            }
            for (Message m : msgs) {
                if (m.getFromUser() != null && m.getFromUser().getId() != null && m.getFromUser().getId() != owner) {
                    lst.add(new SimpleUser(m.getFromUser()));
                }
                if (m.getToUser() != null && m.getToUser().getId() != null && m.getToUser().getId() != owner) {
                    lst.add(new SimpleUser(m.getToUser()));
                }
            }
        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("getRecentUsers(): no such user > ", ex);
            }
            return null;
        }

        return lst;
    }

    @Override
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
            return null;
        }

        List<User> usrs = em.createQuery("SELECT u FROM User u WHERE u.login LIKE :login").setParameter("login", login + "%").getResultList();
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
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public List<Message> getAnyMessages(long id, Long mailBoxOwner, Date afterDate, Date beforeDate) {
        if (log.isTraceEnabled()) {
            log.trace(">> getAnyMessages(): id = " + id + ", userID = " + mailBoxOwner);
        }
        User adm = null;
        try {
            adm = em.getReference(User.class, id);
            if (adm.getUserGroup() != 1) {
                if (log.isTraceEnabled()) {
                    log.trace("getAnyMessages(): operation not permited!");
                }
                return null;
            }
        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("getAnyMessages(): operation not permited!", ex);
            }
            return null;
        }
        List<Message> messages = null;

        Query q = null;
        if (mailBoxOwner == null) {
            if (log.isTraceEnabled()) {
                log.trace("getAnyMessages()): using query Message.findAllBetween");
            }
            q = em.createNamedQuery("Message.findAllBetween");
        } else {
            if (log.isTraceEnabled()) {
                log.trace("getAnyMessages(): using query Message.findAllOfUserBetween");
            }
            q = em.createNamedQuery("Message.findAllOfUserBetween");
        }

        if (mailBoxOwner != null) {
            User u = em.find(User.class, mailBoxOwner);
            if (u == null) {
                if (log.isTraceEnabled()) {
                    log.trace("<< getAnyMessages(): no user with ID=" + mailBoxOwner);
                }
                return null;
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

        if (log.isTraceEnabled()) {
            log.trace("<< getAnyMessages(): " + messages);
        }
        return messages;
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public boolean hasUnreadMessages(Long id) {
        if (log.isTraceEnabled()) {
            log.trace(">> hasUnreadMessages(): id = " + id);
        }
        User u = em.find(User.class, id);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< hasUnreadMessages(): false // user not found");
            }
            return false;
        }
        Query q = em.createQuery("select m from Message m where m.toUser = :u and m.read = false",
                Message.class);
        q.setParameter("u", u);
        q.setMaxResults(1);
        List<Message> l = q.getResultList();
        if (l == null || l.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("<< hasUnreadMessages(): false");
            }
            return false;
        }
        if (log.isTraceEnabled()) {
            log.trace("<< hasUnreadMessages(): true");
        }
        return true;
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public int getUnreadMessagesNumber(long id, Long fromUserId) {
        if (log.isTraceEnabled()) {
            log.trace(">> getUnreadMessagesNumber(): id = " + id);
        }
        User u = em.find(User.class, id);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< getUnreadMessagesNumber(): 0 // user not found");
            }
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
        if (log.isTraceEnabled()) {
            log.trace("<< getUnreadMessagesNumber(): "+n);
        }
        return n.intValue();
    }
}
