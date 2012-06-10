/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rogvold
 */
public class MessageValidationUtils {

    public static final int OK = 0;
    public static final int WARNING = -1;
    public static final int SUSPICIOUS = -2;

    public boolean includesEmail(String text) {
//        Pattern p = Pattern.compile(".*([\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z])");
//        Matcher matcher = p.matcher(text);
//        if (matcher.group(1))
        return false;
    }

    public int checkMessageText(String text) {
//        Pattern pattern = Pattern.compile(pattern);

        return MessageValidationUtils.OK;
    }
}
