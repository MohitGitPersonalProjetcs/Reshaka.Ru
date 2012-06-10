package entity;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;

/**
 * This entity represents a single attachment (file) from the table
 * "ATTACHMENTS". In the database we save only file name.
 *
 * @author Danon
 */
@Entity @Table(name="Attachments")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "User_Attachments", joinColumns = {
        @JoinColumn(name = "attachment_id")
    },
    inverseJoinColumns = {
        @JoinColumn(name = "user_id")
    })
    private Set<User> user;

    @Column(name = "name")
    private String name;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file", nullable=false) 
    private String fileName;
    
    @Column(name="md5", nullable=false)
    private String md5;
    
    @Column(name="file_size", nullable=true)
    private Long size;
    
    @Column(name="tags")
    private String tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

        public Set<User> getUser() {
        return user;
    }

    public void setUser(Set<User> user) {
        this.user = user;
    }

    public String getMD5() {
        return md5;
    }

    public void setMD5(String md5) {
        this.md5 = md5;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setTags(String tags) {
        this.tags = tags;
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
        if (!(object instanceof Attachment)) {
            return false;
        }
        Attachment other = (Attachment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Attachment.class.getName()).append(" {\n");
        sb.append("\tid=").append(id).append(",\n");
        sb.append("\tuser=").append(user).append(",\n");
        sb.append("\tname=").append(name).append(",\n");
        sb.append("\tmimeType=").append(mimeType).append(",");
        sb.append("\tsize=").append(size).append(",");
        sb.append("\tfileName=").append(fileName).append(",");
        sb.append("\ttags=").append(tags).append(",\n");
        sb.append("\tmd5=").append(md5);
        sb.append("\n}");
        return sb.toString();
    }
}
