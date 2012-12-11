package ru.reshaka.web.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import web.utils.Tools;

/**
 *
 * @author rogvold
 */
public class LogOutServlet extends HttpServlet {

    private static final String REDIRECT_URI_PARAM_NAME = "redirect";

    private static final String DEFAULT_REDIRECT_URI = "/index.xhtml";

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String redirect = (request.getParameter(REDIRECT_URI_PARAM_NAME) == null) ? DEFAULT_REDIRECT_URI : request.getParameter(REDIRECT_URI_PARAM_NAME);

            //flushing cookies
            
            Cookie loginCookie = new Cookie("l", null);
            loginCookie.setMaxAge(3600 * 24 * 366);
            Cookie passwordCookie = new Cookie("p", null);
            passwordCookie.setMaxAge(3600 * 24 * 366);
            Cookie emailCookie = new Cookie("e", null);
            emailCookie.setMaxAge(3600 * 24 * 366);
            response.addCookie(loginCookie);
            response.addCookie(passwordCookie);
            response.addCookie(emailCookie);

            HttpSession session = request.getSession(false);
            session.invalidate();
            request.getSession(true);
            
            response.sendRedirect(redirect);
        } finally {
            out.close();
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
        processRequest(request, response);
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
        processRequest(request, response);
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
