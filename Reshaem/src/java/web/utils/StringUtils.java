/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rogvold
 */
public class StringUtils {

    public static List<String> parseAutocomplete(String text) {
        String[] s = text.split("\\,");
        if (s == null) {
            return null;
        }
        List<String> list = new ArrayList();
//                Arrays.asList(s);
        for (String ss : s) {
            list.add(ss.trim());
        }

        System.out.println("parseAutocomplete: list = " + list);
        return list;
    }

    public static String getLastWord(String text) {
        text = "," + text;
        int a = text.lastIndexOf(",") + 1;

        return text.substring(a).trim();
    }

    public static String getFirstPartOfText(String text) {
        int a = text.lastIndexOf(",");
        if (a < 0) {
            return "";
        }
        return text.substring(0, a);
    }
    
     public static String getFirstWord(String text) {
        int a = text.indexOf(",");
        if (a < 0) {
            return text;
        }
        return text.substring(0, a);
    }
}
