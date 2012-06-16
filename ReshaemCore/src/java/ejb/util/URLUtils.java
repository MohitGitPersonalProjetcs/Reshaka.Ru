package ejb.util;

/**
 *
 * @author Danon
 */
public class URLUtils {
    public static String getOrderURL(Long orderID) {
        String url = getReshakaURL() + "/faces/index2.xhtml"; // my orders page
        if(orderID!=null)
            url += "?order="+orderID;
        return url;
    }
    
    public static String getReshakaURL() {
        return "http://reshaka.ru/Reshaem";
    }
    
    public static String createLink(String url, String target, String value) {
        String link = "<a href=\""+url+"\"";
        if(target!=null&&!target.isEmpty())
            link += " target=\""+target+"\"";
        link += ">"+value+"</a>";
        return link;
    }
}
