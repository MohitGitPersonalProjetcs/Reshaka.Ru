package web;

import ejb.AttachmentManager;
import ejb.AttachmentManagerLocal;
import entity.Attachment;
import entity.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import web.utils.SessionListener;

/**
 *
 * @author Danon
 */
@ManagedBean
@ViewScoped
public class FileUploadController implements Serializable {

    @EJB
    AttachmentManagerLocal um;

    private static Logger log = Logger.getLogger(FileUploadController.class);

    private List<UploadedFile> files;

    private Attachment attachment;
    
    private boolean displayUploadControl = false;

    public FileUploadController() {
        files = new LinkedList<>();
    }

    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("File is uploaded");
        UploadedFile uf = event.getFile();
        files.add(uf);
        if (log.isTraceEnabled()) {
            log.trace("FileUploadEvent(): uf = " + uf);
        }
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "File uploaded",
                "File " + event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void setAttachmentManager(AttachmentManagerLocal am) {
        um = am;
    }

    public boolean isDisplayUploadControl() {
        return displayUploadControl;
    }

    public Attachment getFileInfo() {
        FacesContext fc = FacesContext.getCurrentInstance();
        User user = (User) SessionListener.getSessionAttribute("user", true);
        if (attachment == null) {
            return new Attachment();
        }
        return attachment;
    }

    public void uploadFiles(ActionEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();
        User user = (User) SessionListener.getSessionAttribute("user", true);
        try {
            attachment = um.uploadFiles(user, files, "");
            files.clear();
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Success", "File(s) successfully uploaded. " + attachment));
            RequestContext.getCurrentInstance().addCallbackParam("attachmentId", attachment.getId());
        } catch (Exception ex) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "Upload failed. <br/>Exception: " + ex.getMessage()));
        }
    }

    public boolean isUploaded() {
        return attachment != null;
    }

    public StreamedContent getFile() {
        if (!isUploaded()) {
            return null;
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        User user = (User) SessionListener.getSessionAttribute("user", true);
        try {
            File f = new File(AttachmentManager.DEFAULT_UPLOAD_DIRECTORY, attachment.getFileName());
            return new DefaultStreamedContent(new FileInputStream(f), attachment.getMimeType(), attachment.getName());
        } catch (Exception ex) {
            System.out.println("Download operation failed. " + ex.getMessage());
            return null;
        }
    }

    public List<UploadedFile> getFiles() {
        return files;
    }

    public void showUploadControl(ActionEvent evt) {
        displayUploadControl = true;
    }

    public void hideUploadControl(ActionEvent evt) {
        displayUploadControl = false;
    }
    
    public void clearFiles(ActionEvent evt) {
        files.clear();
    }
}