/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.OnlineHelp;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface OnlineHelpManagerLocal {

    public void addNewOnlineHelp(String type, String srcAddr, String dstAddr, Date startDate, int duration, Long orderId);

    public void addNewOnlineHelp(String type, String srcAddr, String dstAddr, Date startDate, Date endDate);
    
    public void deleteOnlineHelp(Long onlineHelpId);
    
    public void addNewBasicOnlineHelp(Long orderId);
    
    public List<OnlineHelp> getOnlineHelpItems(String type, int status);
    
    public OnlineHelp updateOnlineHelp(OnlineHelp online);
    
    public OnlineHelp getOnlineHelpById(Long oId);
}
