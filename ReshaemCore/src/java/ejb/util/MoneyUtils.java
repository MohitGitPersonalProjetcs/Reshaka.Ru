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
public class MoneyUtils {

    public static Long getWebmoneyLMIPaymentNO(String text) {
        Pattern pattern = Pattern.compile("LMI_PAYMENT_NO=(\\d*)");
        Matcher matcher = pattern.matcher(text);
        try {
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
        } catch (Exception exc) {
            return null;
        }
        return null;
    }

    public static Double getWebmoneyLMIPaymentAmount(String text) {
        Pattern pattern = Pattern.compile("LMI_PAYMENT_AMOUNT=(\\d*\\.*\\d*)");
        Matcher matcher = pattern.matcher(text);
        try {
            if (matcher.find()) {
                return Double.parseDouble(matcher.group(1));
            }
        } catch (Exception exc) {
            return null;
        }
        return null;
    }

    public static String getWebmoneyLMIHash(String text) {
        Pattern pattern = Pattern.compile("LMI_HASH=(.*)");
        Matcher matcher = pattern.matcher(text);
        try {
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception exc) {
            return null;
        }
        return null;
    }
}
