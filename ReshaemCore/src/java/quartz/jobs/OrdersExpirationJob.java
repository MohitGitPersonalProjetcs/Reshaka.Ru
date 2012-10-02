package quartz.jobs;

import ejb.util.EJBUtils;
import entity.OnlineHelp;
import entity.Order;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job for Quartz that checks all orders for expiration 
 * and modifies statuses when needed.
 * The following policy is applied:
 *  - online orders are deleted after in (24 hours + relax_time) after start of the order;
 *  - offline orders are deleted immediately if the deadline is before current time stamp.
 * @author Danon
 */
public class OrdersExpirationJob implements ReshakaJob {
    
    @Override
    public String getDescription() {
        return "Checks validity of orders and modifies statuses if needed.";
    }

    @Override
    public String getName() {
        return "Orders Expiration Check";
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            EntityManagerFactory f = Persistence.createEntityManagerFactory("ReshaemCorePU");
            EntityManager em = f.createEntityManager();
            UserTransaction transaction = EJBUtils.resolve("java:comp/UserTransaction", UserTransaction.class);
            transaction.begin();
            // Offline orders
            Query q = em.createQuery("update Order o set o.status=:ORDER_EXPIRED_STATUS"
                    + " where o.deadline <= CURRENT_TIMESTAMP"
                    + " and o.status in :ALLOWED_ORDERS"
                    + " and o.type = :ORDER_TYPE");
            q.setParameter("ORDER_EXPIRED_STATUS", Order.EXPIRED_OFFLINE_ORDER_STATUS);
            q.setParameter("ALLOWED_ORDERS", Arrays.asList(Order.NEW_OFFLINE_ORDER_STATUS, Order.RATED_OFFLINE_ORDER_STATUS));
            q.setParameter("ORDER_TYPE", Order.OFFLINE_TYPE);
            int r = q.executeUpdate();
            
            // Online orders
            q = em.createQuery("update Order o set o.status=:ORDER_EXPIRED_STATUS"
                    + " where o.deadline <= :DATE_THRESHOLD"
                    + " and o.status in :ALLOWED_ORDERS"
                    + " and o.type = :ORDER_TYPE");
            q.setParameter("ORDER_EXPIRED_STATUS", Order.EXPIRED_ONLINE_ORDER_STATUS);
            q.setParameter("ALLOWED_ORDERS", Arrays.asList(Order.NEW_ONLINE_ORDER_STATUS, Order.RATED_ONLINE_ORDER_STATUS));
            q.setParameter("ORDER_TYPE", Order.ONLINE_TYPE);
            q.setParameter("DATE_THRESHOLD", new Date(System.currentTimeMillis()-
                    (OnlineHelp.RELAXATION_TIME+OnlineHelp.MAX_DURATION_TIME)*1000*60));
            r += q.executeUpdate();
            
            transaction.commit();
            em.close();
            f.close();
            
            System.out.println(getName()+" finished: "+r+" rows modified");
        } catch (Exception ex) {
            System.err.println(getName()+" failed!");
            ex.printStackTrace();
        }
    }
    
}
