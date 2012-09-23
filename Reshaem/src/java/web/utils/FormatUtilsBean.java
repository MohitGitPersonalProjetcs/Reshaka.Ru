package web.utils;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * Provides some useful methods for formatting strings
 * @author danon
 */
@ManagedBean
@ApplicationScoped
public class FormatUtilsBean {

    public static String normalizeFileSize(long fileSize) {
        if(fileSize < 0)
            return "недоступно";
        if(fileSize<1500)
            return fileSize + " байт";
        fileSize /= 1024;
        if(fileSize<1500)
            return fileSize + " КБ";
        fileSize /= 1024;
        return fileSize + " МБ";
    }
    
}
