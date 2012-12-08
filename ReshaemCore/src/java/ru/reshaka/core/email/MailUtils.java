package ru.reshaka.core.email;

import entity.Subject;
import entity.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author rogvold
 */
public class MailUtils {

    public static Map<String, String> getSubscribers(Subject subject) {
        EntityManagerFactory f = Persistence.createEntityManagerFactory("ReshaemCorePU");
        EntityManager em = f.createEntityManager();
        Map<String, String> map = new HashMap();
        try {
            Subject subj = em.find(Subject.class, subject.getId());
            List<User> users = subj.getUsers();

            for (User u : users) {
                if (u.getEmail() == null) {
                    continue;
                }
                map.put(u.getLogin(), u.getEmail());
            }
        } finally {
            em.close();
            f.close();
        }

        return map;
    }
}
