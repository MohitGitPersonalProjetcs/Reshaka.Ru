package web;

import data.SimpleUser;
import entity.User;
import java.util.*;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpSession;
import web.utils.SessionListener;

/**
 *
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
@ManagedBean
@ApplicationScoped
public class StatsBean {
    
    /**
     * Creates a new instance of StatsBean
     */
    public StatsBean() {
    }
    
    public int getOnlineCount() {
        return SessionListener.getSessionsCount();
    }
    
    public List<SimpleUser> getUsers() {
        return getUsers(20);
    }
    
    public List<SimpleUser> getUsers(int count) {
        List<SimpleUser> lst = new ArrayList<>();
        Set<User> set = new HashSet<>();
        Map<String, HttpSession> m = SessionListener.getAllSessions();
        for(HttpSession s : m.values()) {
            User u = (User)SessionListener.getSessionAttribute(s,"user");
            if(u!=null) {
                set.add(u);
                if(set.size() == count)
                    break;
            }
        }
        for(User u : set) {
            SimpleUser su = new SimpleUser(u);
            su.setOnline(true);
            lst.add(su);
        }
        return lst;
    }
    
    public int getUsersCount() {
        Set<User> set = new HashSet<>();
        Map<String, HttpSession> m = SessionListener.getAllSessions();
        for(HttpSession s : m.values()) {
            User u = (User)SessionListener.getSessionAttribute(s,"user");
            if(u!=null)
                set.add(u);
        }
        return set.size();
    }
    
    public int getPartnersCount() {
        int cnt = 0;
        Map<String, HttpSession> m = SessionListener.getAllSessions();
        for(HttpSession s : m.values()) {
            User u = (User)SessionListener.getSessionAttribute(s,"user");
            if(u!=null && u.getUserGroup()==3)
                cnt++;
        }
        return cnt;
    }
    
    public int getAdminsCount() {
        int cnt = 0;
        Map<String, HttpSession> m = SessionListener.getAllSessions();
        for(HttpSession s : m.values()) {
            User u = (User)SessionListener.getSessionAttribute(s,"user");
            if(u!=null && u.getUserGroup()==1)
                cnt++;
        }
        return cnt;
    }
    
    public int getGuestsCount() {
        int cnt = 0;
        Map<String, HttpSession> m = SessionListener.getAllSessions();
        for(HttpSession s : m.values()) {
            if(SessionListener.getSessionAttribute(s,"user")==null)
                cnt++;
        }
        return cnt;
    }

}
