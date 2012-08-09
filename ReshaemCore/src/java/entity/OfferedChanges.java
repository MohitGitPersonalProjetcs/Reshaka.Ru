/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author rogvold
 */
@Entity
@Table
public class OfferedChanges implements Serializable {

    public static final int TYPE_MONEY = 1;
    public static final int TYPE_DEADLINE = 2;
    public static final int STATUS_NEW = 0;
    public static final int STATUS_APPROVED = 1;
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int status;
    private int type;
    private double price;
    @Temporal(TemporalType.TIMESTAMP)
    private Date offeredDate;
    private Long orderId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmationDate;

    public OfferedChanges() {
    }

    public OfferedChanges(double price, Long orderId) {
        this.price = price;
        this.orderId = orderId;
        this.type = TYPE_MONEY;
        this.status = STATUS_NEW;
    }

    public OfferedChanges(Date offeredDate, Long orderId) {
        this.offeredDate = offeredDate;
        this.orderId = orderId;
        this.type = TYPE_DEADLINE;
        this.status = STATUS_NEW;
    }
    
    
    public Date getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(Date confirmationDate) {
        this.confirmationDate = confirmationDate;
    }


    public Date getOfferedDate() {
        return offeredDate;
    }

    public void setOfferedDate(Date offeredDate) {
        this.offeredDate = offeredDate;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof OfferedChanges)) {
            return false;
        }
        OfferedChanges other = (OfferedChanges) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Offered_Difference[ id=" + id + " ]";
    }
}
