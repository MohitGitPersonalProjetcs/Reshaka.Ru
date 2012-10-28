package web.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;

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
    
    public static String decode(String value, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        if(value == null) {
            return null;
        }
        return new String(value.getBytes(charset));
    }
    
    public static String convertEncodings(String value, String srcCharset, String dstCharset) {
        return decode(decode(value, srcCharset), dstCharset);
    }
}
