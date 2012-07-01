package ejb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Danon
 */
public class ReshakaUploadedFile implements Serializable {
    private String contentType;
    private String name;
    private File file;
    
    public ReshakaUploadedFile(String name, String contentType, File file) {
        this.contentType = contentType;
        this.name = name;
        this.file = file;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public String getFileName() {
        return name;  
    }
    
    public void setFileName(String name) {
        this.name = name;
    }
    
    public File getFile() {
        return file;
    }
    
    public byte[] getContents() throws IOException {
        if(file==null)
            return null;
        byte r[] = new byte[(int)file.length()]; // we don't support large files!!!
        FileInputStream is = new FileInputStream(file);
        byte rr[] = Arrays.copyOf(r, is.read(r));
        is.close();
        return rr;
    }
    
    public InputStream getInputstream() throws FileNotFoundException {
        if(file==null)
            return null;
        return new FileInputStream(file);
    }
    
    
    public int getSize() {
        if(file==null)
            return 0;
        return (int)file.length();
    }

    @Override
    public String toString() {
        return "ReshakaUploadedFile{" + "contentType=" + contentType + ",\n name=" + name + ",\n file=" + file + "\n}";
    }
}
