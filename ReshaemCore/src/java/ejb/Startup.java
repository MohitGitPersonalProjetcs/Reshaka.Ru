package ejb;

import entity.OpenId;
import entity.Subject;
import entity.Tag;
import entity.User;
import events.EventEJBLocal;
import events.EventManager;
import events.ReshakaEvent;
import events.ReshakaEventAdapter;
import java.net.URL;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import quartz.ReshakaScheduler;
import quartz.jobs.OrdersExpirationJob;

/**
 * This Singleton startup EJB performs initial configuration at startup.
 *
 * @author danon
 */
@Singleton
@javax.ejb.Startup
public class Startup {

    private static Logger log = null;
    public static final String LOG4J_CONFIG = "/log4j.xml";
    @EJB
    ConfigurationManagerLocal cm;
    @EJB
    MoneyManagerLocal monMan;
    @EJB
    EventEJBLocal evt;
    @PersistenceContext
    EntityManager em;

    @PostConstruct
    public void startup() {
        try {
            log4jInit();
        } catch (Exception ex) {
            System.err.println("Failed to initialize log4j!" + ex);
        }
        try {
            initDatabase();
        } catch (Exception ex) {
            System.err.println("Init database failed!" + ex);
        }
        try {
            checkUploads();
        } catch (Exception ex) {
            System.err.println("Upload validity check failed!" + ex);
        }

        Timer t = new Timer("moneyChecker");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMoney(); //uncomment!!!
            }
        }, new Date(), 60 * 1000);

        if (log != null) {
            log.info("EJB module initialized");
        } else {
            System.out.println("EJB module initialized");
        }

        // Example of event
        EventManager.getInstance().registerListener(new ReshakaEventAdapter() {
            @Override
            public void processEvent(ReshakaEvent evt) {
                System.err.println("Captured event: " + evt.getDescription());
            }
        });

        ReshakaEvent event = new ReshakaEvent();
        event.setDescription("test event");
        evt.postEvent(event);

        try {
            ReshakaScheduler.getInstance().start();

            // Schedule: Order Expiration Check
            Trigger trigger = TriggerBuilder.newTrigger().startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever()).build();
            ReshakaScheduler.getInstance().schedule(OrdersExpirationJob.class, trigger);
        } catch (SchedulerException ex) {
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            ReshakaScheduler.getInstance().shutdown();
        } catch (Exception ex) {
        }
        if (log != null) {
            log.info("EJB module destroyed");
        }
    }

    private void log4jInit() {
        URL url = Startup.class.getResource(LOG4J_CONFIG);

        DOMConfigurator.configure(url.getFile());
        log = Logger.getLogger(Startup.class);
        if (log != null) {
            log.debug("Logger initialized with " + url);
        }
    }

    private void checkUploads() {
        //TODO: Check Attachments table and upload directory 
        //       and remove redundant files and entries 
        new AttachmentManager().removeInvalidEntries();
    }

    private void initDatabase() {
        // create main admin
        try {
            addMainAdmin();
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("initDatabase(): addMainAdmin() failed", ex);
            }
        }
        // create chat admin
        try {
            addChatAdmin();
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("initDatabase(): addChatAdmin() failed", ex);
            }
        }
        // add subjects
        try {
            addSubjects();
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("initDatabase(): addSubjects() failed", ex);
            }
        }

        try {
            addTags();
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("initDatabase(): addTags() failed", ex);
            }
        }

    }

    private void addMainAdmin() {
        User u = null;
        boolean exists = false;

        Long mainAdminId = cm.getLong("mainAdminId", 99999999L);
        u = em.find(User.class, mainAdminId);
        if (u != null) {
            exists = true;
        } else {
            u = new User();
        }

        String mainAdminLogin = cm.getString("mainAdminLogin", "admin");
        String mainAdminPassword = cm.getString("mainAdminPassword", "reshaka");
        String mainAdminEmail = cm.getString("mainAdminEmail", "reshaka");
        u.setLogin(mainAdminLogin);
        u.setPassword(mainAdminPassword);
        u.setEmail(mainAdminEmail);
        u.setUserGroup(1);
        u.setOpenId(new OpenId());

        if (!exists) {
            u.setId(mainAdminId);
            em.persist(u);
        } else {
            em.merge(u);
        }
    }

    private void addChatAdmin() {
        User u = null;
        boolean exists = false;

        Long mainAdminId = cm.getLong("chatAdminId", 77777777L);
        u = em.find(User.class, mainAdminId);
        if (u != null) {
            exists = true;
        } else {
            u = new User();
        }

        String mainAdminLogin = cm.getString("chatAdminLogin", "admin");
        String mainAdminPassword = cm.getString("chatAdminPassword", "reshaka");
        String mainAdminEmail = cm.getString("chatAdminEmail", "reshaka");
        u.setLogin(mainAdminLogin);
        u.setPassword(mainAdminPassword);
        u.setEmail(mainAdminEmail);
        u.setUserGroup(1);
        u.setOpenId(new OpenId());

        if (!exists) {
            u.setId(mainAdminId);
            em.persist(u);
        } else {
            em.merge(u);
        }
    }

    private void addSubjects() {
        Query q = em.createNamedQuery("getSubjectByName");
        if (q.setParameter("subjectName", Subject.NOT_SELECTED_SUBJECT_NAME).getResultList() == null) {
            if (log.isTraceEnabled()) {
                log.trace("addSubjects(): added NOT_SELECTED_SUBJECT_NAME");
            }
            Subject nss = new Subject(Subject.NOT_SELECTED_SUBJECT_NAME);
            em.persist(nss);
        }
        List<String> s = cm.getList("subjects", new ArrayList<String>());
        if (s == null) {
            throw new IllegalArgumentException("Filed to get the list of subjects.");
        }

        for (String subj : s) {
            // add subject
            q.setParameter("subjectName", subj);
            List<Subject> lst = q.getResultList();
            if (lst == null || lst.isEmpty()) {
                Subject ss = new Subject(subj);
                em.persist(ss);
            }
        }

    }

    private void addTags() {
        List<String> s = cm.getList("tags", new ArrayList<String>());
        if (s == null) {
            throw new IllegalArgumentException("Filed to get the list of subjects.");
        }
        Query q = em.createNamedQuery("getTagByText");
        for (String t : s) {
            // add subject
            q.setParameter("text", t);
            List<Tag> lst = q.getResultList();
            if (lst == null || lst.isEmpty()) {
                Tag ss = new Tag();
                ss.setText(t);
                em.persist(ss);
            }
        }
    }

    private void updateMoney() {
        monMan.updateMoney();
    }
}
