/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import entity.Offer;
import entity.Order;
import entity.Subject;
import entity.User;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author rogvold
 */
@Local
public interface OrderManagerLocal {

    public void addOffer(Long orderId, Long employeeId, double price);

    public boolean offerExists(Long orderId, Long employeeId);

    public double getMinPrice(Long orderId);

    public String getLoginById(Long userId);

    public String getSubjectById(Long subjectId);
    
    public Order getOrderByStringId(String stringId);

    public List<Order> getAllOrders();
    
    public List<Order> getOnlineOrders();
    
    public List<Order> getOfflineOrders();

//    public Set<String> getEmployees(Long orderId);
    
    public List<User> getEmployees(Long orderId);
    

    public List<Offer> getOffers(Long orderId);

    public void persistEntity(Object o);

    public Order submitOrder(Order order, List<UploadedFile> files);
    
    public Order updateOrder(Order order, List<UploadedFile> files);

    public Order submitSolution(Long userId, Long orderId,Long solutionId);

    public void mergeEntity(Object o);
    
    public List<Order> getFilteredOrders(Map<String, String> filters, int first, int pageSize , String sortField, SortOrder sortOrder);
    
    public void deleteOrder(Long orderId);
    
    public Order updateOrder(Long orderId, Order order );

    public List<Order> myOrders(Long userId);
    
    public List<Order> myOnlineOrders(Long userId);
    
    public List<Order> myOfflineOrders(Long userId);
    
    public List<Order> getOrdersOfReshaka(Long userId);
    
    public int getSolvedOrdersAmountOfReshaka(Long reshakaId);
    
    public List<Order> getOrders(Map<String, String> filters,Long userId, int orderType, int first, int pageSize, String sortField, SortOrder sortOrder);
    
     public int getOrdersCount(Map<String, String> filters,Long userId, int orderType, int first, int pageSize, String sortField, SortOrder sortOrder);

    public boolean userOwnsThisOrder(Long userId, Long orderId);
    
    public Order getOrderById(Long orderId);
    
    public Subject getSubjectByOrderId(Long orderId);
    
    

}