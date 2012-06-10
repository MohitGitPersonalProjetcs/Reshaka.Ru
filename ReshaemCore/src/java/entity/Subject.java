package entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "Subjects")
@NamedQueries({
    @NamedQuery(name = "getAllSubjects", query = "select s from Subject s order by s.subjectName"),
    @NamedQuery(name = "findMySubjects", query = "select s from Subject s"),
    @NamedQuery(name = "findSubjectById", query = "select s from Subject s where s.id=:subjectId"),
    @NamedQuery(name = "getSubjectByName", query = "select s from Subject s where s.subjectName=:subjectName")
})
public class Subject implements Serializable {

    public static final String NOT_SELECTED_SUBJECT_NAME = "Выберите предмет";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @Column(name = "subject")
    private String subjectName;
//    @OneToOne(optional=false,cascade=CascadeType.ALL, 
//       mappedBy="subject",targetEntity=Order.class)
//       private Order order;  
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "orders_subjects", joinColumns = {
        @JoinColumn(name = "subject_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "order_id")
    })
    private Set<Order> orders;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "User_subjects", joinColumns = {
        @JoinColumn(name = "subject_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "user_id")
    })
    private List<User> users;

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public Subject() {
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSubjectName() {
        return subjectName;
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
        if (!(object instanceof Subject)) {
            return false;
        }
        Subject other = (Subject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Subject.class.getName()).append(" {\n");
        sb.append("\tid=").append(id).append(",\n");
        sb.append("\tsubjectName=").append(subjectName);
        sb.append("\n}");
        return sb.toString();
    }
}
