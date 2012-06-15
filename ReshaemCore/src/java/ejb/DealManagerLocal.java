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
public interface DealManagerLocal {

    public boolean canStartDeal(Long userId, Long offerId);

    public boolean canStartOnlineDeal(Long employerId, Long offerId); //online

    public boolean canFinishDeal(Long orderId, Long userId);

    public void startDeal(Long orderId, Long offerId);

    public void startOnlineDeal(Long orderId, Long offerId); // online

    public void makePrepayment(Long orderId);

    public void makeOnlinePrepayment(Long orderId); // online

    public void postSolution(Long orderId, Long solutionId);

    public void makeFinalPayment(Long orderId);

    public String getCurrentStatus(Long orderId);

    public String getRussianCurrentStatus(Long orderId);

    public void finishOnlineDeal(Long orderId);

//    public void rejectOfflineOffer(Long orderId);
    
//    public boolean canRejectOffer(Long orderId, Long userId);
}
