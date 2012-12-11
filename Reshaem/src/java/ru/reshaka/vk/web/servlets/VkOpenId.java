package ru.reshaka.vk.web.servlets;

import ejb.ConfigurationManagerLocal;
import ejb.SessionManagerLocal;
import ejb.UserManagerLocal;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import web.utils.SessionListener;
import web.utils.Tools;

/**
 *
 * @author rogvold
 */
public class VkOpenId extends HttpServlet {

    private transient HttpSession session = null;

    @EJB
    UserManagerLocal userMan;

    @EJB
    ConfigurationManagerLocal confMan;

    @EJB
    SessionManagerLocal sm;

    //Danon, I'm really sorry for this hardcode...
    private static final String SECRET = "jAfZMTAhY0roz7Yr";

    private static final String DEFAULT_REDIRECT_URL = "/mobile/index.xhtml";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");

            String hash = request.getParameter("hash");
            String id = request.getParameter("id");
            if (!hash.equals(Tools.getMD5(id + SECRET))) {
                return;
            }
            User user = openIdAuthorisation(id);

            Cookie loginCookie = new Cookie("l", user.getLogin());
            loginCookie.setMaxAge(3600 * 24 * 366);
            Cookie passwordCookie = new Cookie("p", Tools.getMD5(user.getPassword()));
            passwordCookie.setMaxAge(3600 * 24 * 366);
            Cookie emailCookie = new Cookie("e", "true");
            emailCookie.setMaxAge(3600 * 24 * 366);
            response.addCookie(loginCookie);
            response.addCookie(passwordCookie);
            response.addCookie(emailCookie);
            response.sendRedirect(DEFAULT_REDIRECT_URL);
    }

    private User openIdAuthorisation(String uid) {
        Map<String, String> map = new HashMap();
        map.put("vkontakte", uid);
        User user = userMan.openIdAuthorisation(map);
        System.out.println("VK Iframe: user = " + user);
        SessionListener.setSessionAttribute(session, "user", user);
        sm.addSession(session.getId(), user.getId());
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
