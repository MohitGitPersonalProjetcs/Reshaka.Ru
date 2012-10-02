package web.utils;

import ejb.util.FileUtils;
import ejb.util.ReshakaSortOrder;
import ejb.util.ReshakaUploadedFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Danon
 */
public class Tools {

    /**
     * Converts PrimeFaces sort order model into ReshakaSortOrder
     * <p/>
     * @param o source SortOrder (PrimeFaces)
     * <p/>
     * @return Reshaka.Ru equivalent
     */
    public static ReshakaSortOrder convertSortOrder(SortOrder o) {
        if (o == null) {
            return null;
        }

        switch (o) {
            case ASCENDING:
                return ReshakaSortOrder.ASCENDING;
            case DESCENDING:
                return ReshakaSortOrder.DESCENDING;
            default:
                return ReshakaSortOrder.UNSORTED;
        }
    }

    public static ReshakaUploadedFile convertUploadedFile(UploadedFile uf) {
        if (uf == null) {
            return null;
        }
        try {
            File f = File.createTempFile("more_longer_uc", ".tmp");
            try (FileOutputStream os = new FileOutputStream(f)) {
                FileUtils.copyStream(uf.getInputstream(), os);
            }
            return new ReshakaUploadedFile(uf.getFileName(), uf.getContentType(), f);
        } catch (IOException ex) {
            System.err.println("convertUploadedFile(): " + ex);
        }
        return null;
    }

    public static int getHourDateDifference(Date first, Date second) {
        long diff = second.getTime() - first.getTime();
        return (int) (diff / (1000 * 60 * 60));
    }
    
    public static int getPassedHours(Date date){
     return getHourDateDifference(date, new Date());   
    }
    
    public static String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte byteData[] = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return "";
    }
}
