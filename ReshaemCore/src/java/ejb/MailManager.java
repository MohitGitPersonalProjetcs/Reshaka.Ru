package ejb;

import ejb.util.URLUtils;
import entity.Order;
import entity.User;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rogvold
 */
@Stateless
@TransactionAttribute(value = TransactionAttributeType.SUPPORTS)
public class MailManager implements MailManagerLocal {

    @Resource(name = "mail/myMailSession")
    private Session mailSession;
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    @EJB
    UserManagerLocal userMan;
    @EJB
    ConfigurationManagerLocal confMan;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AttachmentManager.class.getName());
    @EJB
    SubjectManagerLocal subjMan;

    @Asynchronous
    @Override
    public void sendMail(String to, String theme, String text) {
        try {
            if (log.isTraceEnabled()) {
                log.trace("sendMail(): to = " + to + "; theme = " + theme + "; text = " + text);
            }

            if (URLUtils.isValidEmail(to) == false) {
                if (log.isTraceEnabled()) {
                    log.trace("sendMail(): invalid email");
                }
                return;
            }

            if (log.isTraceEnabled()) {
                log.trace("sendMail(): try to send message;  to = " + to + " from = " + theme + " text = " + text);
            }
            // Create the message object
            Message message = new MimeMessage(mailSession);
            // Adjust the recipients. Here we have only one
            // recipient. The recipient's address must be
            // an object of the InternetAddress class.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to, false));

            // Set the message's subject
            message.setSubject(theme);

            // Insert the message's body
            message.setText(text);

            // This is not mandatory, however, it is a good
            // practice to indicate the software which
            // constructed the message.
            //  message.setHeader("X-Mailer", "My Mailer");

            // Adjust the date of sending the message
            Date timeStamp = new Date();
            message.setSentDate(timeStamp);

            // Use the 'send' static method of the Transport
            // class to send the message
            Transport.send(message);
            if (log.isTraceEnabled()) {
                log.trace("sendMail(): message sent!  to = " + to + " from = " + theme + " text = " + text);
            }
            //System.out.println("MAIL SENT !!!!!!!!!!!");
        } // Add business logic below. (Right-click in editor and choose
        // "Insert Code > Add Business Method")
        catch (MessagingException ex) {
            System.out.println("ERROR WHILE SENDING EMAIL");
            System.out.println(ex.toString());
            Logger.getLogger(MailManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Asynchronous
    @Override
    //TODO(Sabir): nullpointer exception
    public void newOrderDistribution(Long orderId) {
        Order order = em.find(Order.class, orderId);
        System.out.println("order = " + order);
        Map<String, String> filters = new HashMap();
        filters.put("userGroup", "3");
        String text = "";
        text = " "
                + "\n Поступил новый заказ."
                + "\n Предмет: " + ((order.getSubject() != null) ? order.getSubject().getSubjectName() : "не выбран") + " ."
                + "\n Нужно решить до: " + ((order.getDeadline() == null) ? "не указано" : order.getDeadline()) + " ." + ""
                //                + "\n Ожижаемая заказчиком стоимость: " + ((order.getPrice() == 0.0) ? "не указано" : order.getPrice()) + " ."
                + "\n \n" + " C уважением, Reshaka.RU"
                + "\n\n P.S. Вы можете отписаться от рассылки на нашем сайте reshaka.ru в разделе \"Мой профиль\"";
        List<User> users = userMan.getFilteredUsers(filters, 0, 1000000, null, null);

        List<User> recipients = subjMan.getUsersListBySubjectId(order.getSubject().getId());
        System.out.println("sending mail to subscribers of subject = " + order.getSubject());
        for (User user : recipients) {
            System.out.println("sending mail to " + user);
            //TODO(Sabir): filter users by subjects!
            //user = em.find(User.class, user.getId());
            System.out.println(text);
            sendMail(user.getEmail(), "Новый заказ", " Здравствуйте, " + user.getLogin() + "!" + text);
        }
    }

    @Asynchronous
    @Override
    public void sendStatusChange(Long orderId) {
        Order order = em.find(Order.class, orderId);
        String text = "", theme = "";
        int a = order.getStatus();
        switch (a) {
//            case Order.RATED_OFFLINE_ORDER_STATUS:
//                theme = "Reshaka.Ru: заказ оценён";
//                text = "Здравствуйте, " + order.getEmployer().getLogin() + " !"
//                        + "Решающий "+ order.getEmployer().getLogin() + "готов выполнить заказ (ID = " + order.getId() + ")"
//                        + "за"
//                        + "\n\n Ваш заказ (ID = " + order.getId() + ")"
//                        + " оценён решающим " + order.getEmployer().getLogin()
//                        + "\n Необходимо внести предоплату в размере 50% от стоимости заказа, либо либо вы можете пока заказ просмотрят другие решающие."
//                        + "\n\n\n C Уважением, Reshaka.RU";
//                if (order.getEmployer().getSettings().isNewStatus()) {
//                    sendMail(order.getEmployer().getEmail(), theme, text);
//                }
//                break;
//            case Order.RATED_ONLINE_ORDER_STATUS:
//                theme = "Reshaka.Ru: заказ оценён";
//                text = "Здравствуйте, " + order.getEmployer().getLogin() + " !"
//                        + "\n\n Ваш заказ (ID = " + order.getId() + ")"
//                        + " оценён решающим " + order.getEmployer().getLogin()
//                        + "\n Необходимо внести предоплату в размере 50% от стоимости заказа, либо либо вы можете пока заказ просмотрят другие решающие."
//                        + "\n\n\n C Уважением, Reshaka.RU";
//                if (order.getEmployer().getSettings().isNewStatus()) {
//                    sendMail(order.getEmployer().getEmail(), theme, text);
//                }
//                break;
            case 3:
                theme = "Reshaka.Ru: внесена предоплата";
                text = "Здравствуйте, " + order.getEmployee().getLogin() + " !"
                        + "\n\n Внесена предоплата за заказа ID = " + URLUtils.createLink(URLUtils.getOrderURL(orderId), "_blank", "" + orderId) + " ."
                        + "Можете приступать к решению."
                        + "\n\n\n C Уважением, Reshaka.RU"
                        + "\n\n P.S. Вы можете отписаться от рассылки на нашем сайте reshaka.ru в разделе \"Мой профиль\"";
                if (order.getEmployee().getSettings().isNewStatus()) {
                    sendMail(order.getEmployee().getEmail(), theme, text);
                }
                break;
            case 4:
                theme = "Reshaka.Ru: заказ выполнен";
                text = "Здравствуйте, " + order.getEmployer().getLogin() + " !"
                        + "\n\n Ваш заказ (ID = " + URLUtils.createLink(URLUtils.getOrderURL(orderId), "_blank", "" + orderId) + ")"
                        + " выполнен."
                        + "\n Теперь вы можете оплатить вторую половину заказа, после чего Вам будет доступно решение."
                        + "\n\n\n C Уважением, Reshaka.RU"
                        + "\n\n P.S. Вы можете отписаться от рассылки на нашем сайте reshaka.ru в разделе \"Мой профиль\"";
                if (order.getEmployer().getSettings().isNewStatus()) {
                    sendMail(order.getEmployer().getEmail(), theme, text);
                }
                break;
            case 5:
                theme = "заказ оплачен";
                text = "Здравствуйте, " + order.getEmployer().getLogin() + " !"
                        + "\n\n Ваш заказ (ID = " + URLUtils.createLink(URLUtils.getOrderURL(orderId), "_blank", "" + orderId) + ")"
                        + " оплачен."
                        + "\n Теперь вы можете скачать решение."
                        + "\n\n\n C Уважением, Reshaka.RU"
                        + "\n\n P.S. Вы можете отписаться от рассылки на нашем сайте reshaka.ru в разделе \"Мой профиль\"";
                if (order.getEmployer().getSettings().isNewStatus()) {
                    sendMail(order.getEmployer().getEmail(), theme, text);
                }
                break;
        }

    }

    @Asynchronous
    @Override
    public void sendMailToAdmin(String theme, String text) {
        User user = em.find(User.class, confMan.getMainAdminId());
        sendMail(user.getEmail(), theme, text);
    }

    @Asynchronous
    @Override
    public void newOrder(Long orderId) {
        if (orderId == null) {
            return;
        }
        try {
            Order order = em.find(Order.class, orderId);
            User user = order.getEmployer();
            String theme = "Размещение заказа (ID = " + orderId + ") на сайте Reshaka.Ru";
            String text = "";
            if (order.getType() == Order.OFFLINE_TYPE) {
                text = "Ваш заказ (ID = " + orderId + ") размещён на сайте Reshaka.Ru !"
                        + "\n"
                        + "\nПараметры заказа:"
                        + "\n Предмет: " + order.getSubject().getSubjectName() + ""
                        + "\n Срок: " + order.getDeadlineString() + ""
                        + "\n"
                        + "\nВ ближайшее время заказ будет оценен нашими специалистами."
                        + "\n"
                        + "\nСпасибо, что пользуетесь нашим сервисом."
                        + "\nС уважением, администрация Reshaka.Ru .";
            } else {
                text = "Ваш заказ на онлайн-помощь на экзамене (ID = " + orderId + ") размещён на сайте  Reshaka.Ru !"
                        + "\n"
                        + "\nПараметры заказа:"
                        + "\n Предмет: " + order.getSubject().getSubjectName() + ""
                        + "\n Дата и время начала: " + order.getDeadlineString() + ""
                        + "\n Продолжительность: " + order.getDuration() + " минут"
                        + "\n"
                        + "\nВ ближайшее время заказ будет оценен нашими специалистами."
                        + "\n"
                        + "\nСпасибо, что пользуетесь нашим сервисом."
                        + "\nС уважением, администрация Reshaka.Ru .";
            }

            if (URLUtils.isValidEmail(user.getEmail())) {
                sendMail(user.getEmail(), theme, text);
            }

        } catch (Exception e) {
        }

    }
}
