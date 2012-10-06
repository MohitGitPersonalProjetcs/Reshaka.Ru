package quartz.jobs;

import entity.OnlineHelp;
import entity.Order;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Job for Quartz that checks all orders for expiration 
 * and modifies statuses when needed.
 * The following policy is applied:
 *  - online order is considered to be EXPIRED (24 hours + relax_time) after order deadline passed (in case it has NEW status);
 *  - offline or online order is considered to be CLOSED in case it is in status FULL_PAYED/PAYED.
 * @author Danon
 */
public class OrdersExpirationJob implements ReshakaJob {
    
    private static Logger log = Logger.getLogger(OrdersExpirationJob.class);
    
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
        if(log.isTraceEnabled()) {
            log.trace(">> execute()");
        }
        EntityTransaction etx = null;
        try {
            EntityManagerFactory f = Persistence.createEntityManagerFactory("ReshaemCorePU");
            EntityManager em = f.createEntityManager();
            etx = em.getTransaction();
            etx.begin();
            // Offline orders
            Query q = em.createQuery("update Order o set o.status=:ORDER_EXPIRED_STATUS"
                    + " where o.deadline <= CURRENT_TIMESTAMP"
                    + " and o.status in :ALLOWED_ORDERS"
                    + " and o.type = :ORDER_TYPE");
            q.setParameter("ORDER_EXPIRED_STATUS", Order.CLOSED_OFFLINE_ORDER_STATUS);
            q.setParameter("ALLOWED_ORDERS", Arrays.asList(Order.FULL_PAYED_OFFLINE_ORDER_STATUS));
            q.setParameter("ORDER_TYPE", Order.OFFLINE_TYPE);
            int r = q.executeUpdate();
            if(log.isTraceEnabled()) {
                log.trace("execute(): "+r+" offline orders has been closed.");
            }
            
            // Online orders
            q = em.createQuery("update Order o set o.status=:ORDER_EXPIRED_STATUS"
                    + " where o.deadline <= :DATE_THRESHOLD"
                    + " and o.status in :ALLOWED_ORDERS"
                    + " and o.type = :ORDER_TYPE");
            q.setParameter("ORDER_EXPIRED_STATUS", Order.FINISHED_ONLINE_ORDER_STATUS);
            q.setParameter("ALLOWED_ORDERS", Arrays.asList(Order.PAYED_ONLINE_ORDER_STATUS));
            q.setParameter("ORDER_TYPE", Order.ONLINE_TYPE);
            q.setParameter("DATE_THRESHOLD", new Date(System.currentTimeMillis()-
                    (OnlineHelp.RELAXATION_TIME+OnlineHelp.MAX_DURATION_TIME)*1000*60));
            r = q.executeUpdate();
            if(log.isTraceEnabled()) {
                log.trace("execute(): "+r+" online orders has been closed.");
            }
            
            // Make some online orders expired
            q = em.createQuery("update Order o set o.status=:ORDER_EXPIRED_STATUS"
                    + " where o.deadline <= :DATE_THRESHOLD"
                    + " and o.status in :ALLOWED_ORDERS"
                    + " and o.type = :ORDER_TYPE");
            q.setParameter("ORDER_EXPIRED_STATUS", Order.EXPIRED_ONLINE_ORDER_STATUS);
            q.setParameter("ALLOWED_ORDERS", Arrays.asList(Order.NEW_ONLINE_ORDER_STATUS));
            q.setParameter("ORDER_TYPE", Order.ONLINE_TYPE);
            q.setParameter("DATE_THRESHOLD", new Date(System.currentTimeMillis()-
                    (OnlineHelp.RELAXATION_TIME+OnlineHelp.MAX_DURATION_TIME)*1000*60));
            r = q.executeUpdate();
            if(log.isTraceEnabled()) {
                log.trace("execute(): "+r+" online orders has been closed.");
            }
              
            etx.commit();
            em.close();
            f.close();
            
            if(log.isTraceEnabled()) {
                log.trace("execute(): "+getName()+" finished: "+r+" rows modified");
            }
        } catch (Exception ex) {
            if(log.isDebugEnabled())
                log.debug("execute(): job execution failed!", ex);
            if(etx != null)
                etx.setRollbackOnly();
        }
        if(log.isTraceEnabled()) {
            log.trace("<< execute()");
        }
    }
    
}
