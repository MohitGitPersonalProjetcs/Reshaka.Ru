package ejb.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Danon
 */
public class URLUtils {

    public static String getOrderURL(Long orderID) {
        String url = getReshakaURL() + "/index.xhtml"; // my orders page
        if (orderID != null) {
            url += "?order=" + orderID;
        }
        return url;
    }

    public static String getReshakaURL() {
        return "http://reshaka.ru";
    }

    public static String createLink(String url, String target, String value) {
        String link = "<a href=\"" + url + "\"";
        if (!StringUtils.isEmpty(target)) {
            link += " target=\"" + target + "\"";
        }
        link += ">" + value + "</a>";
        return link;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Pattern p = Pattern.compile("[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]");
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
