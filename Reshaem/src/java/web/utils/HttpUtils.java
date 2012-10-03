package web.utils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * A few useful methods for working with HTTP request
 * 
 * @author rogvold
 */
public class HttpUtils {

    public static String getRequestParam(String name) {
        String result = null;
        HttpServletRequest request = getRequest();
        if (request != null) {
            result = request.getParameter(name);
        }
        return result;
    }

    public static String getRequestUrl() {
        String result = null;
        HttpServletRequest request = getRequest();
        if (request != null) {
            result = request.getRequestURI();
        }
        return result;
    }
    
    public static HttpServletRequest getRequest() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            return null;
        }
        ExternalContext ec = fc.getExternalContext();
        if (ec == null) {
            return null;
        }
        return (HttpServletRequest) ec.getRequest();
    }
}
