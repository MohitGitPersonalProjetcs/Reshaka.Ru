package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import ejb.util.FileUtils;
import ejb.util.ReshakaUploadedFile;
import entity.Attachment;
import entity.Order;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Deflater;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 *
 * @author Danon
 */
@Stateless
public class AttachmentManager implements AttachmentManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class);
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    public static final long MAX_ZIP_SIZE = 1024 * 1024 * 40; // 40 Mb
    // TODO: default upload directory should be set in config!
    public static final String DEFAULT_UPLOAD_DIRECTORY = "uploads";

    /**
     * Stores uploaded file "as it is" and adds database entry.
     *
     * @return ID of attachment in the database.
     */
    @Override
    public Attachment uploadFile(String fileName, String contentType, User user, byte[] contents, String tags) {
        if (log.isTraceEnabled()) {
            log.trace(">> uploadFile()");
        }
        try {
            if (contents.length > MAX_ZIP_SIZE) {
                log.trace("File too large!");
                throw new IOException("File too large.");
            }
            
            if (!checkUploadRights(user)) {
                return null;
            }

            Attachment a = prepareAttachment(fileName, contentType, user, contents, tags);

            em.persist(a);

            Set<User> uset = new HashSet();
            uset.add(user);
            a.setUser(uset);
            em.merge(a);


            if (log.isTraceEnabled()) {
                log.trace("<< uploadFile(): " + a);
            }
            return a;
        } catch (Exception ex) {
            log.error("uploadFile(): Failed to upload file.", ex);
            return null;
        }
    }

    /**
     * Stores uploaded file in database. If there are several files, they are
     * compressed into zip archive (with filename = user_login.zip).
     *
     * @param user Owner of the attachment
     * @param files List of uploaded files to be saved into database
     * @return ID of attachment in the database or null if operation failed.
     */
    @Override
    public Attachment uploadFiles(User user, List<ReshakaUploadedFile> files, String tags) {

        if (log.isTraceEnabled()) {
            log.trace(">> uploadFiles(): " + files);
        }

        if (!checkUploadRights(user)) {
            return null;
        }

        if (files.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("List of files is empty! Nothing to compress.");
            }
            return null;
        }

        try {
            Attachment att = prepareAttachment(user, files, tags);

            if(att.getSize() > MAX_ZIP_SIZE) {
                if(log.isTraceEnabled()) {
                    log.trace("File too large!");
                }
                throw new IOException("File too large.");
            }
            
            em.persist(att);

            Set<User> uset = new HashSet();
            uset.add(user);
            att.setUser(uset);
            em.merge(att);

            if (log.isTraceEnabled()) {
                log.trace("<< uploadFiles(): " + att);
            }
            return att;
        } catch (Exception ex) {
            log.error("uploadFiles(): Failed to upload files. ", ex);
            return null;
        }
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public Attachment getUploadedFile(Long userId, long id) {
        if (log.isTraceEnabled()) {
            log.trace(">> getUploadedFile(): id=" + id);
        }
        try {
            Attachment att = em.find(Attachment.class, id);
            if (!checkDownloadRights(userId, att.getId())) {
                return null;
            }

            if (log.isTraceEnabled()) {
                log.trace("<< getUploadedFile(): " + att);
            }
            return att;

        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("<< getUploadedFile()");
            }
        }
        return null;
    }

    /**
     * Remove invalid entries from the database and remove redundant files
     */
    @Override
    public void removeInvalidEntries() {
        log.info("Checking validity of Attachments table");
        // TODO: Remove invalid entries from Attachments and remove redundant files
    }

    /**
     * Adds a single file into the ZipOutputStream with specified entry name.
     *
     * @throws IOException
     */
    private void addFileToZip(ZipOutputStream zipOut, ReshakaUploadedFile file, String name) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace(">> addFileToZip(): " + file);
        }

        ZipEntry entry = new ZipEntry(name);
        zipOut.putNextEntry(entry);
        try (InputStream in = file.getInputstream()) {
            FileUtils.copyStream(file.getInputstream(), zipOut);
            zipOut.closeEntry();
        }

        if (log.isTraceEnabled()) {
            log.trace("<< addFileToZip()");
        }
    }

    /**
     * Checks if user can upload files
     */
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    private boolean checkUploadRights(User u) {
        if (log.isTraceEnabled()) {
            log.trace(">> checkDownloadRights(): " + u);
        }

        if (u == null) {
            if (log.isDebugEnabled()) {
                log.debug("<< checkDownloadRights(): false - User is null");
            }
            return false;
        }

        // TODO: Check upload rights

        if (log.isTraceEnabled()) {
            log.trace("<< checkUploadRigths(): true");
        }
        return true;
    }

    /**
     * Checks if user has the right to download specified attachment
     */
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    private boolean checkDownloadRights(User u, Attachment a) {
        if (log.isTraceEnabled()) {
            log.trace(">> checkDownloadRights(): user=" + u);
        }
        //        if(a.isPublic())
        //            return true;

        // do check if it is avatar
        Query q = em.createQuery("SELECT u FROM User u WHERE u.avatarId = :avatarId", User.class);
        q.setParameter("avatarId", a.getId());
        List<User> lst = q.getResultList();
        if (lst != null && !lst.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("<< checkDownloadRights(): true // this is an avatar");
            }
            return true;
        }

        if (a == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< checkDownloadRights(): false // attachment = null");
            }
            return false;
        }

        // do check if it is a problem statement
        q = em.createQuery("SELECT o FROM Order o WHERE o.conditionId = :conditionId", Order.class);
        q.setParameter("conditionId", a.getId());
        lst = q.getResultList();
        if (lst != null && !lst.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("<< checkDownloadRights(): true // this is a problem statement");
            }
            return true;
        }
        
        // do check if it is a solution to closed order
        q = em.createQuery("SELECT o FROM Order o WHERE o.solutionId = :solutionId and o.status in :statuses", Order.class);
        q.setParameter("solutionId", a.getId());
        q.setParameter("statuses", new ArrayList(){{
            add(Order.CLOSED_OFFLINE_ORDER_STATUS);
            add(Order.FULL_PAYED_OFFLINE_ORDER_STATUS);
            add(Order.EXPIRED_OFFLINE_ORDER_STATUS);
            add(Order.EXPIRED_ONLINE_ORDER_STATUS);
        }});
        lst = q.getResultList();
        if (lst != null && !lst.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("<< checkDownloadRights(): true // this is a problem statement");
            }
            return true;
        }

        if (u == null) {
            // guest tries to download file (not condition or avatar)
            if (log.isTraceEnabled()) {
                log.trace("<< checkDownloadRights(): false // user = null");
            }
            return false;
        }

        // Admin ?
        if (u.getUserGroup() == 1) {
            return true;
        }

        try {
            // check whether the requestor is owner of the file
            if (log.isTraceEnabled()) {
                log.trace("checkDownloadRights(): file owners >> " + a.getUser().size());
            }
            if(isOwner(u, a))
                return true;
        } catch (Exception ex) {
            if (log.isTraceEnabled()) {
                log.trace("checkDownloadRights(): false // exception while processing owners list", ex);
            }
            return false;
        }

        // we might as well return false...
        if (log.isTraceEnabled()) {
            log.trace("<< checkDownloadRigths(): false");
        }
        return false;
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    public boolean checkDownloadRights(Long userId, Long attachmentId) {
        if (log.isTraceEnabled()) {
            log.trace("checkDownloadRights(): userId = " + userId + " / attachmentId = " + attachmentId);
        }
        try {
            User u = null;
            if (userId != null) {
                u = em.getReference(User.class, userId);
            }
            return checkDownloadRights(u,
                    em.getReference(Attachment.class, attachmentId));
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("checkDownloadRights(): false ", ex);
            }
        }
        return false;
    }

    @Override
    public List<Attachment> getAttachmentsByUserId(Long userId) {
        User user = em.find(User.class, userId);
        List<Attachment> alist = new ArrayList(user.getAttachments());
        return alist;
    }

    @Override
    public Attachment shareFile(Long attachmentId, long who, Long with) {
        if (log.isTraceEnabled()) {
            log.trace(">> shareFile(): attachmentId=" + attachmentId + ", who=" + who + ", with=" + with);
        }

        Attachment att = em.find(Attachment.class, attachmentId);
        if (att == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< shareFile(): null - no such attachment");
            }
            return null;
        }
        boolean canShare = false;
        try {
            //canShare |= um.isAdmin(who);
            User actor = em.find(User.class, who);
            if (actor != null && actor.getUserGroup() == 1) {
                canShare = true;
            }
        } catch (Exception ex) {
        }
        if (!canShare) {
            for (User u : att.getUser()) {
                if (u.getId() == who) {
                    canShare = true;
                    break;
                }
            }
        }
        if (!canShare) {
            if (log.isTraceEnabled()) {
                log.trace("<< shareFile(): null - operation is not permitted");
            }
            return null;
        }
        User w = em.find(User.class, with);
        if (w == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< shareFile(): cannot share with nobody, and owners list was not modified");
            }
            return att;
        }
        att.getUser().add(w);
        em.persist(att);

        return att;
    }

    @Override
    public Attachment reuploadFiles(User user, Long attachmentId, List<ReshakaUploadedFile> files, String tags) {
        if (attachmentId == null) {
            return uploadFiles(user, files, tags);
        }
        if (!checkUploadRights(user)) {
            return null;
        }
        Attachment original = em.find(Attachment.class, attachmentId);
        if (original == null) {
            return null;
        }

        Set<User> u = original.getUser();
        removeAttachmentFromDisk(original);
        original = prepareAttachment(user, files, tags);
        original.setId(attachmentId);
        original.setUser(u);
        return em.merge(original);
    }

    private void removeAttachmentFromDisk(Attachment a) {
        // TODO: removeAttachmentFromDisk()
        return;
    }

    private Attachment prepareAttachment(String fileName, String contentType, User user, byte[] contents, String tags) {
        try {
            Attachment a = new Attachment();
            a.setName(fileName);
            a.setMimeType(contentType);
            File root = new File(DEFAULT_UPLOAD_DIRECTORY, user.getLogin());
            root.mkdirs();
            File tmpFile = File.createTempFile("upload_", ".bin", root);
            FileUtils.writeToFile(tmpFile, contents);
            a.setSize((long) contents.length);
            a.setMD5(FileUtils.getMD5(tmpFile));
            a.setFileName(user.getLogin() + "/" + tmpFile.getName());
            return a;
        } catch (Exception ex) {
            return null;
        }

    }

    private Attachment prepareAttachment(User user, List<ReshakaUploadedFile> files, String tags) {
        if (files.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("prepareAttachment() : List of files is empty! Nothing to compress.");
            }
            return null;
        }
        if (files.size() == 1) {
            if (log.isTraceEnabled()) {
                log.trace("prepareAttachment() : Single file is being uploaded. Delegating to uploadFile()");
            }
            try {
                return prepareAttachment(files.get(0).getFileName(), files.get(0).getContentType(), user, files.get(0).getContents(), tags);
            } catch (IOException ex) {
                if (log.isTraceEnabled()) {
                    log.trace("prepareAttachment() : I/O exception" + ex);
                }
                return null;
            }
        }

        try {
            // create zip file
            log.trace("prepareAttachment(): Creating zip-file");

            File root = new File(DEFAULT_UPLOAD_DIRECTORY, user.getLogin());
            root.mkdirs();
            File file = File.createTempFile("upload_", ".zip", root);
            try (ZipOutputStream zos = new ZipOutputStream(file)) {
                zos.setEncoding("utf-8");
                zos.setMethod(ZipOutputStream.DEFLATED);
                zos.setLevel(Deflater.BEST_COMPRESSION);

                for (ReshakaUploadedFile uf : files) {
                    addFileToZip(zos, uf, uf.getFileName());
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("prepareAttachment(): Files are saved at " + file);
            }

            if (file.length() > MAX_ZIP_SIZE) {
                file.delete();
                throw new IOException("File too large.");
            }

            // Create attachment
            Attachment att = new Attachment();
            att.setName(file.getName());
            att.setMimeType("application/zip");
            att.setSize(file.length());
            att.setMD5(FileUtils.getMD5(file));
            att.setFileName(user.getLogin() + "/" + file.getName());

            if (log.isTraceEnabled()) {
                log.trace("<< prepareAttachment()");
            }
            return att;
        } catch (IOException ex) {
            log.error("prepareAttachment(): Failed to upload files. ", ex);
            return null;
        }
    }

    @Override
    public Attachment renameAttachment(Long userId, long attachmentId, String name) {
        if (log.isDebugEnabled()) {
            log.debug(">> renameAttachment() : userId =" + userId + ", attachmentId=" + attachmentId);
        }
        if (userId == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // operation is not permitted");
            }
            return null;
        }

        User u = em.find(User.class, userId);
        if (u == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // no such user! operation is not permitted");
            }
            return null;
        }
        Attachment att = em.find(Attachment.class, attachmentId);
        if (att == null) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // invalid attachmentId=" + attachmentId);
            }
            return null;
        }
        if (!isOwner(u, att) && u.getUserGroup() != User.ADMIN) {
            if (log.isTraceEnabled()) {
                log.trace("<< renameAttachment() : null // operation is not permitted");
            }
            return null;
        }
        att.setName(name);
        if (log.isDebugEnabled()) {
            log.debug(">> renameAttachment() : userId =" + userId + ", attachmentId=" + attachmentId);
        }
        return em.merge(att);
    }

    private boolean isOwner(User u, Attachment a) {
        for (User usr : a.getUser()) {
            if (usr.getId().equals(u.getId())) {
                if (log.isTraceEnabled()) {
                    log.trace("<< isOwner(): true // owner of the file");
                }
                return true;
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("<< isOwner(): false");
        }
        return false;
    }
}
