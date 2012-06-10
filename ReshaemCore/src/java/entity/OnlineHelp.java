/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author rogvold
 */
@Entity
@Table
@NamedQueries({
    @NamedQuery(name = "getNewOnlineHelpItems", query = "select o from OnlineHelp o where o.type=0"),
    @NamedQuery(name = "getInProcessOnlineHelpItems", query = "select o from OnlineHelp o where o.type=1"),
    @NamedQuery(name = "getFinishedOnlineHelpItems", query = "select o from OnlineHelp o where o.type=2")
})
public class OnlineHelp implements Serializable {

    public static String TYPE_EMAIL = "EMAIL";
    public static String TYPE_SKYPE = "SKYPE";
    public static String TYPE_ICQ = "ICQ";
    public static String TYPE_VK = "VK";
    
    public static int STATUS_NEW = 0;
    public static int STATUS_IN_PROCESS = 1;
    public static int STATUS_FINISHED = 2;
    public static int RELAXATION_TIME = 60;
    public static int MAX_DURATION_TIME = 24*60;
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column
    private String type;
    @Column
    private String srcAdderss;
    @Column
    private String dstAddress;
    @Column
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startDate;
    @Column
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date endDate;
    @Column
    private Long orderId;
    @Column
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDstAddress() {
        return dstAddress;
    }

    public void setDstAddress(String dstAddress) {
        this.dstAddress = dstAddress;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getSrcAdderss() {
        return srcAdderss;
    }

    public void setSrcAdderss(String srcAdderss) {
        this.srcAdderss = srcAdderss;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
        if (!(object instanceof Tag)) {
            return false;
        }
        OnlineHelp other = (OnlineHelp) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OnlineHelp[ id=" + id + " ]";
    }
}
