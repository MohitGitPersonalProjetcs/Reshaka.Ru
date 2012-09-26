/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import ejb.util.MoneyUtils;
import entity.ExternalMoneyTransaction;
import entity.MoneyTransaction;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.InvalidTokenException;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.api.enums.MoneyDirection;
import ru.yandex.money.api.response.OperationDetailResponse;
import ru.yandex.money.api.response.OperationHistoryResponse;
import ru.yandex.money.api.response.util.Operation;

/**
 *
 * @author rogvold
 */
/**
 * status in MoneyTransaction <br/> 0 - this is internal transaction (between
 * users inside our system)<br/>
 *
 * The other statuses are for filling up user's balance <br/> 1 - user sent a
 * request to fill up his balance (money from administrator to user) <br/> 2
 * -money have come from payment system to administrator external bill <br/> 3
 * -money have filled up to user (transaction is closed)<br/>
 *
 * The other statuses are for filling out user's balance <br/> 4 - user sent a
 * request to fill out his money <br/> 5 - administrator has payed from his
 * external wallet to external user's wallet <br/> 6 - transaction is closed
 */
@Stateless
public class MoneyManager implements MoneyManagerLocal {

    private static Logger log = Logger.getLogger(MoneyManager.class);
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    @EJB
    ConfigurationManagerLocal confMan;
    @EJB
    MessageManagerLocal messMan;
//    @Resource(name = "mail/wmMailSession")
//    private Session mailSession;

    /**
     * transferMoney is used only for internal transactions (it creates new
     * transaction with status = 0)
     *
     * @param ownerId
     * @param fromId
     * @param toId
     * @param money
     * @param description
     */
    @Override
    public void transferMoney(Long ownerId, Long fromId, Long toId, double money, String description) {
        if (log.isTraceEnabled()) {
            log.trace("transferMoney(): fromId/toId/money/description" + fromId + "/" + toId + "/" + money + "/" + description);
        }
        if (ownerId == null) {
            ownerId = confMan.getMainAdminId();
        }
        if (fromId == null) {
            fromId = confMan.getMainAdminId();
        }

        User fromUser = em.find(User.class, fromId);
        User toUser = em.find(User.class, toId);

        User owner = em.find(User.class, ownerId);

        if (fromUser.getCurrentBalance() < money) {
            System.out.println("Not enough money! ");
            return;
        }
        MoneyTransaction monTr = new MoneyTransaction();
        monTr.setOwner(owner);
        monTr.setDescription(description);
        monTr.setMoney(money);
        monTr.setFromId(fromId);
        monTr.setToId(toId);
        monTr.setStatus(0);
        Date date = new Date();
        monTr.setCreationDate(date);
        monTr.setChangesDate(date);
        em.persist(monTr); // I hope that all dependencies will be merged

        fromUser.setCurrentBalance(fromUser.getCurrentBalance() - money);
        toUser.setCurrentBalance(toUser.getCurrentBalance() + money);
        em.merge(fromUser);
        em.merge(toUser);
    }

    @Override
    public void sendRequestToFillUp(Long userId, double money, String description) {
        MoneyTransaction tr = new MoneyTransaction();

        tr.setOwner(em.find(User.class, confMan.getMainAdminId())); // admin ownes all external transactions !
        tr.setFromId(confMan.getMainAdminId()); // only Main Admin can work with external billing
        tr.setToId(userId);
        tr.setMoney(money);
        tr.setStatus(1);
        tr.setDescription(description);
        Date date = new Date();
        tr.setCreationDate(date);
        tr.setChangesDate(date);
        em.persist(tr);

        messMan.sendMessage(confMan.getMainAdminId(), userId, "Денежные операции", "Перечислите деньги, используя платежные системы", null);
    }

    /**
     * for main administrator only ! <br/> transfer money to user after external
     * money have reached administrator's wallet )
     *
     * @param transactionId
     */
    @Override
    public void fillUp(Long transactionId) {
        MoneyTransaction tr = em.find(MoneyTransaction.class, transactionId);
        if (tr.getStatus() != 2) {
            return; // money have already come to administrator from external bill
        }
        User user = em.find(User.class, tr.getToId());
        User admin = em.find(User.class, tr.getFromId());

        user.setCurrentBalance(user.getCurrentBalance() + tr.getMoney());
        admin.setCurrentBalance(admin.getCurrentBalance() - tr.getMoney());
        em.merge(admin);
        em.merge(user);

        tr.setStatus(3); // transaction is closed (our physical money reached the point)
        Date date = new Date();
        tr.setChangesDate(date);

        em.merge(tr);

        messMan.sendMessage(confMan.getMainAdminId(), tr.getToId(), "Денежные операции", "Вам зачислены средства в размере " + tr.getMoney() + " руб.", null);
    }

    /**
     * transactionId this method should be launched when money have come from
     * external space to main administrator
     *
     * @param transactionId should be inside an external transaction check
     */
    @Override
    public void ExternalIncoming(Long transactionId) {
        MoneyTransaction tr = em.find(MoneyTransaction.class, transactionId);
        if (tr.getStatus() != 1) {
            return;
        }

        Date date = new Date();
        tr.setChangesDate(date);
        tr.setStatus(2);
        em.merge(tr);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public boolean enoughMoney(Long userId, double money) {
        User user = em.find(User.class, userId);
        if (user.getCurrentBalance() >= money) {
            return true;
        }
        return false;
    }

    @Override
    public void sendRequestToFillOut(Long userId, double money) {
        if (!enoughMoney(userId, money)) {
            return;
        }
        User admin = em.find(User.class, confMan.getMainAdminId());

        MoneyTransaction tr = new MoneyTransaction();
        tr.setMoney(money);
        tr.setOwner(admin); // admin ownes all external transactions ! Yeah
        tr.setFromId(userId);
        tr.setToId(admin.getId());
        tr.setDescription("I want to fill out my money");
        Date date = new Date();
        tr.setCreationDate(date);
        tr.setChangesDate(date);
        tr.setStatus(4);

        em.persist(tr);
        messMan.sendMessage(userId, admin.getId(), "Денежные операции", "Мне нужно вывести деньги", null);
        messMan.sendMessage(admin.getId(), userId, "Денежные операции", "Заявка принята", null);
    }

    @Override
    public void ExternalOutcoming(Long transactionId) {
        MoneyTransaction tr = em.find(MoneyTransaction.class, transactionId);
        if (tr.getStatus() != 4) {
            return;
        }

        Date date = new Date();
        tr.setChangesDate(date);

        tr.setStatus(5);
        em.merge(tr);
    }

    @Override
    public void fillOut(Long transactionId) {
        MoneyTransaction tr = em.find(MoneyTransaction.class, transactionId);
        if (tr.getStatus() != 5) {
            return;
        }

        User admin = em.find(User.class, tr.getToId());
        User user = em.find(User.class, tr.getFromId());

        admin.setCurrentBalance(admin.getCurrentBalance() + tr.getMoney());
        user.setCurrentBalance(user.getCurrentBalance() - tr.getMoney());
        em.merge(admin);
        em.merge(user);

        Date date = new Date();
        tr.setChangesDate(date);

        tr.setStatus(6); //money transaction is closed
        em.merge(tr);

        messMan.sendMessage(admin.getId(), user.getId(), "Денежные операции", "Сумма успешно выведена ( " + tr.getMoney() + " )", null);
    }

    @Override
    public void updateMoney() {
        updateYandexMoney();
        updateWebmoney();
    }

    @Asynchronous
    private void updateYandexMoney() {
        try {
            List<Operation> list = getFreshYandexMoneyOperations();
            for (Operation op : list) {
                processExternalYandexMoneyOperation(op);
            }
            confMan.setString("YandexLastOperationId", ((Operation) list.get(0)).getOperationId());

            if (log.isTraceEnabled()) {
                log.trace("updateYandexMoney(): money updated!");
            }
        } catch (Exception exc) {
            if (log.isTraceEnabled()) {
                log.trace("updateYandexMoney(): Exception occured");
            }
        }
    }

    @Asynchronous
    private void updateWebmoney() {
        try {
            List<ExternalMoneyTransaction> list = getFreshWebmoneyOperations();
            
            System.out.println("updateWM list = " + list);
            
            for (ExternalMoneyTransaction op : list) {
                processExternalWebmoneyMoneyOperation(op);
            }

            confMan.setDate("WebmoneyLastMessageReceivedDate", ((ExternalMoneyTransaction) list.get(0)).getOperationDate());

            if (log.isTraceEnabled()) {
                log.trace("updateWebmoney(): money updated!");
            }
        } catch (Exception exc) {
            if (log.isTraceEnabled()) {
                log.trace("updateWebmoney(): Exception occured");
            }
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public String getLinkForYandexPayment(Long userId, double money) {
        money=   Math.ceil(100* money*1.005) / 100.0;
        String DIRECT_PAYMENT_URI =
                "https://money.yandex.ru/direct-payment.xml?"
                + "isDirectPaymentFormSubmit=true&"
                + "ErrorTemplate=ym2xmlerror&"
                + "ShowCaseID=7&"
                + "SuccessTemplate=ym2xmlsuccess&"
                + "isViaWeb=true&js=0&"
                + "p2payment=1&"
                + "rnd=595587893&"
                + "scid=767&"
                + "secureparam5=5&"
                + "shn=ShowcaseName&"
                + "showcase_comm=0.5%25&"
                + "suspendedPaymentsAllowed=true&"
                + "targetcurrency=643&";
        List<NameValuePair> params = new ArrayList<>();
        String title = "Пополнение счета. ID = [" + Long.toString(userId) + "]";
        String dest = confMan.getString("YandexPurse");
        params.add(new BasicNameValuePair("FormComment", title));
        params.add(new BasicNameValuePair("destination", title));
        params.add(new BasicNameValuePair("short-dest", "пополнение счета в системе Reshaka.Ru"));
        params.add(new BasicNameValuePair("receiver", dest));
        params.add(new BasicNameValuePair("sum", Double.toString(money)));
        return DIRECT_PAYMENT_URI + URLEncodedUtils.format(params, "UTF-8");
    }

    private List<ExternalMoneyTransaction> getFreshWebmoneyOperations() {
        Date lastDate = confMan.getDate("WebmoneyLastMessageReceivedDate");
        List<ExternalMoneyTransaction> list = new ArrayList();
        try {
            Properties props = System.getProperties();

            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);

            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "wmreshaka@gmail.com", "reshaemmipt2B");

            System.out.println("store isConneceted =  " + store.isConnected());
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int lastMessageNumber = inbox.getMessageCount();
            int i = inbox.getMessageCount();
            Date currentDate = new Date();
            Message message;
            Long userId;
            Double money;
            String content, hash;

            while ((i > 0) && (currentDate.after(lastDate))) {
                
                System.out.println("i = " + i + " currentDate = " + currentDate + " lastDate = " + lastDate);
                
                message = inbox.getMessages(i, i)[0];
                if (message.getFrom()[0].toString().indexOf("merchant@webmoney.ru") < 0) {
                    i--;
                    continue;
                }
                System.out.println("next circle");
                currentDate = message.getReceivedDate();

                i--;
                content = message.getContent().toString();
                userId = MoneyUtils.getWebmoneyLMIPaymentNO(content);
                money = MoneyUtils.getWebmoneyLMIPaymentAmount(content);
                hash = MoneyUtils.getWebmoneyLMIHash(content);
                list.add(new ExternalMoneyTransaction(hash, money, message.getReceivedDate(), userId));
//                System.out.println("content : " + message.getContent());
            }
        } // Add business logic below. (Right-click in editor and choose
        catch (Exception ex) {
            System.out.println("ERROR WHILE receiving EMAIL");
            System.out.println("exception: " + ex);
            return null;
        }
        return list;
    }

    @Asynchronous
    private void processExternalWebmoneyMoneyOperation(ExternalMoneyTransaction webmoneyTransaction) {
        System.out.println("processExternalWebmoneyMoneyOperation -> transaction userId/money " + webmoneyTransaction.getUserId() + "/"+webmoneyTransaction.getMoney());
        
        Query q = em.createNamedQuery("getExternalTransactionByPaymentSystemAndOperationId").setParameter("system", "webmoney");
        q.setParameter("operationId", webmoneyTransaction.getOperationId());
        List<ExternalMoneyTransaction> l = q.getResultList();
        if (l.isEmpty() == false) {
            System.out.println("money have been filled up");
            return;
        }
        try {
            ExternalMoneyTransaction ext = new ExternalMoneyTransaction();
            ext.setMoney(webmoneyTransaction.getMoney());
            ext.setOperationDate(webmoneyTransaction.getOperationDate());
            ext.setOperationId(webmoneyTransaction.getOperationId());
            ext.setUserId(webmoneyTransaction.getUserId());
            ext.setPaymentSystem("webmoney");
            addMoney(ext.getUserId(), ext.getMoney());
            ext.setFinished(true);
            em.merge(ext);

            if (log.isTraceEnabled()) {
                log.trace("processExternalWebmoneyMoneyOperation(): added  " + ext.getMoney() + " rub to userID = " + ext.getUserId());
            }

        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("processExternalWebmoneyMoneyOperation(): exception occured exc =  " + ex);
            }
        }

    }
    
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    private List<Operation> getFreshYandexMoneyOperations() {
        ru.yandex.money.api.YandexMoney ym = new YandexMoneyImpl(confMan.getString("yandexId"));
        String token = confMan.getString("YandexToken");
        List<Operation> list = new ArrayList();
        boolean flag = false;
        int i = 0;
        Operation op;
        String endOperationId = confMan.getString("YandexLastOperationId");
        try {
            while (!flag) {
                OperationHistoryResponse resp = ym.operationHistory(token, i, 1);
                i++;

                if (resp.getOperations().isEmpty() == true) {
                    flag = true;
                    break;
                }
                op = resp.getOperations().get(0);
                if (op.getOperationId().equals(endOperationId)) {
                    flag = true;
                    break;
                }
                if (op.getDirection() == MoneyDirection.out) {
                    continue;
                }
                list.add(op);
            }
//            confMan.setString("YandexLastOperationId", ((Operation) list.get(0)).getOperationId());
            return list;
        } catch (IOException | InvalidTokenException | InsufficientScopeException ex) {
            if (log.isTraceEnabled()) {
                log.trace("<< getFreshYandexMoneyOperations(): null", ex);
            }
        }
        return null;
    }

    private Long getUserIdFromOperation(Operation operation) {
        String s = operation.getTitle();
        return null;
    }

    @Asynchronous
    private void processExternalYandexMoneyOperation(Operation operation) {
        ru.yandex.money.api.YandexMoney ym = new YandexMoneyImpl(confMan.getString("yandexId"));
        String token = confMan.getString("YandexToken");
        String operationId, s;
        Long userId;
        double money;
//         Query q = em.createNamedQuery("findByLoginAndPassword").setParameter("userLogin", login);
        Query q = em.createNamedQuery("getExternalTransactionByPaymentSystemAndOperationId").setParameter("system", "yandex");
        q.setParameter("operationId", operation.getOperationId());
        List<ExternalMoneyTransaction> l = q.getResultList();
        if (l.isEmpty() == false) {
            System.out.println("money have been filled up");
            return;
        }
        try {
            OperationDetailResponse response = ym.operationDetail(token, operation.getOperationId());
            operationId = operation.getOperationId();
            s = response.getMessage();

            if (s.indexOf("ID =") < 0) {
                return;
            }
            s = s.substring(s.indexOf("ID =") + 6);
            s = s.substring(0, s.length() - 1);
            System.out.println("s = " + s);

            userId = Long.parseLong(s);
            money = operation.getAmount().doubleValue();

            ExternalMoneyTransaction ext = new ExternalMoneyTransaction();
            ext.setMoney(money);
            ext.setOperationDate(new Date());
            ext.setOperationId(operation.getOperationId());
            ext.setUserId(userId);
            ext.setPaymentSystem("yandex");
            addMoney(userId, money);
            ext.setFinished(true);
            em.merge(ext);

            System.out.println("added" + money + " rub to " + userId);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MoneyManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    private void addMoney(Long userId, double money) {
        try {
            User user = em.find(User.class, userId);
            user.setCurrentBalance(user.getCurrentBalance() + money);
            em.merge(user);
        } catch (Exception exc) {
        }
    }

    @Override
    public void replenishMoneyToAdmin(double money) {
        User user = em.find(User.class, confMan.getMainAdminId());
        System.out.println("admin's balance before = " + user.getCurrentBalance());
        user.setCurrentBalance(user.getCurrentBalance() + money);
        em.merge(user);
        System.out.println("admin's ballance is " + user.getCurrentBalance());
        System.out.println("replenishMoneyToAdmin");
    }

    @Override
    public synchronized void testWM() {
        List<ExternalMoneyTransaction> l = getFreshWebmoneyOperations();

    }
}
