package ejb.util;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import org.apache.log4j.Logger;

/**
 * Contains some methods for working with files and streams
 *
 * @author Danon
 */
public class FileUtils {
    
    private static Logger log = Logger.getLogger(FileUtils.class);

    /**
     * Redirects one stream into another
     * @param src Source stream (InputStream)
     * @param dst Destination stream (OutputStream)
     * @throws IOException
     */
    public static void copyStream(InputStream src, OutputStream dst) throws IOException {
        byte[] buffer = new byte[512 * 1024];
        int len = -1;
        while ((len = src.read(buffer)) >= 0) {
            dst.write(buffer, 0, len);
        }
    }

    /**
     * Deletes file with specified path name
     * @param fileName absolute name of the file to delete
     * @return true - if file was deleted, otherwise - false
     */
    public static boolean deleteFile(String fileName) {
        try {
            boolean b = new File(fileName).delete();
            log.info("File deleted: "+fileName+" => "+b);
            return b;
        } catch (Exception ex) {
            log.warn("Failed to delete file "+fileName, ex);
            return false;
        }
    }

    /**
     * Creates temporary file with specified binary content
     * @param bin content of target file
     * @return temp file
     * @throws IOException 
     */
    public static File tmpFileFromBin(byte[] bin) throws IOException {
        File f = File.createTempFile("tmp_", ".bin");
        log.trace("Temp file created "+f.getAbsolutePath());
        try (FileOutputStream fout = new FileOutputStream(f)) {
            fout.write(bin);
        }
        log.trace("content is written successfully.");
        return f;
    }

    /**
     * Checks if file with specified name can be read
     */
    public static boolean canRead(String filePath) {
        return new File(filePath).canRead();
    }

    /**
     * Writes raw data into specified file
     * @throws IOException 
     */
    public static void writeToFile(File f, byte[] data) throws IOException {
        try (FileOutputStream fout = new FileOutputStream(f)) {
            fout.write(data);
        }
        log.trace("Data successfully written to file "+f);
    }

    /**
     * Calculates MD5 of the file
     * @param f
     * @return Calculated MD5 or empty string if operation failed
     */
    public static String getMD5(File f) {
        String output = "";
        try {
            if (f.exists()) {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                try (InputStream is = new FileInputStream(f)) {
                    byte[] buffer = new byte[819200];
                    int read;
                    while ((read = is.read(buffer)) > 0) {
                        digest.update(buffer, 0, read);
                    }
                    byte[] md5sum = digest.digest();
                    BigInteger bigInt = new BigInteger(1, md5sum);
                    output = bigInt.toString(16);
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            log.warn("Failed to calculate MD5 for "+f, e);
        }
        return output;
    }

    /**
     * Returns extention of the file with specified name.
     * For example: "/tmp/my_file.bin.tmp" => ".tmp"
     * @param name path name of the file
     * @return file extention or empty sting
     */
    public static String extractExtention(String name) {
        if(name==null)
            return "";
        int i = name.lastIndexOf(".");
        if(i>=0)
            return name.substring(i);
        else return "";
    }
}
