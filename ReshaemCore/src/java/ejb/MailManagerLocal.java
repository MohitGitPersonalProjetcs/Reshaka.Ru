package ejb;

import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface MailManagerLocal {
    public void sendMail(String to, String theme, String text);
    public void sendMailToAdmin(String theme , String text);
    public void newOrderDistribution(Long orderId);
    public void sendStatusChange(Long orderId);
}
