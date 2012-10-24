package ru.reshaka.web.servlets;
import ejb.OrderManagerLocal;
import ejb.UserManagerLocal;
import ejb.util.FileUtils;
import ejb.util.ReshakaUploadedFile;
import entity.Order;
import entity.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import web.utils.SessionListener;

/**
 * A simple servlet for submitting orders.
 * 
 * @author danon
 */
@MultipartConfig(fileSizeThreshold=40*1024*1024) @WebServlet("/orderentry")
public class OrderEntryServlet extends HttpServlet {
    
    private static final Logger log = Logger.getLogger(OrderEntryServlet.class);

    public static final int POST_TYPE = 0;
    public static final int GET_TYPE = 1;
    
    public static final String DEFAULT_REDIR_URL = "index.xhtml";
    public static final String ERROR_REDIR_URL = "index.xhtml";
    
    private static final String ORDER_TYPE_PARAM = "order_type";
    private static final String ORDER_DEADLINE_PARAM = "order_deadline";
    private static final String ORDER_PRICE_PARAM = "order_price";
    private static final String ORDER_SUBJECT_PARAM = "order_subject";
    private static final String ORDER_DESCRIPTION_PARAM = "order_description";
    private static final String ORDER_FROM_EMAIL_PARAM = "order_from_email";
    private static final String ORDER_TAGS_PARAM = "order_tags";
    private static final String ORDER_DURATION = "order_duration";
    private static final String ORDER_CONDITION_PARAM = "order_condition";
    private static final String DATE_FORMAT_PARAM = "date_format";
    private static final String REDIR_URL_PARAM = "redir";
    
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    
    private String redir = DEFAULT_REDIR_URL;
    
    @EJB
    private UserManagerLocal um;
    
    @EJB
    private OrderManagerLocal om;
    

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     * Submits order and redirects to index.xhtml page 
     * or to URL which is passed in redirect parameter.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, int type, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            redir = getValidParameter(request, REDIR_URL_PARAM, DEFAULT_REDIR_URL);
            HttpSession session = request.getSession(false);
            User user = (User)SessionListener.getSessionAttribute(session, "user");
            
            if(user == null) {
                redirect(response, redir, user+" Cannot submit order. Operation is not permitted.");
            }
            
            user = um.getUserById(user.getId());
            
            if(user == null || user.getUserGroup() != User.ADMIN && user.getUserGroup() != User.USER) {
                redirect(response, redir, user+" Cannot submit order. Operation is not permitted.");
            }
            
            Order o = createOrder(request, type, user, response);
            boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
            if(!isMultipartContent && o.getType() == Order.OFFLINE_TYPE) {
                redirect(response, ERROR_REDIR_URL, "Multipart content is required for offline order type.");
            }
            List<ReshakaUploadedFile> files = new LinkedList<>();
            FileItem f = getFileItem(request, ORDER_CONDITION_PARAM);
            if(f!=null && !f.isFormField()) {
                if(log.isDebugEnabled()) {
                    log.debug("File: "+f.getName()+" uploaded");
                }
                files.add(convertUploadedFile(f));
            }
            if(log.isTraceEnabled()) {
                log.trace("Submitting order: "+o);
            }
            om.submitOrder(o, files);
        } catch (ServletException ex) {
            System.out.println("exc = " + ex);
            redirect(response, ERROR_REDIR_URL, "Unexpected server error.");
        } 
        redirect(response, redir, "Redirecting to "+redir);
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
    
    private String getValidParameter(HttpServletRequest request, String name) {
//        String param = "";
//        if(ServletFileUpload.isMultipartContent(request)) {
//            param = request.getPart(name).
//        }
        
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
        } else if(fileObject instanceof FileItem) {
            return (FileItem)fileObject;
        }
        return null;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, GET_TYPE, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, POST_TYPE, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "A simple servlet for submitting orders.";
    }// </editor-fold>

    private void redirect(HttpServletResponse response, String redir, String message) throws ServletException
    {
        if(log.isDebugEnabled()) {
            log.debug(">> redirect(): url="+redir+" - "+message);
        }
        try {
            if(!response.isCommitted())
                response.sendRedirect(redir);
        } catch (IOException ex) {
            log.error("redirect(): IOException", ex);
            throw new ServletException("Unexpected server error.", ex);
        }
    }

    private Order createOrder(HttpServletRequest request, int type, User u, HttpServletResponse response) throws ServletException
    {
        try {
            final Order o = new Order();
            o.setHireDate(new Date());
            o.setEmployer(u);

            String param = getValidParameter(request, ORDER_TYPE_PARAM);
            if(param.equalsIgnoreCase("online")) {
                o.setType(Order.ONLINE_TYPE);
            } else if(param.equalsIgnoreCase("offline")) {
                o.setType(Order.OFFLINE_TYPE);
            } else {
                redirect(response, ERROR_REDIR_URL, "Incorrect order type specified: "+param);
            }

            param = getValidParameter(request, ORDER_DESCRIPTION_PARAM);
            o.setDescription(param);

            param = getValidParameter(request, ORDER_PRICE_PARAM, "0");
            o.setPrice(Double.parseDouble(param));

            param = getValidParameter(request, ORDER_TAGS_PARAM);
            o.setTags(param);

            param = getValidParameter(request, ORDER_SUBJECT_PARAM);
            if(param.isEmpty()) {
                redirect(response, ERROR_REDIR_URL, "Subject is not specified.");
            }
            o.setSubject(om.getFuckingSubjectById(Long.valueOf(param)));

            param = getValidParameter(request, DATE_FORMAT_PARAM, DEFAULT_DATE_FORMAT);
            DateFormat df = new SimpleDateFormat(param);
            param = getValidParameter(request, ORDER_DEADLINE_PARAM);
            if(param.isEmpty()) {
                redirect(response, ERROR_REDIR_URL, "Deadline is not specified.");
            }
            o.setDeadline(df.parse(param));

            if(o.getType() == Order.ONLINE_TYPE) {
                param = getValidParameter(request, ORDER_FROM_EMAIL_PARAM);
                o.setFromEmail(param);
                
                param = getValidParameter(request, ORDER_DURATION, "60");
                o.setDuration(Integer.parseInt(param));
            } else { // offline order
                
            }
            
            return o;
        } catch (Exception ex) {
            System.out.println("excetion occured");
            throw new ServletException(ex);
        }
    }
}
