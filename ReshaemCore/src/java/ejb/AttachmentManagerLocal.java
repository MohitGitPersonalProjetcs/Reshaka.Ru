package ejb;

import entity.Attachment;
import entity.User;
import java.util.List;
import javax.ejb.Local;
import org.primefaces.model.UploadedFile;

/**
 * Local interface for AttachmentManager EJB.
 * Provides some functions for managing uploads
 * @author Danon
 */

@Local
public interface AttachmentManagerLocal {

    /**
     * Uploads a single file.
     * Content of the file is stored on disk and informations about it is added into DB.
     * @param fileName Name of file entry in database
     * @param contentType MIME-type of file (ex. application/zip)
     * @param user A user who uploads file (owner)
     * @param contents raw contents of the file
     * @param tags Tags (not implemented yet) // TODO
     * @return Attachment entity if succeeded or null if operation failed.
     */
    Attachment uploadFile(String fileName, String contentType, User user, byte[] contents, String tags);
    
    /**
     * Uploads multiple files
     * Files are archived on disk within one file and information is added to DB 
     * @param user Owner of upload
     * @param files List of uploaded files
     * @param tags Tags (not implemented yet) // TODO
     * @return Attachment entity if succeeded or null if operation failed.
     */
    Attachment uploadFiles(User user, List<UploadedFile> files, String tags);
    
    /**
     * Returns information about uploaded file with specified id
     * @param user Who tries to get information? (for security check)
     * @param id Upload id
     * @return Attachment entity if succeeded or null if operation failed.
     */
    Attachment getUploadedFile(Long userId, long id);
    
    boolean checkDownloadRights(Long userId, Long attachmentId);
    
    List<Attachment> getAttachmentsByUserId(Long userId);

    /**
     * Shares the file with specified ID with another user
     * @param attachmentId the ID of the file to share
     * @param who the ID of the user who tries to share file (must be admin, or file owner)
     * @param with the ID of user to share the file with
     * @return Attachment entity if succeeded or null if operation failed
     */
    Attachment shareFile(Long attachmentId, long who, Long with);
}
