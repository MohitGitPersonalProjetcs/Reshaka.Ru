package entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.*;

/**
 * This entity represents a single order.
 *
 * @author Danon and ASUS
 */
@Entity
@Table(name = "Orders")
@NamedQueries({
    @NamedQuery(name = "getAllOrders", query = "select o from Order o order by o.id desc"),
    @NamedQuery(name = "findOrderById", query = "select o from Order o where o.id=:orderId"),
    @NamedQuery(name = "getFilteredOrdersByEmployerId", query = "select o from Order o where o.employer.id=:employerId"),
    @NamedQuery(name = "getFilteredOrdersByEmployeeId", query = "select o from Order o where o.employee.id=:employeeId"),
    @NamedQuery(name = "getFilteredOrdersBySubjectId", query = "select o from Order o where o.subject.id=:subjectId"),
    @NamedQuery(name = "getOrdersOfReshaka", query = "select off.order from Offer off where off.userId=:userId order by off.order.id"),
    @NamedQuery(name = "getOnlineOrdersOfReshaka", query = "select off.order from Offer off where off.userId=:userId and off.order.type=1 order by off.order.id"),
    @NamedQuery(name = "getOfflineOrdersOfReshaka", query = "select off.order from Offer off where off.userId=:userId and off.order.type=0 order by off.order.id"),
    @NamedQuery(name = "getSolvedOrdersIdOfReshaka", query = "select o.id from Order o where o.employee.id=:reshakaId and o.status=6")
})
public class Order implements Serializable {

    public static final int OFFLINE_TYPE = 0;
    public static final int ONLINE_TYPE = 1;
    
    public static final int NEW_OFFLINE_ORDER_STATUS = 0;
    public static final int RATED_OFFLINE_ORDER_STATUS = 1;
    public static final int AGREED_OFFLINE_ORDER_STATUS = 2;
    public static final int HALF_PAYED_OFFLINE_ORDER_STATUS = 3;
//    public static final int FIRED_OFFLINE_ORDER_STATUS = 4;
    public static final int SOLVED_OFFLINE_ORDER_STATUS = 4;
    public static final int FULL_PAYED_OFFLINE_ORDER_STATUS = 5;
    public static final int CLOSED_OFFLINE_ORDER_STATUS = 7;
    public static final int EXPIRED_OFFLINE_ORDER_STATUS = 8;
    
    public static final int NEW_ONLINE_ORDER_STATUS = 10;
    public static final int RATED_ONLINE_ORDER_STATUS = 11;
    public static final int AGREED_ONLINE_ORDER_STATUS = 12;
    public static final int PAYED_ONLINE_ORDER_STATUS = 13;
    public static final int FINISHED_ONLINE_ORDER_STATUS = 14;
    public static final int EXPIRED_ONLINE_ORDER_STATUS = 15;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(name = "type")
    private int type;
    /**
     * Status of the order:<br/> 0 - new;<br/> 1 - rated;<br/> 2 - agreed<br/> 3
     * - half-paid<br/> 4 - fire (optional) - Employee refused to solve the
     * problem and another employee is needed<br/> 5 - solved<br/> 6 -
     * full-paid<br/> 7 - closed<br/> ... - expired, banned, frozen and so on
     */
    @Column(name = "status")
    private int status;
    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;
    @ManyToOne(optional = true)
    @JoinTable(name = "User_Orders", joinColumns = {
        @JoinColumn(name = "order_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "user_id")
    })
    private User employer;
    @Column(name = "description", nullable = false)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String description;
    @Column(name = "price")
    private double price;
//    @OneToOne(optional = false, fetch = FetchType.EAGER)
//    @JoinColumn(name = "subject_id")
//    private Subject subject;
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinTable(name = "orders_subjects", joinColumns = {
        @JoinColumn(name = "order_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "subject_id")
    })
    private Subject subject;
    @Column(name = "tags")
    private String tags;
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private User employee;
    @Column(name = "condition_id")
    private Long conditionId;
    @Column(name = "solution_id")
    private Long solutionId;
    @Column(name = "hire_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date hireDate;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Offers", joinColumns = {
        @JoinColumn(name = "order_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "offer_id")
    })
    private List<Offer> offers;
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getDeadlineString() {
        SimpleDateFormat f = new SimpleDateFormat("dd.MM HH.mm");
        if (getDeadline() == null) {
            return "";
        }
        return f.format(getDeadline());
    }

    public String getHireDateString() {
        if (getHireDate() == null) {
            return "";
        }
        SimpleDateFormat f = new SimpleDateFormat("dd.MM HH.mm");
        return f.format(getHireDate());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public User getEmployer() {
        return employer;
    }

    public void setEmployer(User employer) {
        this.employer = employer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(Long solutionId) {
        this.solutionId = solutionId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Order)) {
            return false;
        }
        Order other = (Order) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Order.class.getName()).append(" {\n");
        sb.append("\tid=").append(id).append(",\n");
        sb.append("\temployer").append(employer).append(",\n");
        sb.append("\temployee=").append(employee).append(",\n");
        sb.append("\tconditionId=").append(conditionId).append(",");
        sb.append("\tsolutionId=").append(solutionId).append(",");
        sb.append("\tstatus=").append(status).append(",");
        sb.append("\tprice=").append(price).append(",\n");
        sb.append("\tdescription=").append(description).append(",\n");
        sb.append("\tdeadline=").append(deadline).append(",\n");
        sb.append("\tsubject=").append(subject).append(",\n");
        sb.append("\tdescription=").append(description);
        sb.append("\n}");
        return sb.toString();
    }
}
