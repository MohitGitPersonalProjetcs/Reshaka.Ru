package ejb;

import entity.OnlineHelp;
import entity.Order;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author rogvold
 */
@Stateless
public class OnlineHelpManager implements OnlineHelpManagerLocal {

    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    @EJB
    OrderManagerLocal orderManger;
    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());

    private Date incrementMinutes(Date date, int minutes) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MINUTE, minutes);
            return cal.getTime();
        } catch (Exception exc) {
            return null;
        }
    }

    @Override
    public void addNewOnlineHelp(String type, String srcAddr, String dstAddr, Date startDate, int duration, Long orderId) {
        OnlineHelp help = new OnlineHelp();
        help.setType(type);
        help.setDstAddress(dstAddr);
        help.setSrcAdderss(srcAddr);
        help.setStartDate(incrementMinutes(startDate, -OnlineHelp.RELAXATION_TIME));
        help.setEndDate(incrementMinutes(startDate, duration + OnlineHelp.RELAXATION_TIME));
        help.setStatus(OnlineHelp.STATUS_NEW);
        help.setOrderId(orderId);
        em.persist(help);
    }

    @Override
    public void deleteOnlineHelp(Long onlineHelpId) {
        try {
            OnlineHelp help = em.find(OnlineHelp.class, onlineHelpId);
            em.remove(help);
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("deleteOnlineHelp(): Exception occured while deleteing the OnlineHelp id = " + onlineHelpId);
            }
        }
    }

    @Override
    public void addNewBasicOnlineHelp(Long orderId) {
        Order order = em.find(Order.class, orderId);
        if (order == null) {
            if (log.isTraceEnabled()) {
                log.trace("addNewBasicOnlineHelp(Long orderId): order is not found (id=" + orderId + ")");
            }
            return;
        }
        if (order.getType() != Order.ONLINE_TYPE) {
            if (log.isTraceEnabled()) {
                log.trace("addNewBasicOnlineHelp(Long orderId): order is not Online Help (id=" + orderId + ")");
            }
            return;
        }
        if (order.getStatus() < Order.PAYED_ONLINE_ORDER_STATUS) {
            if (log.isTraceEnabled()) {
                log.trace("addNewBasicOnlineHelp(Long orderId): order is not payed (id=" + orderId + ")");
            }
            return;
        }
        addNewOnlineHelp(OnlineHelp.TYPE_EMAIL, order.getEmployer().getEmail(), order.getEmployee().getEmail(), order.getDeadline(), order.getDuration(), orderId);
    }

    /**
     *
     * @param type - type of the OnlineHelp (null if we want to get Online Help
     * items of all types)
     * @param status - status (negative (-1) we don't mean status)
     * @return
     */
    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<OnlineHelp> getOnlineHelpItems(String type, int status) {
        String jpqlString = "select o from OnlineHelp o where 'plug'='plug' ";
        if (type != null) {
            jpqlString += " and o.type = " + type;
        }
        if (status >= OnlineHelp.STATUS_NEW) {
            jpqlString += " and o.status=" + status;
        }
        jpqlString += " order by o.id desc ";

        Query q = em.createQuery(jpqlString);
        return q.getResultList();
    }

    @Override
    public OnlineHelp updateOnlineHelp(OnlineHelp online) {
//        OnlineHelp help = em.find(OnlineHelp.class, online.getId());
        System.out.println("EJB updateOnlineHelp online = " + online);

        return em.merge(online);
    }

    @Override
    public void addNewOnlineHelp(String type, String srcAddr, String dstAddr, Date startDate, Date endDate) {
        System.out.println("type = " + type);
        System.out.println("srcAddr = " + srcAddr);
        System.out.println("dstAddr = " + dstAddr);

        OnlineHelp help = new OnlineHelp();
        help.setType(type);
        help.setDstAddress(dstAddr);
        help.setSrcAdderss(srcAddr);
        help.setStartDate(startDate);
        help.setEndDate(endDate);
        help.setStatus(OnlineHelp.STATUS_NEW);
        em.persist(help);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public OnlineHelp getOnlineHelpById(Long oId) {
        try {
            return em.find(OnlineHelp.class, oId);
        } catch (Exception e) {
            return null;
        }
    }
}
