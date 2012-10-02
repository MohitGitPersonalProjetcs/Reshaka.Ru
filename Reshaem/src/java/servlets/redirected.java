/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import ejb.ConfigurationManagerLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ru.yandex.money.api.InsufficientScopeException;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.api.response.ReceiveOAuthTokenResponse;
import web.utils.SessionListener;

/**
 *
 * @author rogvold
 */
public class redirected extends HttpServlet {

    private transient HttpSession session = null;

    @EJB
    ConfigurationManagerLocal confMan;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, InsufficientScopeException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet redirected</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet redirected at " + request.getContextPath() + "</h1>");


            String code = request.getParameter("code");
            out.println("code = " + code);
            out.println("меняем его на token");

            YandexMoney ym = new YandexMoneyImpl("7E35884E9F3D0DC7ADE5A85F524D52D01B4CEB086650663C843939FE73AE733A");
            try {
                System.out.println("ym to String = " + ym.toString());
                if (ym == null) {
                    System.out.println("ym is null!!!");
                }
                ReceiveOAuthTokenResponse resp =
                        ym.receiveOAuthToken(code, "http://reshaka.ru/redirected");
                System.out.println("resp to string = " + resp.toString());
                if (resp == null) {
                    System.out.println("resp is null!!!");
                }
                if (resp.isSuccess() == null) {
                    System.out.println("resp.isSuccess  ==  null");
                }
                if (resp.isSuccess()) {
                    FacesContext context = FacesContext.getCurrentInstance();
                    session = (HttpSession) context.getExternalContext().getSession(true);

                    System.out.println("session = " + session);

                    //SessionListener.setSessionAttribute(session, "token", resp.getAccessToken());
                    out.println("token = " + resp.getAccessToken());


                }
            } catch (Exception exc) {
                System.out.println(exc.toString());
                System.out.println("ERROR");
            }
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

//  StringBuffer requestURI = request.getRequestURL();
            // System.out.println("url = " + requestURI);


           
            

            processRequest(request, response);
        } catch (InsufficientScopeException ex) {
            Logger.getLogger(redirected.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * <p/>
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (InsufficientScopeException ex) {
            Logger.getLogger(redirected.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
