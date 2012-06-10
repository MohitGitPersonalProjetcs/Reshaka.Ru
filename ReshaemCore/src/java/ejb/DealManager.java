/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import entity.Offer;
import entity.Order;
import entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rogvold
 */
@Stateless
public class DealManager implements DealManagerLocal {

    @PersistenceContext(unitName = "ReshaemCorePU")
    private EntityManager em;
    @EJB
    MoneyManagerLocal monMan;
    @EJB
    MessageManagerLocal messMan;
    @EJB
    UserManagerLocal userMan;
    @EJB
    ConfigurationManagerLocal confMan;
    @EJB
    MailManagerLocal mailMan;
    @EJB
    OnlineHelpManagerLocal onlineMan;

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public boolean canStartDeal(Long employerId, Long offerId) {
        try {
            Offer offer = em.find(Offer.class, offerId);
            return monMan.enoughMoney(employerId, offer.getPrice() / 2.0);
        } catch (Exception exc) {
        }
        return false;
    }

    @Override
    public void makePrepayment(Long orderId) {
        Order order = em.find(Order.class, orderId);
        System.out.println("makePrepayment orderId = " + orderId);

        User empr = em.find(User.class, order.getEmployer().getId());
        User empe = em.find(User.class, order.getEmployee().getId());

//        empr.setOrderedAmount(empr.getOrderedAmount() + 1);
//        em.merge(empr);        

        System.out.println("order =  " + order);
        System.out.println("employer = " + empr);
        System.out.println("employee = " + empe);

        // System.out.println("Trying to make prepayment ... " + order.toString() + empr.toString() + empe.toString());

        if (order.getStatus() != 2) {
            return;
        }
        if (monMan.enoughMoney(empr.getId(), order.getPrice() / 2.0)) {

//            monMan.transferMoney(empr.getId(), empr.getId(), empe.getId(), (1 - confMan.getAdminPercent()) * (order.getPrice() / 2.0), "prepayment");
//            monMan.transferMoney(empr.getId(), empr.getId(), confMan.getMainAdminId(), confMan.getAdminPercent() * (order.getPrice() / 2.0), "prepayment");
            monMan.transferMoney(empr.getId(), empr.getId(), confMan.getMainAdminId(), order.getPrice() / 2.0, "prepayment");
//!!!

            order.setStatus(3);//half-paid
            em.merge(order);
            //sending message to employee from admin 
            messMan.sendMessage(confMan.getMainAdminId(), order.getEmployee().getId(), "Внесена предоплата за заказ " + order.getId().toString(), "Можете начинать решать", order.getConditionId());
            mailMan.sendStatusChange(order.getId());
        }

    }

    @Override
    public void startDeal(Long orderId, Long offerId) {
        Order order = em.find(Order.class, orderId);
        Offer offer = em.find(Offer.class, offerId);



        if (order.getStatus() != 1) {
            return;
        }
        if (monMan.enoughMoney(order.getEmployer().getId(), offer.getPrice() / 2.0) == false) {
            return;
        }

        User employee = em.find(User.class, offer.getUserId());
        order.setEmployee(employee);
        order.setPrice(offer.getPrice());
        order.setStatus(2); //agreed
        //TODO(Sabir): send message to reshaka


        // deleting all other offers
        List<Offer> offers = new ArrayList();
        offers.add(offer);
        order.setOffers(offers);
        em.merge(order);

        makePrepayment(orderId);
    }

    @Override
    public void postSolution(Long orderId, Long solutionId) {
        Order order = em.find(Order.class, orderId);
        if (order.getStatus() != 3) {
            return;
        }
        System.out.println("solutionId is " + solutionId);

        order.setSolutionId(solutionId);
        order.setStatus(4); // solved
        em.merge(order);
        messMan.sendMessage(confMan.getMainAdminId(), order.getEmployer().getId(), "Ваше задание решено! (id = " + order.getId().toString() + " )", "Внесите оставшуюся часть денег, чтобы скачать решение (" + order.getPrice() / 2.0 + " p.)", null);
        mailMan.sendStatusChange(order.getId());
    }

    @Override
    public void makeFinalPayment(Long orderId) {
        System.out.println("try to make final payment : orderId = " + orderId);
        Order order = em.find(Order.class, orderId);

        if (order.getStatus() != 4) {
            return;
        }

        if (monMan.enoughMoney(order.getEmployer().getId(), order.getPrice() / 2.0) == false) {
            System.out.println("can't make fin al payment");
            return;
        }
        System.out.println("transfering money");

//        monMan.transferMoney(order.getEmployer().getId(), order.getEmployer().getId(), order.getEmployee().getId(), (1 - confMan.getAdminPercent()) * (order.getPrice() / 2.0), "Final payment");
//        monMan.transferMoney(order.getEmployer().getId(), order.getEmployer().getId(), confMan.getMainAdminId(), confMan.getAdminPercent() * (order.getPrice() / 2.0), "Final payment");
        monMan.transferMoney(order.getEmployer().getId(), order.getEmployer().getId(), confMan.getMainAdminId(), order.getPrice() / 2.0, "Final payment");
        monMan.transferMoney(confMan.getMainAdminId(), confMan.getMainAdminId(), order.getEmployee().getId(), (1 - confMan.getAdminPercent()) * order.getPrice(), "70 % to reshaka");

        order.setStatus(5); //payed
        userMan.addAttachmentToUser(order.getEmployer().getId(), order.getSolutionId());
        em.merge(order);
        mailMan.sendStatusChange(order.getId());
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public String getCurrentStatus(Long orderId) {
        Order order = em.find(Order.class, orderId);
        if (order == null) {
            return "";
        }
        int status = order.getStatus();
        switch (status) {
            case 0:
                return "New";
            case 1:
                return "Rated";
            case 2:
                return "Chosen";
            case 3:
                return "Agreed";
            case 4:
                return "Solved";
            case 5:
                return "Payed";
            case 10:
                return "NewOnline";
            case 11:
                return "RatedOnline";
            case 12:
                return "AgreedOnline";
            case 13:
                return "PrepayedOnline";
            case 14:
                return "DoneOnline";
        }
        return "";
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public boolean canFinishDeal(Long orderId, Long userId) {
        boolean b = false;
        try {
            Order order = em.find(Order.class, orderId);
            if (order.getStatus() != 4) {
                b = false;
            }
            //System.out.println("canFinishDeal: order = " + order);
            //Offer offer = em.find(Offer.class, offerId);
            b = monMan.enoughMoney(userId, order.getPrice() / 2.0);
        } catch (Exception exc) {
        }
        System.out.println("canFinishDeal : userId/orderId/can : " + userId + "/" + orderId + "/" + "/" + b);
        return b;
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public String getRussianCurrentStatus(Long orderId) {
        Order order = em.find(Order.class, orderId);
        if (order == null) {
            return "";
        }
        int status = order.getStatus();
        switch (status) {
            case 0:
                return "Новый";
            case 1:
                return "Оценен";
            case 2:
                return "Выбран";
            case 3:
                return "Согласен";
            case 4:
                return "Выполнен";
            case 5:
                return "Оплачен";
            case 10:
                return "Новый";
            case 11:
                return "Оценен";
            case 12:
                return "Согласен";
            case 13:
                return "Оплачен";
            case 14:
                return "Выполнен";
        }
        return "";
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public boolean canStartOnlineDeal(Long employerId, Long offerId) {
        try {
            Offer offer = em.find(Offer.class, offerId);
            return monMan.enoughMoney(employerId, offer.getPrice());
        } catch (Exception exc) {
        }
        return false;
    }

    @Override
    public void startOnlineDeal(Long orderId, Long offerId) {
        Order order = em.find(Order.class, orderId);
        Offer offer = em.find(Offer.class, offerId);



        if (order.getStatus() != 11) {
            return;
        }
        if (monMan.enoughMoney(order.getEmployer().getId(), offer.getPrice()) == false) {
            return;
        }

        User employee = em.find(User.class, offer.getUserId());
        order.setEmployee(employee);
        order.setPrice(offer.getPrice());
        order.setStatus(12); //agreed
        // deleting all other offers
        List<Offer> offers = new ArrayList();
        offers.add(offer);
        order.setOffers(offers);
        em.merge(order);

        makeOnlinePrepayment(orderId);
    }

    @Override
    public void makeOnlinePrepayment(Long orderId) {
        Order order = em.find(Order.class, orderId);
        System.out.println("makeOnlinePrepayment orderId = " + orderId);

        User empr = em.find(User.class, order.getEmployer().getId());
        User empe = em.find(User.class, order.getEmployee().getId());

        if (order.getStatus() != 12) {
            return;
        }
        if (monMan.enoughMoney(empr.getId(), order.getPrice())) {

            monMan.transferMoney(empr.getId(), empr.getId(), confMan.getMainAdminId(), order.getPrice(), "prepayment for online help");
            order.setStatus(13);// 100 % prepayed online
            em.merge(order);

            onlineMan.addNewBasicOnlineHelp(orderId); // authomatical adding OnlineHelp

            //sending message to employee from admin 
            String text;
            text = "Внесена предоплата за \"Онлайн - помошь\" . Номер заказа :" + order.getId() + " ."
                    + "\n Дата: " + order.getDeadlineString() + " ."
                    + "\n Продолжительность: " + order.getDuration() + " минут."
                    + "\n Будьте на связи в указанное время. ";

            messMan.sendMessage(confMan.getMainAdminId(), order.getEmployee().getId(), text, "Вас выбрали исполнителем на онлайн - помощь (ID заказа = " + order.getId() + "). Будьте вовремя в указанное время", order.getConditionId());
            //TODO(Sabir): sendMail...
        }
    }

    @Override
    public void finishOnlineDeal(Long orderId) {
        Order order = em.find(Order.class, orderId);
        User empe = em.find(User.class, order.getEmployee().getId());
        User admin = em.find(User.class, confMan.getMainAdminId());

        if (order.getStatus() != 13) {
            return;
        }

        monMan.transferMoney(admin.getId(), admin.getId(), empe.getId(), order.getPrice() * (1 - confMan.getAdminPercent()), "Оплата за онлайн - помощь");
        order.setStatus(14);
        em.merge(order);

        //TODO(Sabir) sendMail
    }
}
