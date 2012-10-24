package servlets;

import ejb.ConfigurationManagerLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.yandex.money.api.YandexMoney;
import ru.yandex.money.api.YandexMoneyImpl;
import ru.yandex.money.api.enums.Destination;
import ru.yandex.money.api.rights.*;

/**
 *
 * @author rogvold
 */
public class auth extends HttpServlet {

    @EJB
    ConfigurationManagerLocal confMan;
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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /*
             * TODO output your page here. You may use following sample code.
             */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet auth</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet auth at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");

            YandexMoney ym = new YandexMoneyImpl("7E35884E9F3D0DC7ADE5A85F524D52D01B4CEB086650663C843939FE73AE733A");

            Collection<Permission> scope = new LinkedList<>();
            scope.add(new AccountInfo());
            scope.add(new OperationHistory());
            scope.add(new OperationDetails());
            scope.add(new MoneySource(true, true));
            scope.add(new PaymentP2P().limit(30, "1000"));
//    scope.add(new PaymentShop().limit(1, "100"));
            scope.add(new Payment(Destination.toPattern, "337", 1, "100"));
            scope.add(new Payment(Destination.toPattern, "335", 1, "100"));
            scope.add(new Payment(Destination.toPattern, "343", 1, "100"));
            scope.add(new Payment(Destination.toPattern, "928", 1, "100"));

            String codeReqUri = ym.authorizeUri(scope,"http://reshaka.ru/redirected", false);
            response.sendRedirect(codeReqUri);

        } finally {
            out.close();
        }
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
        return "Short description";
    }// </editor-fold>
}
