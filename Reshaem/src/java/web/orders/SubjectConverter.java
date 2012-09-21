package web.orders;

import ejb.SubjectManagerLocal;
import entity.Subject;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Danon
 */
@FacesConverter("subjectConverter")
public class SubjectConverter implements Converter {

    public static volatile SubjectManagerLocal sm;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("SubjectConverter.getAsObject: value = " + value);
        try {
            Map<Long, String> map = sm.getAllSubjectsMap();
            Long id = new Long(value);
            if (map.containsKey(id)) {
                Subject s = new Subject(map.get(id));
                s.setId(id);
                System.out.println("Converted: " + s);
                return s;
            }
        } catch (Exception ex) {
            throw new ConverterException(ex);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
//        System.out.println("SubjectConverter.getAsString: value = " + value);
        if (!(value instanceof Subject)) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion failed", "Invalid instance type"));
        }
        Subject s = (Subject) value;
        return s.getId().toString();
    }
}
