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
@Table(name = "external_transaction")
@NamedQueries({
    @NamedQuery(name = "getExternalTransactionByPaymentSystemAndOperationId", query = ""
    + "select e from ExternalMoneyTransaction e where e.paymentSystem=:system and e.operationId=:operationId")})
public class ExternalMoneyTransaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String paymentSystem;
    @Column
    private String operationId;
    @Column
    private double money;
    @Column
    private boolean finished;
    @Column
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date operationDate;
    @Column
    private Long userId;

    public ExternalMoneyTransaction() {
    }

    public ExternalMoneyTransaction(String operationId, double money, Date operationDate, Long userId) {
        this.operationId = operationId;
        this.money = money;
        this.operationDate = operationDate;
        this.userId = userId;
    }

    

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(String paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        if (!(object instanceof ExternalMoneyTransaction)) {
            return false;
        }
        ExternalMoneyTransaction other = (ExternalMoneyTransaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ExternalMoneyTransaction[ id=" + id + " ]";
    }
}
