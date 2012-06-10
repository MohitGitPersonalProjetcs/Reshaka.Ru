package web;

import ejb.AttachmentManager;
import ejb.UserManagerLocal;
import entity.Attachment;
import entity.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import web.utils.SessionListener;

/**
 *
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
@ManagedBean
@RequestScoped
public class ProfileViewBean {
    
    @EJB
    UserManagerLocal um;
    
    private User user = null;
    
    /**
     * Creates a new instance of ProfileViewBean
     */
    public ProfileViewBean() {
        user = (User) SessionListener.getSessionAttribute("user",true);
        if(user==null)
            user = new User();
    }
    
    public StreamedContent getCurrentAvatar() {
        if (user == null || user.getAvatarId() == null) {
            return null;
        }
        Attachment avatar = um.getUserAvatar(user.getAvatarId());
        if (avatar == null) {
            return null;
        }   
        try {
            FileInputStream img = new FileInputStream(new File(AttachmentManager.DEFAULT_UPLOAD_DIRECTORY, avatar.getFileName()));
            return new DefaultStreamedContent(img, avatar.getMimeType(), avatar.getName());
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
    
    
}
