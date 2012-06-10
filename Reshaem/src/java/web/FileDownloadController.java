package web;

import ejb.AttachmentManager;
import ejb.AttachmentManagerLocal;
import entity.Attachment;
import entity.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import web.utils.SessionListener;

/**
 *
 * @author Danon
 */
@ManagedBean
@RequestScoped
public class FileDownloadController implements Serializable {

    @EJB
    AttachmentManagerLocal um;
    
    private Long id;
    private Attachment attachment;

    public FileDownloadController() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
        try {
            id = Long.parseLong(params.get("id"));
            System.out.println("ID = "+id);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            id = -1L;
        }
    }
    
    /**
     * Returns streamed content of the file with id in request params.
     * @return Streamed content of the file or null in case of an error.
     *         All error details are returned via FacesContext Messages
     */
    public StreamedContent getFile() {
        FacesContext fc = FacesContext.getCurrentInstance();
        User user = (User)SessionListener.getSessionAttribute("user", true);
        // TODO: Check if user has enough privileges for downloading
        try {
            Attachment tmp = um.getUploadedFile((user!=null) ? user.getId() : null, id);
            File f = new File(AttachmentManager.DEFAULT_UPLOAD_DIRECTORY, tmp.getFileName());
            return new DefaultStreamedContent(new FileInputStream(f), tmp.getMimeType(), tmp.getName());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error", "Failed to download file" + ex.getMessage()));
            System.out.println(ex.getMessage());
            return null;
        }
    }
    
    public Attachment getFileInfo() {
        FacesContext fc = FacesContext.getCurrentInstance();
        User user = (User)SessionListener.getSessionAttribute("user", true);
        Attachment fileInfo = um.getUploadedFile((user!=null) ? user.getId() : null, id);
        if(fileInfo==null) return new Attachment();
        return fileInfo;
    }
    
    public boolean fileExists() {
        FacesContext fc = FacesContext.getCurrentInstance();
        User user = (User)SessionListener.getSessionAttribute("user", true);
        Attachment fileInfo = um.getUploadedFile((user!=null) ? user.getId() : null, id);
        if(fileInfo==null) return false;
        return true;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
