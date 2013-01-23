package ru.reshaka.web.servlets;

import com.sun.xml.bind.StringInputStream;
import ejb.RpcManagerLocal;
import ejb.util.StringUtils;
import ejb.util.rpc.Context;
import entity.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import web.utils.SessionListener;

/**
 *
 * @author danon
 */
@WebServlet(name = "XmlRpcServlet", urlPatterns = {"/server/xmlrpc"})
public class XmlRpcServlet extends HttpServlet {
    
    private static final String XML_PARAM = "xml";
    private static final String CLASS_NAME_PARAM = "class_name";
    private static final String ASYNC_PARAM = "async";
    
    @EJB
    private RpcManagerLocal xmlRpcManager;
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/xml");
        PrintWriter out = response.getWriter();
        try {
            User currentUser = (User)SessionListener.getSessionAttribute(request.getSession(true), "user");
            Long currentUserId = (currentUser == null) ? null : currentUser.getId();            
            
            Context ctx = new Context();
            String async = getValidParameter(request, ASYNC_PARAM, "no");
            String className = getValidParameter(request, CLASS_NAME_PARAM);
            ctx.setParameter("request", request);
            ctx.setParameter(Context.USER_ID, currentUserId);
            if(!className.isEmpty()) {
                String xml = getValidParameter(request, XML_PARAM);
                if(!xml.isEmpty()) {
                    Element req = parseElement(xml);
                    if(StringUtils.isTrue(async))
                        xmlRpcManager.invokeMethodXmlAsync(ctx, className, req);
                    else {
                        Element result = xmlRpcManager.invokeMethodXml(ctx, className, req);
                        out.print(StringUtils.elementToString(result));
                    }
                }
            }
            
        } catch(ParserConfigurationException ex) {
            throw new ServletException(ex);
        } catch(SAXException ex) {
            throw new ServletException(ex);
        } finally {            
            out.close();
        }
    }
    
    public String getValidParameter(HttpServletRequest request, String name) {
        String param = request.getParameter(name);
        return param == null ? "" : param;
    }

    public String getValidParameter(HttpServletRequest request, String name, String defaultValue) {
        final String param = getValidParameter(request, name);
        return param.isEmpty() ? defaultValue : param;
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
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet for invoking Java code remotely";
    }// </editor-fold>

    private Element parseElement(String xml) 
            throws IOException, ParserConfigurationException, SAXException {
        InputStream is = new StringInputStream(xml);
        Element root = DocumentBuilderFactory.newInstance()
                                     .newDocumentBuilder()
                                     .parse(is)
                                     .getDocumentElement();
        is.close();
        return root;
    }
}