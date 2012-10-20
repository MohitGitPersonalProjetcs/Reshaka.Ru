package web.utils;

import ejb.SessionManagerLocal;
import ejb.util.EJBUtils;
import entity.User;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.log4j.Logger;
//import org.apache.log4j.Logger;

/**
 * A simple session listener implementation.
 * Seems to be thread-safe.
 * <p/>
 * @author Danon
 */
public class SessionListener implements HttpSessionListener {

    private static final Map<String, HttpSession> map = Collections.synchronizedMap(new HashMap<String, HttpSession>(100));

    private static SessionManagerLocal sm = EJBUtils.resolve("java:global/ReshaemEE/ReshaemCore/SessionManager!ejb.SessionManagerLocal", SessionManagerLocal.class);

    private static Logger log = Logger.getLogger(SessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("ID Session Created: " + event.getSession().getId());
        HttpSession session = event.getSession();
        map.put(session.getId(), session);
        sm.addSession(session.getId(), null);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        try {
            sm.removeSession(session.getId());
        } catch (Exception exc) {
            if (log.isTraceEnabled()) {
                log.trace("sm.removeSession(session.getId) Exception !!!");
            }
        }
        map.remove(session.getId());
        System.out.println("ID Session Destroyed: " + event.getSession().getId());
    }

    public static boolean isOnline(Long id) {
        return sm.isOnline(id);
    }

    public static List<HttpSession> getAllUserSessions(Long id) {
        List<HttpSession> lst = new ArrayList<>();
        Set<Map.Entry<String, HttpSession>> entrySet = map.entrySet();
        if (entrySet == null) {
            return lst;
        }
        for (Map.Entry<String, HttpSession> entry : entrySet) {
            HttpSession session = (HttpSession) entry.getValue();
            if(!isSessionValid(session))
                continue;
            User u = (User) session.getAttribute("user");
            if (u == null && id == null) {
                lst.add(session);
                continue;
            }
            if (id == null) {
                continue;
            }
            if (u != null && u.getId().equals(id)) {
                lst.add(session);
            }
        }
        return lst;
    }

    public static int getSessionsCount() {
        return map.size();
    }

    public static Map<String, HttpSession> getAllSessions() {
        return new HashMap<>(map);
    }

    public static boolean isRequestedSessionValid() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return false;
        }
        ExternalContext ext = fc.getExternalContext();
        if (ext == null) {
            return false;
        }
        HttpServletRequest request = (HttpServletRequest) ext.getRequest();
        if (request == null) {
            return false;
        }
        return request.isRequestedSessionIdValid();
    }

    public static boolean isSessionValid(HttpSession session) {
        if (session == null) {
            return false;
        }
        try {
            session.getCreationTime();
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    public static Object getSessionAttribute(String attrName, boolean createSession) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return null;
        }
        ExternalContext ctx = fc.getExternalContext();
        if (ctx == null) {
            return null;
        }
        HttpSession session = (HttpSession) ctx.getSession(createSession);
        if (isRequestedSessionValid()) {
            try {
                return session.getAttribute(attrName);
            } catch (IllegalStateException ex) {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public static Object getSessionAttribute(HttpSession session, String attrName) {
        if(isSessionValid(session)) {
            try {
                return session.getAttribute(attrName);
            } catch (IllegalStateException ex) {
                return null;
            }
        } else return null;
    }
    
    public static HttpSession getCurrentSession(boolean create) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if(fc==null) return null;
        ExternalContext ext = fc.getExternalContext();
        if(ext==null) return null;
        return (HttpSession)ext.getSession(create);
    }
    
    public static void setSessionAttribute(HttpSession session, String attrName, Object value) {
        try {
            if(session==null || !isSessionValid(session))
                return;
            session.setAttribute(attrName, value);
        } catch (Exception ex) {
            System.err.println("WARN: setSessionAttribute() failed!\n"+ex);
        }
    }
    
}