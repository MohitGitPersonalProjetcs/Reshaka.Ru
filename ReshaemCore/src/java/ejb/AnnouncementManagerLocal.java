/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Announcement;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface AnnouncementManagerLocal {
    
    public void createAnnouncement(String theme, String text);
    
    public void updateAnnouncement(Long annId, String theme, String text);
    
    public void deleteAnnouncement(Long annId);
    
    public List<Announcement> getAllAnnouncements();
    
}
