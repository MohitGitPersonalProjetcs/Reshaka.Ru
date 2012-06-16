package web.view.subjects;

import ejb.OrderManagerLocal;
import ejb.SubjectManagerLocal;
import ejb.UserManagerLocal;
import entity.Order;
import entity.Subject;
import entity.User;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import web.utils.HttpUtils;

/**
 *
 * @author rogvold
 */
public class LazyOrderDataModel<T extends Object> extends LazyDataModel<Order> {

    private List<Subject> datasource;

    private SubjectManagerLocal subjMan;

    private int orderType = -1;

    private Long currentUserId = null;

    private Long myUserId = null;

    private Long resultUserId;

    private int userGroup = -1;

    private OrderManagerLocal orderMan;

    private UserManagerLocal userMan;

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public LazyOrderDataModel(OrderManagerLocal orderManager, UserManagerLocal userManager) {
        this.userMan = userManager;
        this.orderMan = orderManager;
        this.setOrderType(-1);
    }

    private void initUserIds() {
        // defining current user to show his orders
        if (HttpUtils.getRequestUrl().indexOf("profile") > 0) {
            String sid = HttpUtils.getRequestParam("id");
            Long mid = null;
            try {
                currentUserId = null;
                mid = Long.parseLong(sid);
                if (userMan.userExistsById(mid)) {
                    currentUserId = mid;

                    userGroup = userMan.getGroupById(currentUserId);
                }

                return;
            } catch (Exception e) {
            }
        }

        if (HttpUtils.getRequestUrl().indexOf("allorders") > 0) {
            currentUserId = null;
            return;
        }

        //defining user id in session
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            myUserId = user.getId();
            currentUserId = myUserId;
        }
    }

    private void initResultUserId() {
        if (currentUserId == null) {
            resultUserId = null;
            return;
        }
        resultUserId = currentUserId;
        userGroup = userMan.getGroupById(resultUserId);
    }

    @Override
    public List<Order> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        initUserIds();
        initResultUserId();
        List<Order> data = orderMan.getOrders(filters, resultUserId, orderType, first, pageSize, "id", SortOrder.DESCENDING);
        int dataSize = orderMan.getOrdersCount(filters, resultUserId, orderType, first, pageSize, null, null);
        this.setRowCount(dataSize);
        return data;
    }
}
