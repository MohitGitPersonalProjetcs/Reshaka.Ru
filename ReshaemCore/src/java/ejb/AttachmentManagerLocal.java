package ejb;

import ejb.util.ReshakaUploadedFile;
import entity.Attachment;
import entity.User;
import java.util.List;
import javax.ejb.Local;

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
    Attachment uploadFiles(User user, List<ReshakaUploadedFile> files, String tags);
    
    /**
     * Returns information about uploaded file with specified id
     * @param user Who tries to get information? (for security check)
     * @param id Upload id
     * @return Attachment entity if succeeded or null if operation failed.
     */
    Attachment getUploadedFile(Long userId, long id);
    
    /**
     * Checks if a user with specified userId can download the file with attachmentId.
     * If userId is null, method returns true only if attachment is avatar of file of condition.
     * @param userId user id
     * @param attachmentId attachment id
     * @return true if user can download file, false if not or file does not exist
     */
    boolean checkDownloadRights(Long userId, Long attachmentId);
    
    /**
     * Returns all attachments of the user with specified userId
     * @param userId user id
     * @return List of files or null in case of error
     */
    List<Attachment> getAttachmentsByUserId(Long userId);

    /**
     * Shares the file with specified ID with another user
     * @param attachmentId the ID of the file to share
     * @param who the ID of the user who tries to share file (must be admin, or file owner)
     * @param with the ID of user to share the file with
     * @return Attachment entity if succeeded or null if operation failed
     */
    Attachment shareFile(Long attachmentId, long who, Long with);

    /**
     * TODO: (danon) implement removeInvalidEntries in attachment manager
     */
    void removeInvalidEntries();
    
    /**
     * Substitutes attachment, that already exists, with new files
     * Files are archived on disk within one file and information is modified in DB.
     * Original files in existing attachment are removed
     * @param user Owner of upload
     * @param files List of uploaded files
     * @param tags Tags (not implemented yet) // TODO
     * @return Attachment entity if succeeded or null if operation failed.
     */
    Attachment reuploadFiles(User user, Long attachmentId, List<ReshakaUploadedFile> files, String tags);
    
    /**
     * Renames the file.
     * @param userId who tries to rename
     * @param attachmentId ID of attachment
     * @param name new name
     * @return attachment or null in case of error.
     */
    Attachment renameAttachment(Long userId, long attachmentId, String name);
}
