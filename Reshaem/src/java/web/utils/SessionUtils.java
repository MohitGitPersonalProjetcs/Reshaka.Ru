package web.utils;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.ejbLinkType;
import ejb.UserManagerLocal;
import entity.User;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 *
 * @author danon
 */
public class SessionUtils {
    
    private static UserManagerLocal um;
    
    static {
        Context context = null;
        try {
            context = new InitialContext();
            um = (UserManagerLocal)context.lookup("java:global/ReshaemEE/ReshaemCore/UserManager!ejb.UserManagerLocal");
        } catch (Exception ex) {
            
        }
    }

    public static boolean isSignedIn() {
        if (SessionListener.getSessionAttribute("user", false) != null) {
            return true;
        }
        return false;
    }

    public static Long getId() {
        User u = ((User) SessionListener.getSessionAttribute("user", true));
        if (u != null) {
            return u.getId();
        }
        return null;
    }

    public static User loginByCookies(Cookie cookies[]) {
        String l = null, p = null, e = null;
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            switch (cookie.getName()) {
                case "l":
                    l = cookie.getValue();
                    break;
                case "p":
                    p = cookie.getValue();
                    break;
                case "e":
                    e = cookie.getValue();
                    break;
            }
        }
        System.out.println("Cookies: l=" + l + "; p=" + p + "; e=" + e);
        if (e == null || "true".equalsIgnoreCase(e)) {
            return um.logInByEmailMD5(l, p);
        } else {
             return um.logInMD5(l, p);
        }
    }
}
