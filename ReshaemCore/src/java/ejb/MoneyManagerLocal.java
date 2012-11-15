/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface MoneyManagerLocal {

    public void transferMoney(Long ownerId, Long fromId, Long toId , double money, String description);
    
    public boolean enoughMoney(Long userId , double money);
    
    public void sendRequestToFillUp(Long userId, double money, String description);
    
    public void sendRequestToFillOut(Long userId, double money);
    
    public void fillUp(Long transactionId);
    public void fillOut(Long transactionId);
    
    public void replenishMoneyToAdmin(double money);
    
    public void ExternalIncoming(Long transactionId);
    public void ExternalOutcoming(Long transactionId);

    public String getLinkForYandexPayment(Long userId,double money);
    
    public String getMobileLinkForYandexPayment(Long userId, double money);
    
    void updateMoney();
    
    public void testWM();
    
}
