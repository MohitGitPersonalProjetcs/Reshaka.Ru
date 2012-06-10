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
import web.utils.SessionListener;

/**
 *
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
@FacesConverter(value="simpleUser")
public class SimpleUserConverter implements Converter {

    @EJB
    static MessageManagerLocal mm;
    
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("Convert: value="+value);
        User u = (User)SessionListener.getSessionAttribute("user", true);
        if(u!=null && u.getId()!=null) {
            List<SimpleUser> l = mm.filterUsersByLogin(u.getId(), value); 
            if(l!=null&&l.size()==1)
                return l.get(0);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value instanceof SimpleUser) {
            SimpleUser su = (SimpleUser)value;
            return su.getLogin();
        }
        return null;
    }
    
}
