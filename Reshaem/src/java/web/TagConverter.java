package web;

import ejb.TagManagerLocal;
import entity.Tag;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Sabir
 */
@FacesConverter(value = "tagConverter")
public class TagConverter implements Converter {

    @EJB
    static TagManagerLocal tagMan;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("Convert: value=" + value);

        List<Tag> l = tagMan.completeTagList(value);



        System.out.println("TagConverter: list = " + l);
        if (l != null && l.size() == 1) {
            return l.get(0);
        }

        if (l == null) {
            return tagMan.addTag(value);
        }
        
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Tag) {
            Tag tag = (Tag) value;
            return tag.getText();
        }
        return null;
    }
}
