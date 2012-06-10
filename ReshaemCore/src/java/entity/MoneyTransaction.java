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
@Table(name = "Money_Transactions")
public class MoneyTransaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
   
    /**
     * status
     * 0 - this is internal transaction (between users inside our system)
     * 
     * The other statuses are for filling up user's balance
     * 1 - user sent a request to fill up his balance (money from administrator to user)
     * 2 - money have come from payment system to administrator external bill
     * 3 - money have filled up to user (transaction is closed)
     * 
     * The other statuses are for filling out user's balance
     * 4 - user sent a request to fill out his money
     * 5 - administrator has payed from his external wallet to external user's wallet
     * 6 - transaction is closed
     */
    
    
    @Column(name="status")
    private int status;
    @Column(name = "from_id")
    private Long fromId;
    @Column(name = "to_id")
    private Long toId;
    @Column(name="money")
    private double money;
    @Column(name = "description")
    private String description;
    @ManyToOne(optional = true)
    @JoinTable(name = "User_transactions", joinColumns = {
        @JoinColumn(name = "transaction_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "user_id")
    })
    private User owner;
    
    @Column(name="creation_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    
    @Column(name="changes_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date changesDate;

    public Date getChangesDate() {
        return changesDate;
    }

    public void setChangesDate(Date changesDate) {
        this.changesDate = changesDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }


    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
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
        if (!(object instanceof MoneyTransaction)) {
            return false;
        }
        MoneyTransaction other = (MoneyTransaction) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.MoneyTransaction[ id=" + id + " ]";
    }
}
