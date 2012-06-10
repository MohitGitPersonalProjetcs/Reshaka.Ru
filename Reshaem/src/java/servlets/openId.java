/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import ejb.ConfigurationManagerLocal;
import ejb.UserManagerLocal;
import entity.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import web.utils.SessionListener;
import web.utils.SessionUtils;
import web.utils.openIdUtils;

/**
 *
 * @author rogvold
 */
@WebServlet(name = "openId", urlPatterns = {"/openId"})
public class openId extends HttpServlet {

    @EJB
    ConfigurationManagerLocal confMan;

    @EJB
    UserManagerLocal userMan;

    private transient HttpSession session = null;

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
        // PrintWriter out = response.getWriter();
        try (PrintWriter out = response.getWriter()) {
            /*
             * TODO output your page here. You may use following sample code.
             */
//            System.out.println("-----------------------------------------------------------");
//            System.out.println("openId -> processRequest");
//            System.out.println("--------------------");
//
//
//            Map params = request.getParameterMap();
//
//            System.out.println("greetings from servlet! params = " + params);
//
//            Iterator i = params.keySet().iterator();
//            String s = "?";
//            System.out.println("wtf s = " + s);
//            int k = 0;
//            while (i.hasNext()) {
//
//                String key = (String) i.next();
//                if (("code".equals(key) || ("token".equals(key)))) {
//                    continue;
//                }
//                String value = ((String[]) params.get(key))[0];
//                if (k == 0) {
//                    s = s + "" + key + "=" + value;
//                } else {
//                    s = s + "&" + key + "=" + value;
//                }
//                k++;
//            }
//
//            s = "index2.xhtml" + s;
//
//            System.out.println("s = " + s);
//            out.println("<html>" + "<head><title> Receipt </title>");
//
//            System.out.println("<h3>POST PARAMETERS: "
//                    + "token : " + request.getParameter("token") + ""
//                    + "");
//
//            String json = getJson(request.getParameter("token"));
//            out.println("<br/> json is " + json);
//            openIdUtils utils = new openIdUtils();
//            out.println("<br/> extracted id is " + utils.extractIdFromJson(json));
//
//            System.out.println("JSON = " + json);
//
//            if (!isSignedIn()) {
//                openIdAuthorisation(json);
//                System.out.println("openIdAuthorisation success... ");
//                response.sendRedirect("index2.xhtml");
//            } else {
//                makeBundle(utils.extractIdFromJson(json));
//                response.sendRedirect("index2.xhtml");
//            }
//
//
//
//            response.sendRedirect("index2.xhtml");
        }
    }
    
    


    private void openIdAuthorisation(String json) {
        openIdUtils utils = new openIdUtils();
        Map<String, String> map = utils.extractIdFromJson(json);
        User user = userMan.openIdAuthorisation(map);
        System.out.println("openId auth: user = " + user);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        session = (HttpSession) facesContext.getExternalContext().getSession(false);
        SessionListener.setSessionAttribute(session, "user", user); // working with session in servlet...
    }

    private void makeBundle(Map<String, String> map) {
        User user = userMan.makeOpenIdBundle(SessionUtils.getId(), map);
        if (user == null) {
            System.out.println("Trying to fuck the system!!!");
            return;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        session = (HttpSession) context.getExternalContext().getSession(true);
        SessionListener.setSessionAttribute(session, "user", user);
        System.out.println("makeBundle(): success");
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
         response.setContentType("text/html");
      //  processRequest(request, response);
        try (PrintWriter out = response.getWriter()) {
            
            // working with get params
            
            System.out.println("-----------------------------------------------------------");
            System.out.println("openId -> processRequest");
            System.out.println("--------------------");


            Map params = request.getParameterMap();

            System.out.println("greetings from servlet! params = " + params);

            Iterator i = params.keySet().iterator();
            String s = "#";
            System.out.println("wtf s = " + s);
            int k = 0;
            while (i.hasNext()) {

                String key = (String) i.next();
                if (("code".equals(key) || ("token".equals(key)))) {
                    continue;
                }
                String value = ((String[]) params.get(key))[0];
                if (k == 0) {
                    s = s + "" + key + "=" + value;
                } else {
                    s = s + "&" + key + "=" + value;
                }
                k++;
            }

            s = "index2.xhtml" + s;

            System.out.println("s = " + s);
            
            out.println("s = " + s );
            // !! end;
            
            out.println("<html>" + "<head><title> Receipt </title>");
            System.out.println("<h3>POST PARAMETERS: "
                    + "token : " + request.getParameter("token") + ""
                    + "");

            String json = getJson(request.getParameter("token"));
            out.println("<br/> json is " + json);
            openIdUtils utils = new openIdUtils();
            out.println("<br/> extracted id is " + utils.extractIdFromJson(json));

            System.out.println("JSON = " + json);

            if (!SessionUtils.isSignedIn()) {
                out.println("is not signed in. trying to make authorisation");
                openIdAuthorisation(json);
                if (SessionUtils.isSignedIn())
                out.println("openIdAuthorisation success... ");
                
                response.sendRedirect(s);
            } else {
                makeBundle(utils.extractIdFromJson(json));
                response.sendRedirect(s);
            }



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

    public String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte byteData[] = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception exc) {
        }

        return "";
    }

    public String getJson(String token) {
        System.out.println("getJSON ocuured");
        if (confMan == null) {
            System.out.println("confMan is null!!!!");
        }
        //String ID = confMan.getOpenIdWidgetId();
        String ID = confMan.getString("openIdWidgetId");

//        String secret = confMan.getOpenIdSecretKey();
        String secret = confMan.getString("openIdSecretKey");
        String sig = getMD5(token + secret);
        String link = "http://loginza.ru/api/authinfo?token=" + token + "&id=" + ID + "&sig=" + sig;

        System.out.println("getJSON - MD5 succesfull");
        System.out.println("link = " + link);

        try {
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            String res = "";
            String inputLine;
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                res += inputLine;
            }

            System.out.println("res - " + res);
            return res;
        } catch (Exception e) {
        }

        return "";
    }
}
