package web.chat;

import data.SimpleUser;
import ejb.MessageManagerLocal;
import entity.User;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import web.utils.SessionListener;

/**
 * Converts SimpeUser to ID and vice versa.
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
@FacesConverter(value="simpleUser")
public class SimpleUserConverter implements Converter {

    private static Logger log = Logger.getLogger(SimpleUserConverter.class);
    
    @EJB
    static MessageManagerLocal mm;
    
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        User u = (User)SessionListener.getSessionAttribute("user", true);
        Object result = null;
        if(u!=null && u.getId()!=null) {
            List<SimpleUser> l = mm.filterUsersByLogin(u.getId(), value); 
            if(l!=null&&l.size()==1)
                result = l.get(0);
        }
        
        if(log.isTraceEnabled())
            log.trace("getAsObject(): value="+value+" was resolved to "+result);    
        return result;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String result = null;
        if(value instanceof SimpleUser) {
            SimpleUser su = (SimpleUser)value;
            result = su.getLogin();
        }
        
        if(log.isTraceEnabled())
            log.trace("getAsString(): value="+value+" was resolved to "+result);
        return result;
    }
    
}
