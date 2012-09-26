package ejb;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 * An interface which provides the access to the main configuration file.
 *
 * @author Anton Danshin <danon.oren@gmail.com>
 */
@Local
public interface ConfigurationManagerLocal {

    Object getParameter(String paramName);

    Object getParameter(String paramName, Object defaultValue);

    String getString(String paramName);

    String getString(String paramName, String defaultValue);

    Integer getInteger(String paramName);

    Integer getInteger(String paramName, Integer defaultValue);

    Long getLong(String paramName);

    Long getLong(String paramName, Long defaultValue);

    Double getDouble(String paramName);

    Double getDouble(String paramName, Double defaultValue);

    Boolean getBoolean(String paramName);

    Boolean getBoolean(String paramName, Boolean defaultValue);

    Date getDate(String paramName);

    Date getDate(String paramName, Date defaultValue);

    void setDate(String paramName, Date value);

    void setString(String paramName, String value);

    void setInteger(String paramName, Integer value);

    void setLong(String paramName, Long value);

    void setDouble(String paramName, Double value);

    void setBoolean(String paramName, Boolean value);

    void setParameter(String paramName, Object value);

    List getList(String paramName);

    <T> List<T> getList(String paramName, List<T> defaultValue);

    List<String> getParameterNames();

    void load();

    void load(String xmlFile);

    void reload();

    public void reload(String xmlFile);

    public Map<Integer, String> getOrderStatusDescription();

    public Long getMainAdminId();

    public double getAdminPercent();
    
    public Long getAdminId();
}
