package quartz.jobs;

import ejb.AttachmentManager;
import ejb.MailManager;
import ejb.util.EJBUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ru.reshaka.core.email.MailQueue;

/**
 *
 * @author rogvold
 */
public class MailQueueJob implements ReshakaJob {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AttachmentManager.class.getName());

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Checking MailQueue. Mailing.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Mailing.");
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            Session session = (Session) EJBUtils.resolve(MailManager.MAIL_JNDI);
            MailQueue.getInstance().processQueue(session);
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("execute(): MailQueueJob exception ",e);
            }
        }


    }
}
