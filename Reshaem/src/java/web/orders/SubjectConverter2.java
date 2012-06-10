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
@FacesConverter("subjectConverter2")
public class SubjectConverter2 implements Converter {

    public static volatile SubjectManagerLocal sm;
    
    
//    @Override
//    public Object getAsObject(FacesContext context, UIComponent component, String value) {
//        System.out.println("SubjectConverter2.getAsObject: value = "+value);
//        try {
//            Subject s = new entity.Subject();
//            s = sm.getSubjectById(Long.parseLong(value));
//            return s;
//            //return sm.getSubjectById(Long.parseLong(value));
//            }
//        } catch (Exception ex) {
//            throw new ConverterException(ex);
//        }
//        return null;
//    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("SubjectConverter2.getAsString: value = "+value);
        if(!(value instanceof Subject))
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion failed", "Invalid instance type"));
        Subject s = (Subject)value;
        return s.getSubjectName();
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Subject s = sm.getSubjectBySubjectName(value);
        return s;
    }
    
}
