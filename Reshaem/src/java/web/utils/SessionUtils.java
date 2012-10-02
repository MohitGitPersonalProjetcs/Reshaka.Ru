package web.utils;

import ejb.util.EJBUtils;
import ejb.UserManagerLocal;
import entity.User;
import javax.servlet.http.Cookie;

/**
 *
 * @author danon
 */
public class SessionUtils {
    
    private static UserManagerLocal um = 
            EJBUtils.resolve("java:global/ReshaemEE/ReshaemCore/UserManager!ejb.UserManagerLocal",
                              UserManagerLocal.class);

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
//        System.out.println("Cookies: l=" + l + "; p=" + p + "; e=" + e);
        if (e == null || "true".equalsIgnoreCase(e)) {
            return um.logInByEmailMD5(l, p);
        } else {
             return um.logInMD5(l, p);
        }
    }
}
