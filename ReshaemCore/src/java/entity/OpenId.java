/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Map;
import javax.persistence.*;

/**
 *
 * @author rogvold
 */
@Entity
@Table(name = "open_id")
public class OpenId implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "vkontakte")
    private String vkontakte;
    @Column(name = "gmail")
    private String gmail;
    @Column(name = "yandex")
    private String yandex;
    @Column(name = "facebook")
    private String facebook;
    @Column(name = "mail_ru")
    private String mailru;

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getMailru() {
        return mailru;
    }

    public void setMailru(String mailRu) {
        this.mailru = mailRu;
    }

    public String getVkontakte() {
        return vkontakte;
    }

    public void setVkontakte(String vkUid) {
        this.vkontakte = vkUid;
    }

    public String getYandex() {
        return yandex;
    }

    public void setYandex(String yandex) {
        this.yandex = yandex;
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
        if (!(object instanceof OpenId)) {
            return false;
        }
        OpenId other = (OpenId) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OpenId[ id=" + id + " ]";
    }

    public void setParameter(Map<String, String> map) {
        String key = "";
        for (String s : map.keySet()) {
            key = s;
        }
        String value = map.get(key);

        if ("vkontakte".equals(key)) {
            setVkontakte(value);
        }
        if ("facebook".equals(key)) {
            setFacebook(value);
        }
        if ("mailru".equals(key)) {
            setMailru(value);
        }
        if ("gmail".equals(key)) {
            setGmail(value);
        }
        if ("yandex".equals(key)) {
            setYandex(value);
        }
    }
}
