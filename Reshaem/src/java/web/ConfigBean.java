package web;

import ejb.ConfigurationManagerLocal;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Anton Danshin <anton.danshin@frtk.ru>
 */
@ManagedBean
@RequestScoped
public class ConfigBean {

    private String name;
    private String value;
    
    @EJB
    ConfigurationManagerLocal cm;
    
    /**
     * Creates a new instance of ConfigBean
     */
    public ConfigBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }
    
    public void updateValue(ActionEvent evt) {
        value = cm.getString(name, "(none)");
    }
    
}
