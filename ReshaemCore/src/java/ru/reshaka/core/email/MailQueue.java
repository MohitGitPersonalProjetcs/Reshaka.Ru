package ru.reshaka.core.email;

import ejb.AttachmentManager;
import entity.Subject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author rogvold
 */
public class MailQueue {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AttachmentManager.class.getName());
    private static final String NOREPLY_EMAIL = "noreply@reshaka.ru";
    private static MailQueue INSTANCE = new MailQueue();
    private Queue<MyMail> queue = new PriorityQueue();

    public static MailQueue getInstance() {
        
        return INSTANCE;
    }

    public synchronized void sendMail(Session session, MyMail mail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(NOREPLY_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.getRecipient(), false));
            
            Multipart multipart = new MimeMultipart("related");
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(mail.getHtmlText(),"text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            
            message.setSubject(mail.getSubject());
            message.setSentDate(new Date());

            Transport.send(message);
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("sendMail(): exception occured while sending email " + mail.getSubject(), e);
            }
        }

    }

    public synchronized void processQueue(Session session) {
        while (!this.queue.isEmpty()) {
            MyMail currentMail = this.queue.poll();
            sendMail(session, currentMail);
        }
    }

    public synchronized void addSubscribersMail(Map<String, String> map, String subject, String text) {
        for (String key : map.keySet()) {
            MyMail mail = new MyMail(subject, text, map.get(key), key);
            addMyMail(mail);
        }
    }

    public synchronized void addSubscribersMail(Subject subj, String mailSubject, String text) {
        Map<String,String> map = MailUtils.getSubscribers(subj);
        addSubscribersMail(map, mailSubject, text);
    }

    public synchronized void addMyMail(MyMail myMail) {
        this.queue.add(myMail);
    }

    public synchronized void addMyMailList(List<MyMail> mails) {
        for (MyMail m : mails) {
            addMyMail(m);
        }
    }
}