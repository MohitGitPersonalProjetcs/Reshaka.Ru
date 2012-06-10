/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.orders;

import entity.Subject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author rogvold
 */
@FacesConverter("dateConverter")
public class DateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("SubjectConverter.getAsObject: value = " + value);
        // 04/10/2012 14:12
//        Pattern pattern = Pattern.compile("([0-9][0-9])/([0-9][0-9])/([0-9][0-9]) ([0-9][0-9]):([0-9][0-9])");
//        Matcher matcher = pattern.matcher(value);
//        boolean matchFound = matcher.find();
        try {
            SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            Date date = f.parse(value);
            return date;
        } catch (Exception ex) {
            throw new ConverterException(ex);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (!(value instanceof Date)) {
            throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion failed", "Invalid instance type"));
        }

        Date date = (Date) value;
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        return f.format(date);
        //return s.getId().toString();
    }
}
