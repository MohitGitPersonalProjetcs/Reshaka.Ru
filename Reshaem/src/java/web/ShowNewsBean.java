/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.AnnouncementManagerLocal;
import entity.Announcement;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ShowNewsBean implements Serializable {

    @EJB
    AnnouncementManagerLocal annMan;

    private List<Announcement> announcements;

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    @PostConstruct
    private void init() {
        System.out.println("showNewsBean postconstruct...");
        this.announcements = annMan.getAllAnnouncements();
    }
}
