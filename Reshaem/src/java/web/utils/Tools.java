package web.utils;

import ejb.util.FileUtils;
import ejb.util.ReshakaSortOrder;
import ejb.util.ReshakaUploadedFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Danon
 */
public class Tools {

    /**
     * Converts PrimeFaces sort order model into ReshakaSortOrder 
     * @param o source SortOrder (PrimeFaces)
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
        if(uf==null)
            return null;
        try {
            File f = File.createTempFile("uc", ".tmp");
            try (FileOutputStream os = new FileOutputStream(f)) {
                FileUtils.copyStream(uf.getInputstream(), os);
            }
            return new ReshakaUploadedFile(uf.getFileName(), uf.getContentType(), f);
        } catch (IOException ex) {
            System.err.println("convertUploadedFile(): "+ex);
        }
        return null;
    }
}
