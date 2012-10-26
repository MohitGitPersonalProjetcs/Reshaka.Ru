/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.reshaka.web.servlets;

import ejb.AttachmentManagerLocal;
import ejb.ReplenishmentRequestManagerLocal;
import ejb.UserManagerLocal;
import ejb.util.FileUtils;
import ejb.util.ReshakaUploadedFile;
import entity.Attachment;
import entity.Order;
import entity.ReplenishmentRequest;
import entity.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
@WebServlet(name = "ReplenismentRequestServlet", urlPatterns = {"/replservl"})
public class ReplenismentRequestServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ReplenismentRequestServlet.class);

    public static final int POST_TYPE = 0;
    public static final int GET_TYPE = 1;

    public static final String DEFAULT_REDIR_URL = "Vkiframe/index.xhtml";
    public static final String ERROR_REDIR_URL = "Vkiframe/index.xhtml";

    private static final String REQUEST_TYPE_PARAM = "type";
    private static final String REQUEST_MONEY_PARAM = "money";
    private static final String REQUEST_TEXT_PARAM = "text";
    private static final String REQUEST_FILE_PARAM = "attachment";
    private static final String REDIR_URL_PARAM = "redir";
    private static final String REQUEST_DEFAULT_TYPE = "other";

    private String redir = DEFAULT_REDIR_URL;

    @EJB
    ReplenishmentRequestManagerLocal replMan;

    @EJB
    UserManagerLocal um;

    @EJB
    AttachmentManagerLocal attMan;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     * <p/>
     * @param request  servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, int type, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            redir = getValidParameter(request, REDIR_URL_PARAM, DEFAULT_REDIR_URL);
            HttpSession session = request.getSession(false);
            User user = (User) SessionListener.getSessionAttribute(session, "user");

            if (user == null) {
                redirect(response, redir, user + " Cannot submit replenishment request. Operation is not permitted.");
            }

            user = um.getUserById(user.getId());

            if (user == null || user.getUserGroup() != User.ADMIN && user.getUserGroup() != User.USER) {
                redirect(response, redir, user + " Cannot submit order. Operation is not permitted.");
            }

//            Order o = createOrder(request, type, user, response);
            ReplenishmentRequest replReq = createReplenishmentRequest(request);

            boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
            if (!isMultipartContent) {
                redirect(response, ERROR_REDIR_URL, "Multipart content is required");
            }

            List<ReshakaUploadedFile> files = new LinkedList<>();
            FileItem f = getFileItem(request, REQUEST_FILE_PARAM);
            if (f != null && !f.isFormField()) {
                if (log.isDebugEnabled()) {
                    log.debug("File: " + f.getName() + " uploaded");
                }
                files.add(convertUploadedFile(f));
            }
            if (log.isTraceEnabled()) {
                log.trace("Submitting replenishmentRequest: " + replReq);
            }
            Attachment att = attMan.uploadFiles(user, files, "replenishmentRequest file");
            Long attId = att.getId();
            replMan.createReplenishmentRequest(user.getId(), replReq.getMoney(), replReq.getType(), replReq.getText(), attId);
        } catch (ServletException ex) {
            System.out.println("exc = " + ex);
            redirect(response, ERROR_REDIR_URL, "Unexpected server error.");
        }
        redirect(response, redir, "Redirecting to " + redir);
    }

    private String getValidParameter(HttpServletRequest request, String name) {
        final String param = request.getParameter(name);
        return param == null ? "" : param;
    }

    private String getValidParameter(HttpServletRequest request, String name, String defaultValue) {
        final String param = getValidParameter(request, name);
        return param.isEmpty() ? defaultValue : param;
    }

    private FileItem getFileItem(HttpServletRequest request, String name) {
        // Validate file.
        Object fileObject = request.getAttribute(name);
        if (fileObject == null) {
            // No file uploaded.
            return null;
        } else if (fileObject instanceof FileUploadException) {
            // File upload is failed.
            FileUploadException fileUploadException = (FileUploadException) fileObject;
            log.debug("Failed to upload file. ", fileUploadException);
            return null;
        } else if (fileObject instanceof FileItem) {
            return (FileItem) fileObject;
        }
        return null;
    }

    private ReplenishmentRequest createReplenishmentRequest(HttpServletRequest request) throws ServletException {
        ReplenishmentRequest repl = new ReplenishmentRequest();

        String param = getValidParameter(request, REQUEST_TEXT_PARAM, "");
        repl.setText(param);
        repl.setCreationDate(new Date());

        param = getValidParameter(request, REQUEST_MONEY_PARAM, "0");
        repl.setMoney(Double.parseDouble(param));

        param = getValidParameter(request, REQUEST_TYPE_PARAM, REQUEST_DEFAULT_TYPE);
        repl.setType(param);

        return repl;
    }

    public static ReshakaUploadedFile convertUploadedFile(FileItem fileItem) {
        if (fileItem == null) {
            return null;
        }
        try {
            File f = File.createTempFile("more_longer_uc", ".tmp");
            try (FileOutputStream os = new FileOutputStream(f)) {
                FileUtils.copyStream(fileItem.getInputStream(), os);
            }
            return new ReshakaUploadedFile(FilenameUtils.getName(fileItem.getName()), fileItem.getContentType(), f);
        } catch (IOException ex) {
            log.error("convertUploadedFile() ", ex);
        }
        return null;
    }

    private void redirect(HttpServletResponse response, String redir, String message) throws ServletException {
        if (log.isDebugEnabled()) {
            log.debug(">> redirect(): url=" + redir + " - " + message);
        }
        try {
            if (!response.isCommitted()) {
                response.sendRedirect(redir);
            }
        } catch (IOException ex) {
            log.error("redirect(): IOException", ex);
            throw new ServletException("Unexpected server error.", ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     * <p/>
     * @param request  servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, GET_TYPE, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     * <p/>
     * @param request  servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request,POST_TYPE, response);
    }

    /**
     * Returns a short description of the servlet.
     * <p/>
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
