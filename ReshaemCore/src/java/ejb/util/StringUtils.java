package ejb.util;

/**
 * Contains a few methods for dealing with Strings
 * 
 * @author danon
 */
public class StringUtils {
   
    public static final String EMPTY_STRING = "";
    
    public static String getValidString(String s) {
        return s == null ? EMPTY_STRING : s;
    } 
    
    public static boolean isEmpty(String s) {
        return getValidString(s).isEmpty();
    }
    
}
