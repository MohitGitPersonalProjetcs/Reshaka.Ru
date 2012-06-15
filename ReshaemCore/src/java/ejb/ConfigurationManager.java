package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

/**
 * Configuration manager provides access to the program configuration. The
 * configuration is stored in the XML-file.
 *
 * @author rogvold
 */
@Singleton
public class ConfigurationManager implements ConfigurationManagerLocal {

    Logger log = Logger.getLogger(ConfigurationManager.class.getName());
    private static Map<String, Object> config = new HashMap<>();
    public static final URL DEFAULT_XML_CONFIG_FILE = ConfigurationManager.class.getResource("/config.xml");
    public static final String CURRENT_XML_CONFIG_FILE = new File("./config.xml").getAbsolutePath();

    @PostConstruct
    synchronized public void init() {
        reload();
    }

    @PreDestroy
    synchronized public void save() {
        Document doc = null;
        File xmlFile = null;
        try {
            // Initialize DOM
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            DOMImplementation domImplementation = db.getDOMImplementation();
            doc = domImplementation.createDocument("", "ReshakaConfig", null);

            // Populate DOM Tree
            buildTree(doc, doc.getDocumentElement());

            // Initialize transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            Properties transfProps = new Properties();
            transfProps.put("method", "xml");
            transfProps.put("indent", "yes");
            transformer.setOutputProperties(transfProps);

            // Output XML
            DOMSource source = new DOMSource(doc);
            OutputStream out = new FileOutputStream(new File(CURRENT_XML_CONFIG_FILE));
            StreamResult result = new StreamResult(out);
            //StreamResult result =  new StreamResult(System.out);
            transformer.transform(source, result);
        } catch (DOMException | ParserConfigurationException ex) {
            log.warn("save(): failed to create XML DOM Document", ex);
        } catch (TransformerException ex) {
            log.warn("save(): failed to create XML Transformer", ex);
        } catch (FileNotFoundException ex) {
            log.warn("save(): failed to write XML output");
        } catch (Exception ex) {
            log.warn("save(): exception occured");
        }
    }

    private void buildTree(Document doc, Node root) {
        doc.setXmlStandalone(true);
        doc.setStrictErrorChecking(true);
        doc.setXmlVersion("1.0");
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            Element parameter = doc.createElement("parameter");
            parameter.setAttribute("name", entry.getKey());
            Object val = entry.getValue();
            parameter.setAttribute("list", (val instanceof List) + "");
            if (val instanceof List) {
                for (Object o : (List) val) {
                    Element elem = doc.createElement("value");
                    elem.setTextContent(o.toString());
                    parameter.appendChild(elem);
                }
            } else {
                Element elem = doc.createElement("value");
                elem.setTextContent(val.toString());
                parameter.appendChild(elem);
            }
            root.appendChild(parameter);
        }
    }

    @Override
    synchronized public Object getParameter(String param) {
        return config.get(param);
    }

    @Override
    synchronized public void load() {
        load(CURRENT_XML_CONFIG_FILE);
    }

    private void addParameterFromXML(Element element) {
        String name = element.getAttribute("name");
        boolean isArray = false;
        try {
            isArray = Boolean.parseBoolean(element.getAttribute("list"));
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("addParameterFromXML(): illegal value of atribute 'list' in parameter(" + name + ") declaration. "
                        + "Using false by default");
            }
        }
        if (config.get(name) != null) {
            if (log.isDebugEnabled()) {
                log.debug("addParameterFromXML(): the parameter " + name + " seems to have duplicate declarations. "
                        + "The last value will be used.");
            }
        }
        if (name == null || name.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("addParameterFromXML(): parameter without name! skipped");
            }
            return;
        }
        NodeList children = element.getElementsByTagName("value");
        if (children == null) {
            if (log.isDebugEnabled()) {
                log.debug("addParameterFromXML(): the parameter " + name + " has no value");
            }
            config.put(name, null);
            return;
        }
        if (children.getLength() == 1 && !isArray) {
            Node n = children.item(0);
            if (n instanceof Element) {
                Element elem = (Element) n;
                String value = getParameterValue(elem);
                if (value != null) {
                    config.put(name, value);
                    if (log.isTraceEnabled()) {
                        log.trace("addParameterFromXML(): parameter " + name + " is loaded");
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("addParameterFromXML(): failed to parse parameter value! skipped");
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("addParameterFromXML(): failed to parse parameter " + name);
                }
            }
            return;
        }
        // multiple values or list=true
        List<String> list = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element) {
                Element elem = (Element) n;
                String value = getParameterValue(elem);
                if (value != null) {
                    list.add(value);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("addParameterFromXML(): failed to parse list element for parameter " + name + "! skipped");
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("addParameterFromXML(): failed to parse list element for parameter " + name + "! skipped");
                }
            }
        }
        config.put(name, list);
        if (log.isTraceEnabled()) {
            log.trace("addParameterFromXML(): parameter " + name + " is loaded as list");
        }
    }

    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    private String getParameterValue(Element elem) {
        Node node = elem.getFirstChild();
        if (node instanceof Text) {
            Text text = (Text) node;
            return text.getData();
        }
        return null;
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    synchronized public Long getMainAdminId() {
        return Long.parseLong(getParameter("mainAdminId").toString());
    }

    @Override
    @Transactional(value = Transactional.TransactionFlowType.SUPPORTS)
    synchronized public double getAdminPercent() {
        return Double.parseDouble(getParameter("adminPercent", "0.3").toString());
    }

    @Override
    synchronized public void load(String xmlFile) {
        if (log.isTraceEnabled()) {
            log.trace("load(xmlFile): xmlFile=" + xmlFile);
        }
        if (xmlFile == null) {
            return;
        }
        File xml = new File(xmlFile);
        if (!xml.canRead()) {
            if (log.isTraceEnabled()) {
                log.trace("load(xmlFile): can't read xurrent config.xml Using default instead: " + DEFAULT_XML_CONFIG_FILE);
            }
            try {
                xml = new File(DEFAULT_XML_CONFIG_FILE.toURI());
            } catch (URISyntaxException ex) {
                log.warn("load(xmlFile): failed to load default configuration file", ex);
                return;
            }
        }


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            if (!xml.canRead()) {
                throw new IOException("Cannot open file " + xml.getAbsolutePath());
            }
            Document doc = builder.parse(xml);
            Element root = doc.getDocumentElement();

            if (!root.getTagName().equals("ReshakaConfig")) {
                throw new ParseException("Invalid start xml tag: " + root.getTagName(), 0);
            }

            NodeList children = root.getElementsByTagName("parameter");
            if (children.getLength() == 0) {
                if (log.isTraceEnabled()) {
                    log.trace("load(): the parameter list in xml file is empty");
                }
                return;
            }

            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (n instanceof Element) {
                    addParameterFromXML((Element) n);
                }
            }
            if (log.isTraceEnabled()) {
                log.trace("load(xmlFile): configuration loaded from " + xmlFile);
            }
        } catch (Exception ex) {
            log.warn("load(): failed to load xml config file.", ex);
        }
    }

    @Override
    synchronized public void reload() {
        reload(CURRENT_XML_CONFIG_FILE);
    }

    @Override
    synchronized public void reload(String xmlFile) {
        if (log.isTraceEnabled()) {
            log.trace(">> reload(): xmlFile = " + xmlFile);
        }
        config.clear();
        load(xmlFile);
        if (log.isTraceEnabled()) {
            log.trace("<< reload()");
        }
    }

    @Override
    synchronized public Object getParameter(String paramName, Object defaultValue) {
        Object value = getParameter(paramName);
        return value == null ? defaultValue : value;
    }

    @Override
    synchronized public String getString(String paramName) {
        return getString(paramName, null);
    }

    @Override
    synchronized public String getString(String paramName, String defaultValue) {
        Object value = getParameter(paramName);
        return value == null ? defaultValue : value.toString();
    }

    @Override
    synchronized public Integer getInteger(String paramName) {
        return getInteger(paramName, null);
    }

    @Override
    synchronized public Integer getInteger(String paramName, Integer defaultValue) {
        Object value = getParameter(paramName);
        return value == null ? defaultValue : Integer.valueOf(value.toString());
    }

    synchronized @Override
    public Long getLong(String paramName) {
        return getLong(paramName, null);
    }

    @Override
    synchronized public Long getLong(String paramName, Long defaultValue) {
        Object value = getParameter(paramName);
        return value == null ? defaultValue : Long.valueOf(value.toString());
    }

    @Override
    synchronized public Double getDouble(String paramName) {
        return getDouble(paramName, null);
    }

    @Override
    synchronized public Double getDouble(String paramName, Double defaultValue) {
        Object value = getParameter(paramName);
        return value == null ? defaultValue : Double.valueOf(value.toString());
    }

    @Override
    synchronized public Boolean getBoolean(String paramName) {
        return getBoolean(paramName, null);
    }

    @Override
    synchronized public Boolean getBoolean(String paramName, Boolean defaultValue) {
        Object value = getParameter(paramName);
        return value == null ? defaultValue : Boolean.valueOf(value.toString());
    }

    @Override
    synchronized public List getList(String paramName) {
        return getList(paramName, null);
    }

    @Override
    synchronized public <T> List<T> getList(String paramName, List<T> defaultValue) {
        Object value = getParameter(paramName);
        if (value instanceof List) {
            return value == null ? defaultValue : (List<T>) value;
        } else {
            return null;
        }
    }

    @Override
    synchronized public List<String> getParameterNames() {
        return Collections.synchronizedList(new ArrayList(config.keySet()));
    }

    @Override
    synchronized public void setString(String paramName, String value) {
        config.put(paramName, value);
        if (value == null) {
            config.remove(paramName);
        }
        save();
    }

    @Override
    public void setInteger(String paramName, Integer value) {
        config.put(paramName, value);
        if (value == null) {
            config.remove(paramName);
        }
        save();
    }

    @Override
    public void setLong(String paramName, Long value) {
        config.put(paramName, value);
        if (value == null) {
            config.remove(paramName);
        }
        save();
    }

    @Override
    public void setDouble(String paramName, Double value) {
        config.put(paramName, value);
        if (value == null) {
            config.remove(paramName);
        }
        save();
    }

    @Override
    public void setBoolean(String paramName, Boolean value) {
        config.put(paramName, value);
        if (value == null) {
            config.remove(paramName);
        }
        save();
    }

    @Override
    public void setParameter(String paramName, Object value) {
        config.put(paramName, value);
        if (value == null) {
            config.remove(paramName);
        }
        save();
    }

    @Override
    public Map<Integer, String> getOrderStatusDescription() {
        Map map = new HashMap();
        try {
            map.put(0, getString("statusNew")); //new
            map.put(1, getString("statusRated")); //rated
            map.put(4, getString("statusSolved")); //chosen
            map.put(5, getString("statusPayed")); //agreed
        } catch (Exception e) {
        }
        return map;
    }

    @Override
    public Date getDate(String paramName) {
        String s = getString(paramName, "01/01/1495 00:00:00");
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            Date date = f.parse(s);
            return date;
        } catch (ParseException ex) {
            return null;
        }

    }

    @Override
    public Date getDate(String paramName, Date defaultValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDate(String paramName, Date value) {
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        setString(paramName, f.format(value));
    }
}