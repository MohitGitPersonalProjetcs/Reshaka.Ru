package ejb;

import ejb.util.FileUtils;
import ejb.util.ReshakaSortOrder;
import ejb.util.ReshakaUploadedFile;
import entity.*;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import ru.reshaka.core.email.MailQueue;

/**
 *
 * @author rogvold
 */
@Stateless
public class OrderManager implements OrderManagerLocal {

    @EJB
    AttachmentManagerLocal am;
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());
    @EJB
    MailManagerLocal mailMan;
    @EJB
    MessageManagerLocal messMan;
    @EJB
    UserManagerLocal userMan;
    @EJB
    OnlineHelpManagerLocal onlineMan;
    @EJB
    ConfigurationManagerLocal confMan;

    @Override
    public void persistEntity(Object o) {
        em.persist(o);
    }

    @Override
    public void mergeEntity(Object o) {
        em.merge(o);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public boolean offerExists(Long orderId, Long employeeId) {
        Order order = em.find(Order.class, orderId);
        List<Offer> offers = order.getOffers();
        for (Offer o : offers) {
            if (o.getUserId() == employeeId) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addOffer(Long orderId, Long employeeId, double price) {
        if (price < getMinPrice(orderId)) {
            return;
        }
        Order order = em.find(Order.class, orderId);
        System.out.println("try to add offer ; price = " + price);

        if (order.getType() == 0) {
            order.setStatus(Order.RATED_OFFLINE_ORDER_STATUS);// rated
        }
        if (order.getType() == Order.ONLINE_TYPE) {
            order.setStatus(Order.RATED_ONLINE_ORDER_STATUS); //rated
        }


        List<Offer> offers = new ArrayList(order.getOffers());

        // 
        Offer offe = null;
        for (Offer o : offers) {
            if (o.getUserId().equals(employeeId)) {
                offe = o;
                break;
            }
        }
        if (offe == null) {
            Offer off = new Offer(price, employeeId);
            em.persist(off);
            offers.add(off);
            order.setOffers(offers);
            mergeEntity(order);
            offe = off;
//            mailMan.sendStatusChange(order.getId());

        } else {
            offe.setPrice(price);
            offe = em.merge(offe);
//            mailMan.sendStatusChange(order.getId());
        }
        String theme = "Reshaka.Ru: заказ оценён";
        String text = "";
        if (order.getType() == Order.OFFLINE_TYPE) {
            text = "Здравствуйте, " + order.getEmployer().getLogin() + " !"
                    + "\n\nРешающий " + getLoginById(offe.getUserId()) + " готов выполнить заказ (ID=" + order.getId() + ")"
                    + " за " + offe.getPrice() + " р."
                    + "\n\n Необходимо внести предоплату в размере 50% от стоимости заказа, либо вы можете подождать пока заказ просмотрят другие решающие."
                    + "\n\n\n C уважением, администрация Reshaka.RU"
                    + "\n\nПожалуйста, не отвечайте на данное сообщение. По всем вопросам обращаться к пользователю admin.";
        }
        if (order.getType() == Order.ONLINE_TYPE) {
            text = "Здравствуйте, " + order.getEmployer().getLogin() + " !"
                    + "\n\nРешающий " + getLoginById(offe.getUserId()) + " готов выполнить заказ на онлайн-помощь (ID=" + order.getId() + ")"
                    + " за " + offe.getPrice() + " р."
                    + "\n\n Необходимо внести предоплату (100% стоимости заказа), либо вы можете подождать пока заказ просмотрят другие решающие."
                    + "\n\n\n C уважением, администрация Reshaka.RU"
                    + "\n\nПожалуйста, не отвечайте на данное сообщение. По всем вопросам обращаться к пользователю admin.";
        }
        mailMan.sendMail(order.getEmployer().getEmail(), theme, text);
        messMan.sendMessage(confMan.getMainAdminId(), order.getEmployer().getId(), theme, text, null);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<User> getEmployees(Long orderId) {
        try {
            List<Offer> offers = getOffers(orderId);
            Set<String> set = new HashSet();
            List<User> list = new ArrayList();
            User us;
            for (Offer o : offers) {
                us = em.find(User.class, o.getUserId()); //не очень оптимально , а по другому никак ..
                // set.add(us.getLogin());
                list.add(us);
            }
            return list;
        } catch (Exception exc) {
        }
        return null;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Offer> getOffers(Long orderId) {
        try {
            Order order = em.find(Order.class, orderId);
            System.out.println("getOffers(): order = " + order);
            List<Offer> offers = order.getOffers();
            System.out.println("offers = " + offers);
            return offers;
        } catch (Exception exc) {
            return null;
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public double getMinPrice(Long orderId) {
        System.out.println("ejb: orderId is " + orderId);
        List<Offer> set = getOffers(orderId);
        if (set == null) {
            return -1;
        }
        System.out.println("Try to get min Price");
        System.out.println("set is " + set);
        double min = Double.MAX_VALUE, f = -1;
        for (Offer o : set) {
            System.out.println("f = " + f);
            f = o.getPrice();
            if (f < min) {
                min = f;
            }
        }
        Order ord = em.find(Order.class, orderId);
        if (min == Double.MAX_VALUE) {
            min = ord.getPrice();
        }

        if (log.isTraceEnabled()) {
            log.trace("<<getMinPrice(orderId): orderId"
                    + "\n>>getMinPrice(orderId): min");
        }
        return min;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public String getLoginById(Long userId) {
        // todo plug
        System.out.println("order.userId = " + userId);
        User user;
        try {
            user = em.find(User.class, userId);
            return user.getLogin();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public String getSubjectById(Long subjectId) {
        Subject subj = em.find(Subject.class, subjectId);
        return subj.getSubjectName();
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> getAllOrders() {
        System.out.println("get all orders occured");
        Map filter = new HashMap();
        //filter.put("type", "0");
        return getFilteredOrders(filter, 0, 1000000, "id", ReshakaSortOrder.DESCENDING);
    }

    @Override
    public Order submitOrder(Order order, List<ReshakaUploadedFile> files) {
        //System.out.println("ejb - submitOrder - order = " + order);
        if (files != null) {
            Attachment att = am.uploadFiles(order.getEmployer(), files, order.getTags());
            if (att != null) {
                order.setConditionId(att.getId());
                //(Danon): naming of problem statemets "problem_PROBLEM_ID.ext"
                am.renameAttachment(order.getEmployer().getId(), att.getId(), "problem_" + att.getId() + FileUtils.extractExtention(att.getName()));
            }
        }

        User empr = em.find(User.class, order.getEmployer().getId());
        empr.setOrderedAmount(empr.getOrderedAmount() + 1); //TODO(Sabir): bad style((( (Danon): ;) +1
        em.merge(empr);

        em.persist(order);

//        System.out.println("distribution from order manager : order = " + order);
//        mailMan.newOrderDistribution(order.getId()); // send messages to all employees TODO(Sabir): NullPointer Exception occured
        try {
            Subject sub = em.find(Subject.class, order.getSubject().getId());
            order.setSubject(sub);
            order = em.merge(order);

            String mailText =  "Заказ (ID=" + order.getId() + ") по предмету " + sub.getSubjectName() + " размещен в системе Reshaka.Ru ."
                    + "<br/><br/>" + ( (order.getType() == Order.OFFLINE_TYPE) ? "Дедлайн: " + order.getDeadlineString() : "Начало экзамена: " + order.getDeadlineString() + ""
                        + "<br/>Продолжительность: " + order.getDuration()  + " мин.") +""
                    + "<br/><br/><a href=\"http://reshaka.ru/download.xhtml?id="+order.getConditionId()+"\" >" + ( (order.getType() == Order.OFFLINE_TYPE) ? "Условие задачи" : "Примеры задач") + "</a> <br/>";

            MailQueue.getInstance().addSubscribersMail(sub, ( (order.getType() == Order.OFFLINE_TYPE) ? "Новый заказ в системе Reshaka.Ru" : "Новый заказ на онлайн-помощь в системе Reshaka.Ru"), mailText);

        } catch (Exception exc) {
        }



        mailMan.newOrder(order.getId());
        messMan.sendMessage(confMan.getMainAdminId(), order.getEmployer().getId(), "Размещение заказа", "Ваш заказ (ID=" + order.getId() + ") успешно размещен в системе. Дождитесть пока он не будет оценен решающим", null);
        return order;
    }

    @Override
    public Order submitSolution(Long userId, Long orderId, Long solutionId) {
        //TODO(sabir): security check
        System.out.println("EJB try to submit solution orderId = " + orderId);
        Order order = em.find(Order.class, orderId);
        order.setSolutionId(solutionId);
        //(Danon): naming of solution files "solution_SOLUTION_ID.ext"
        am.renameAttachment(userId, solutionId, "solution_" + solutionId + FileUtils.extractExtention(em.find(Attachment.class, solutionId).getName()));
//        if (files != null) {
//            Attachment att = am.uploadFiles(order.getEmployee(), files, order.getTags());
//            if (att != null) {
//                order.setSolutionId(att.getId());
//            }
//        }
        em.merge(order);
        String theme = "Заказ ID=" + orderId + " выполнен";
        String text = "Здравствуйте, " + order.getEmployer().getLogin() + " !"
                + "\n\nРешение заказа ID=" + orderId + " было загружено на сайт. "
                + "\nПосле оплаты вы сможете его скачать, нажав конпку информации о заказе (i) -> скачать решение. "
                + "\n\n\n C уважением, администрация Reshaka.RU.";
        mailMan.sendMail(order.getEmployer().getEmail(), theme, text);
        messMan.sendMessage(confMan.getMainAdminId(), order.getEmployer().getId(), theme, text, null);
        return order;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Order getOrderByStringId(String stringId) {
        Long orderId = Long.parseLong(stringId);
        try {
            Order order = em.find(Order.class, orderId);
            return order;
        } catch (Exception exc) {
            return null;
        }
    }

    /**
     *
     * @param filters filterPloperty : <br/> "employer.id" "employee.id"
     * "subject.id" "status" "id"
     *
     * @return
     */
    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> getFilteredOrders(Map<String, String> filters, int first, int pageSize, String sortField, ReshakaSortOrder sortOrder) {
        String jpqlString = "select o from Order o where 'plug' = 'plug' ";
        String filterProperty, filterValue;
        System.out.println("filters = " + filters);
        try {
            //filtering
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    filterProperty = it.next();
                    filterValue = filters.get(filterProperty);
                    jpqlString += " and o." + filterProperty + " = " + filterValue;
                }
            }
            // ranging
            if (sortField != null) {
                jpqlString += " order by o." + sortField + " ";
                if (sortOrder != null) {
                    if (sortOrder == ReshakaSortOrder.ASCENDING) {
                        jpqlString += "ASC";
                    }
                    if (sortOrder == ReshakaSortOrder.DESCENDING) {
                        jpqlString += "DESC";
                    }
                    if (sortOrder == ReshakaSortOrder.UNSORTED) {
                        jpqlString += "ASC";
                    }
                }
            }
        } catch (Exception exc) {
            System.out.println(exc.toString());
        } //hardcode

        //setting bounds
        System.out.println("jpql = " + jpqlString);
        Query q = em.createQuery(jpqlString);
        q.setMaxResults(first + pageSize + 1);
        List<Order> data = q.getResultList();

        // System.out.println("orders = " + orders);
        int dataSize = (data == null) ? 0 : data.size();
        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        } else {
            return data;
        }

    }

    @Override
    public void deleteOrder(Long orderId) {
        Order order = em.find(Order.class, orderId);
        User user = em.find(User.class, order.getEmployer().getId());
        user.setOrderedAmount(user.getOrderedAmount() - 1);
        em.merge(user);
        em.remove(order);
    }

    @Override
    public Order updateOrder(Long orderId, Order order) {
        Order oldOrder = em.find(Order.class, orderId);
        oldOrder.setDeadline(order.getDeadline());
        oldOrder.setDescription(order.getDescription());
        //oldOrder.setSubject(order.getSubject());
        oldOrder.setConditionId(order.getConditionId());// не уверен...
        order = em.merge(order);
        return order;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> myOrders(Long userId) {
        //User user = em.find(User.class, userId);
        Map<String, String> map = new HashMap();
        map.put("employer.id", Long.toString(userId));
        return getFilteredOrders(map, 0, 100000, "id", ReshakaSortOrder.DESCENDING);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> getOnlineOrders() {
        Map<String, String> map = new HashMap();
        map.put("type", "1");
        return getFilteredOrders(map, 0, 100000, "id", ReshakaSortOrder.DESCENDING);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> getOrdersOfReshaka(Long userId) {
        Query q = em.createNamedQuery("getOrdersOfReshaka").setParameter("userId", userId);
        List<Order> list = q.getResultList();
        System.out.println("getOrdersOfReshaka resultList = " + list);
        return list;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> myOnlineOrders(Long userId) {
        if (log.isTraceEnabled()) {
            log.trace(">>myOnlineOrders(): userId = " + userId);
        }
        User user = em.find(User.class, userId);
        try {
            if (user.getUserGroup() == 3) {
                Query q = em.createNamedQuery("getOnlineOrdersOfReshaka").setParameter("userId", userId);
                return q.getResultList();
            }
        } catch (Exception e) {
        }


        Map<String, String> map = new HashMap();
        map.put("type", "1");
        map.put("employer.id", Long.toString(userId));
        return getFilteredOrders(map, 0, 100000, "id", ReshakaSortOrder.DESCENDING);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> myOfflineOrders(Long userId) {
        if (log.isTraceEnabled()) {
            log.trace(">>myOfflineOrders(): userId = " + userId);
        }
        User user = em.find(User.class, userId);
        try {
            if (user.getUserGroup() == 3) {
                Query q = em.createNamedQuery("getOfflineOrdersOfReshaka").setParameter("userId", userId);
                return q.getResultList();
            }
        } catch (Exception e) {
        }
        Map<String, String> map = new HashMap();
        map.put("type", "0");
        map.put("employer.id", Long.toString(userId));
        return getFilteredOrders(map, 0, 100000, "id", ReshakaSortOrder.DESCENDING);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> getOfflineOrders() {
        System.out.println("get all orders occured");
        Map filter = new HashMap();
        filter.put("type", "0");
        return getFilteredOrders(filter, 0, 1000000, "id", ReshakaSortOrder.DESCENDING);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public int getSolvedOrdersAmountOfReshaka(Long reshakaId) {
        Query q = em.createNamedQuery("getSolvedOrdersIdOfReshaka").setParameter("reshakaId", reshakaId);
        List<Long> list = q.getResultList();
        return list.size();
    }

    /**
     * @param filters key is one of the following: "subject" , "status"
     * @param userId - user id of user or reshaka (null is for all orders in
     * system)
     * @param orderType : -1 - for all orders (both types); 0 - for offline
     * orders ; 1 - for online orders
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return List of Orders
     */
    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Order> getOrders(Map<String, String> filters, Long userId, int orderType, int first, int pageSize, String sortField, ReshakaSortOrder sortOrder) {
        int userGroup = userMan.getGroupById(userId);
        // depends on userGroup
        String jpqlString = "";
        String base = "o.";
        //
        if (userId != null) {
            if (userMan.getGroupById(userId) == User.RESHAKA) {
                jpqlString = "select off.order from Offer off where off.userId=" + userId.toString() + " ";
                base = "off.order.";
            }
            if (userMan.getGroupById(userId) == User.USER) {
                jpqlString = "select o from Order o where o.employer.id=" + userId.toString() + " ";
                base = "o.";
            }
            if (userMan.getGroupById(userId) == User.ADMIN) {
                jpqlString = "select o from Order o where 'plug' = 'plug' ";
                base = "o.";
            }
        } else {
            jpqlString = "select o from Order o where 'plug' = 'plug' ";
        }
        //defining the type of an order
        if ((orderType == 1) || (orderType == 0)) {
            jpqlString += "and " + base + "type=" + orderType + " ";
        }

        //filtering
        String filterProperty, filterValue;
        try {
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    filterProperty = it.next();
                    filterValue = filters.get(filterProperty);
                    if (filterProperty.equals("subject")) {
                        filterProperty = "subject.subjectName";
                    }
                    jpqlString += " and " + base + filterProperty + " = " + filterValue;
                }
            }
            // ranging
            if (sortField != null) {
                jpqlString += " order by " + base + sortField + " ";
                if (sortOrder != null) {
                    if (sortOrder == ReshakaSortOrder.ASCENDING) {
                        jpqlString += "ASC";
                    }
                    if (sortOrder == ReshakaSortOrder.DESCENDING) {
                        jpqlString += "DESC";
                    }
                    if (sortOrder == ReshakaSortOrder.UNSORTED) {
                        jpqlString += "ASC";
                    }
                }
            }
        } catch (Exception exc) {
            System.out.println(exc.toString());
        } //hardcode
        //setting bounds
        System.out.println("jpql = " + jpqlString);
        Query q = em.createQuery(jpqlString);
        q.setMaxResults(first + pageSize + 1);
        List<Order> data = q.getResultList();

        int dataSize = (data == null) ? 0 : data.size();
        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        } else {
            return data;
        }
    }

    /**
     * @param filters key is one of the following: "subject" , "status"
     * @param userId - user id of user or reshaka (null is for all orders in
     * system)
     * @param orderType : -1 - for all orders (both types)
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return size of List of Orders
     */
    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public int getOrdersCount(Map<String, String> filters, Long userId, int orderType, int first, int pageSize, String sortField, ReshakaSortOrder sortOrder) {
        int userGroup = userMan.getGroupById(userId);
        // depends on userGroup
        String jpqlString = "";
        String base = "o.";
        //
        if (userId != null) {
            if (userMan.getGroupById(userId) == User.RESHAKA) {
                jpqlString = "select count(off.order) from Offer off where off.userId=" + userId.toString() + " ";
                base = "off.order.";
            }
            if (userMan.getGroupById(userId) == User.USER) {
                jpqlString = "select count(o) from Order o where o.employer.id=" + userId.toString() + " ";
                base = "o.";
            }
            if (userMan.getGroupById(userId) == User.ADMIN) {
                jpqlString = "select count(o) from Order o where 'plug' = 'plug' ";
                base = "o.";
            }
        } else {
            jpqlString = "select count(o) from Order o where 'plug' = 'plug' ";
        }
        //defining the type of an order
        if ((orderType == 1) || (orderType == 0)) {
            jpqlString += "and " + base + "type=" + orderType + " ";
        }

        //filtering
        String filterProperty, filterValue;
        try {
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    filterProperty = it.next();
                    filterValue = filters.get(filterProperty);
                    if (filterProperty.equals("subject")) {
                        filterProperty = "subject.subjectName";
                    }
                    jpqlString += " and " + base + filterProperty + " = " + filterValue;
                }
            }
            // ranging
            if (sortField != null) {
                jpqlString += " order by " + base + sortField + " ";
                if (sortOrder != null) {
                    if (sortOrder == ReshakaSortOrder.ASCENDING) {
                        jpqlString += "ASC";
                    }
                    if (sortOrder == ReshakaSortOrder.DESCENDING) {
                        jpqlString += "DESC";
                    }
                    if (sortOrder == ReshakaSortOrder.UNSORTED) {
                        jpqlString += "ASC";
                    }
                }
            }
        } catch (Exception exc) {
            System.out.println(exc.toString());
        } //hardcode

        //setting bounds
        System.out.println("jpql = " + jpqlString);
        Query q = em.createQuery(jpqlString);
        Long count = (Long) q.getSingleResult();
        return Integer.parseInt(count.toString()); // indian code
    }

    @Override
    public Order updateOrder(Order order, List<ReshakaUploadedFile> files) {
        //System.out.println("ejb - submitOrder - order = " + order);
        if (log.isTraceEnabled()) {
            log.trace(">>updateOrder(): order =  " + order + " ; files = " + files);
        }
        if ((files != null) && (files.isEmpty() == false)) {
            Attachment att = am.reuploadFiles(order.getEmployer(), order.getConditionId(), files, order.getTags());
//            Attachment att = am.uploadFiles(order.getEmployer(), files, order.getTags());
            if (att != null) {
                order.setConditionId(att.getId());
            }
        }
        em.merge(order);

        return order;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public boolean userOwnsThisOrder(Long userId, Long orderId) {
        if ((userId == null) || (orderId == null)) {
            return false;
        }
        Order order = em.find(Order.class, orderId);
        return (order.getEmployer().getId().equals(userId));
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Order getOrderById(Long orderId) {
        return (orderId == null) ? null : em.find(Order.class, orderId);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Subject getSubjectByOrderId(Long orderId) {
//        if (log.isTraceEnabled()) {
//            log.trace(">> getSubjectByOrderId(): orderId =  " + orderId);
//        }
        if (orderId == null) {
            return null;
        }
        Order order = em.find(Order.class, orderId);
//        if (log.isTraceEnabled()) {
//            log.trace("getSubjectByOrderId(): order =  " + order);
//        }
        if (order == null) {
            return null;
        }
        Subject subject = order.getSubject();
//        if (log.isTraceEnabled()) {
//            log.trace("getSubjectByOrderId(): subject =  " + subject);
//        }
        return subject;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public int getOrderedAmount(Long userId) {

        try {
            Query q = em.createQuery("select o.id from Order o where o.employer.id=" + userId);
            return q.getResultList().size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public boolean isExpired(Long orderId) {
        try {
            Order order = em.find(Order.class, orderId);
            return (order.getStatus() == Order.EXPIRED_OFFLINE_ORDER_STATUS || order.getStatus() == Order.EXPIRED_ONLINE_ORDER_STATUS);
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("isExpired(): exception. orderId =  " + orderId);
            }
        }
        return true;
    }

    @Override
    public void deleteOffer(Long offerId, Long orderId) {
        try {
//            Offer offer  = em.find(Offer.class, offerId);
//            em.remove(offer);
//            if (log.isTraceEnabled()) {
//                log.trace("EJB: deleteOffer(): offer id="+offerId+" removed");
//            }
            Order order = em.find(Order.class, orderId);

            List<Offer> offers = new ArrayList(order.getOffers());
            if (offers.size() == 1) {
                if (order.getType() == Order.OFFLINE_TYPE) {
                    order.setStatus(Order.NEW_OFFLINE_ORDER_STATUS);// new
                }
                if (order.getType() == Order.ONLINE_TYPE) {
                    order.setStatus(Order.NEW_ONLINE_ORDER_STATUS); //new
                }
            }

            for (int i = 0; i < offers.size(); i++) {
                if (offers.get(i).getId().equals(offerId)) {
                    offers.remove(i);
                    break;
                }
            }
            order.setOffers(offers);
            em.merge(order);

        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("EJB: deleteOffer(): exception = " + e);
            }
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Subject getFuckingSubjectById(Long subjectId) {
        Subject subj = em.find(Subject.class, subjectId);
        return subj;
    }
}
