package quartz.jobs;

import ejb.ConfigurationManagerLocal;
import ejb.MailManagerLocal;
import ejb.MessageManagerLocal;
import ejb.util.EJBUtils;
import entity.Order;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job for Quartz that checks all orders and prolong the deadline
 * The following policy is applied:
 *  What policy should be applied????
 * @author Danon
 */
public class OrdersAutoRenewJob implements ReshakaJob {
    
    private static final Logger log = Logger.getLogger(OrdersAutoRenewJob.class);
    
    private static final ConfigurationManagerLocal cm = EJBUtils.resolve("java:global/ReshaemEE/ReshaemCore/ConfigurationManager!ejb.ConfigurationManagerLocal", ConfigurationManagerLocal.class);
    
    
    
    private static final List<Integer> ALLOWED_STATUSES = new ArrayList<Integer>(2) {{
                    add(Order.NEW_OFFLINE_ORDER_STATUS);
                    add(Order.RATED_OFFLINE_ORDER_STATUS);
                }};
    
    
    
    @Override
    public String getDescription() {
        return "Updated the deadline of orders if needed.";
    }

    @Override
    public String getName() {
        return "Orders Auto Renew";
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        if(log.isTraceEnabled()) {
            log.trace(">> execute()");
        }
        try {
            MessageManagerLocal mm = EJBUtils.resolve("java:global/ReshaemEE/ReshaemCore/MessageManager!ejb.MessageManagerLocal", MessageManagerLocal.class);
            EntityManagerFactory f = Persistence.createEntityManagerFactory("ReshaemCorePU");
            MailManagerLocal mailMan = EJBUtils.resolve("java:global/ReshaemEE/ReshaemCore/MailManager!ejb.MailManagerLocal", MailManagerLocal.class);
            
            EntityManager em = f.createEntityManager();
            em.getTransaction().begin();
//            UserTransaction transaction = EJBUtils.resolve("java:comp/UserTransaction", UserTransaction.class);
//            transaction.begin();
            // Offline orders
            Query q = em.createQuery("SELECT o FROM Order o WHERE o.deadline <= :now");
            q.setParameter("now", new Date());
            List<Order> orders = q.getResultList();
            for(Order o : orders) {
                if(ALLOWED_STATUSES.contains(o.getType())) {
                    o.setDeadline(new Date(o.getDeadline().getTime() + 24*60*60*1000));
                    em.merge(o);
                    mm.sendMessage(cm.getMainAdminId(), 
                                    o.getEmployer().getId(), 
                                    "Order Auto Renew", 
                                    "Ваш заказ #"+o.getId()+" продлен еще на одни сутки.", 
                                    null);
                    String text = "Здравствуйте, " + o.getEmployer().getLogin() + " !"
                        + "\n\nВаш заказ ID=" + o.getId() + " продлён еще на сутки."
                        + "\n\n\nC уважением, администрация Reshaka.Ru";
                    String theme = "Автоматическое продление заказа";
                    mailMan.sendMail(o.getEmployer().getEmail(), theme, text);
                }
            }
            
            em.getTransaction().commit();
            em.close();
            f.close();
        } catch (Exception ex) {
            if(log.isDebugEnabled())
                log.debug("execute(): job execution failed!", ex);
        }
        if(log.isTraceEnabled()) {
            log.trace("<< execute()");
        }
    }
    
}
