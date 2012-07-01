package web.orders;

import entity.Order;
import entity.User;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import web.UserBean;

/**
 *
 * @author ASUS
 */
@ManagedBean
public class OrderBean {

    private Order entity;
    
//    public OrderBean(int subjectId, int price, int orderId, int solutionId, int conditionId, String deadline, int status, String comment, int type) {
//        this.subjectId = subjectId;
//        this.price = price;
//        this.orderId = orderId;
//        this.solutionId = solutionId;
//        this.conditionId = conditionId;
//        this.deadline = deadline;
//        this.status = status;
//        this.comment = comment;
//    }

    public OrderBean() {
        entity = new Order();
    }
    
    public OrderBean(Order entity) {
        this.entity = entity;
    }
    
    private List<UserBean> employeesId;

    public void setType(int type) {
        entity.setType(type);
    }

    public int getType() {
        return entity.getType();
    }

//    public void setSubjectId(long subjectId) {
//        // TODO: setSubjectId
//        entity.setSubject(new entity.Subject());
//        entity.getSubject().setId(subjectId);
//    }

    public long getSubjectId() {
        entity.Subject s = entity.getSubject();
        return s == null ? 0 : s.getId();
    }

    public void setPrice(double price) {
        entity.setPrice(price);
    }

    public double getPrice() {
        return entity.getPrice();
    }

    public void setEmployerId(long employerId) {
        // TODO: set EmployerID
    }

    public long getEmployerId() {
        User u = entity.getEmployer();
        return u == null ? 0 : u.getId();
    }

    public void setConditionId(Long conditionId) {
        entity.setConditionId(conditionId);
    }

    public void setDeadline(Date deadline) { 
        entity.setDeadline(deadline);
    }

    public void setEmployeesId(List<UserBean> employeesId) {
        this.employeesId = employeesId;
    }

    public void setOrderId(long orderId) {
        entity.setId(orderId);
    }

    public void setSolutionId(Long solutionId) {
        entity.setSolutionId(solutionId);
    }

    public void setStatus(int status) {
        entity.setStatus(status);
    }

    public long getConditionId() {
        return entity.getConditionId();
    }

    public Date getDeadline() {
        return entity.getDeadline();
    }

    public List<UserBean> getEmployeesId() {
        return employeesId;
    }

    public long getOrderId() {
        return entity.getId();
    }

    public long getSolutionId() {
        return entity.getSolutionId();
    }

    public int getStatus() {
        return entity.getStatus();
    }
    
    
}
//      order_id             INTEGER NOT NULL ,
//	type                 INTEGER NULL ,
//	status               INTEGER NULL ,
//	deadline             DATE NULL ,
//	employer_id          INTEGER NULL ,
//	description          CLOB NULL ,
//	price                INTEGER NULL ,
//	subject_id           INTEGER NULL ,
//	tags                 CLOB NULL ,
//	employee_id          INTEGER NULL ,
//	condition_id         INTEGER NULL ,
//	solution_id          INTEGER NULL 