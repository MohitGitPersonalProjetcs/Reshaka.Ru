package web.utils;

import ejb.OrderManagerLocal;
import entity.Order;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author rogvold
 */
public class LazyOrderDataModel extends LazyDataModel<Order> {

    
    OrderManagerLocal orderMan;

    public LazyOrderDataModel(OrderManagerLocal orderManager) {
        this.orderMan = orderManager;
    }

    @Override
    public int getRowCount() {
        return 100; // hardcode 
    }

    @Override
    public List<Order> load(int i, int i1, String string, SortOrder so, Map<String, String> map) {
        System.out.println("lazy Loading! ");
        System.out.println("first/psize = " + i + "/" + i1);
        if (orderMan == null) {
            System.out.println("we have got problems with EJB");
        }
        List<Order> orders = orderMan.getFilteredOrders(null, i, i1, null, null);
        setPageSize(i1);
        //   setRowCount(orders.size());

        return orders;
    }

    @Override
    public Object getRowKey(Order order) {
        return order.getId();
    }

    @Override
    public Order getRowData(String rowKey) {
        return orderMan.getOrderByStringId(rowKey);
    }
    
     @Override
    public void setRowIndex(int rowIndex) {
        /*
         * The following is in ancestor (LazyDataModel):
         * this.rowIndex = rowIndex == -1 ? rowIndex : (rowIndex % pageSize);
         */
        if (rowIndex == -1 || getPageSize() == 0) {
            super.setRowIndex(-1);
        }
        else
            super.setRowIndex(rowIndex % getPageSize());
    }
}
