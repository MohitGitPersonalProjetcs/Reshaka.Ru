package ru.reshaka.mobile.servlets;

import ejb.SessionManagerLocal;
import ejb.UserManagerLocal;
import ejb.util.URLUtils;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
@WebServlet(name = "authorisation", urlPatterns = {"/authorisation"})
public class SimpleAuthorisationServlet extends HttpServlet {

    @EJB
    UserManagerLocal userMan;

    @EJB
    SessionManagerLocal sm;

    public static final String EMAIL_FIELD = "email";

    public static final String PASSWORD_FIELD = "password";

    public static final String REDIRECT_FIELD = "redirect";

    public static final String LOGGED_IN = "loggedIn";
    public static final String INCORRECT_PASSWORD = "incorrectPassword";
    public static final String INVALID_DATA = "invalidData";
    public static final String USER_DOES_NOT_EXIST = "userDoesNotExist";

    public static final String DEFAULT_REDIRECT_STRING = "/mobile/login.xhtml";

    public static final String ERROR = "error";

    private transient HttpSession session = null;

    private static final Logger log = Logger.getLogger(SimpleAuthorisationServlet.class);

    private String email;

    private String password;

    private String flag;

    private User user;

    private boolean checkData() {
        if ((!URLUtils.isValidEmail(email)) || (password.isEmpty())) {
            flag = INVALID_DATA;
            return false;
        }

        if (!userMan.userExists(email)) {
            flag = USER_DOES_NOT_EXIST;
            return false;
        }

        user = userMan.logInByEmail(email, password);
        if (user == null) {
            flag = INCORRECT_PASSWORD;
            return false;
        }

        flag = LOGGED_IN;
        return true;
    }

    private boolean logIn() {
        boolean b;
        b = checkData();
        if (!b) {
            return b;
        }
        SessionListener.setSessionAttribute(session, "user", user);
        sm.addSession(session.getId(), user.getId());
        return b;
    }

//    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        try {
//            /*
//             * TODO output your page here. You may use following sample code.
//             */
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet SimpleAuthorisation</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet SimpleAuthorisation at " + request.getContextPath() + "</h1>");
//            out.println("</body>");
//            out.println("</html>");
//        } finally {
//            out.close();
//        }
//    }
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
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
////        processRequest(request, response);
//    }
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
        session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            String redirectString = request.getParameter(REDIRECT_FIELD);
            if (redirectString == null) {
                redirectString = DEFAULT_REDIRECT_STRING;
            }

            this.email = request.getParameter(EMAIL_FIELD);
            this.password = request.getParameter(PASSWORD_FIELD);
            if (log.isTraceEnabled()) {
                log.trace("doPost(): try to login: email/password = " + email + "/" + password);
            }


            boolean b = logIn();
            if (b) {
                if (log.isTraceEnabled()) {
                    log.trace("doPost(): logging in successFull: flag = " + flag);
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("doPost(): logging in failed: flag = " + flag);
                }
            }
            out.print(flag);
        } catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.trace("doPost(): exc = " + e.toString());
            }
            this.flag = ERROR;
        }

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
