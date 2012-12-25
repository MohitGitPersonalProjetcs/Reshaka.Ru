package web.orders;

import ejb.OrderManagerLocal;
import ejb.SubjectManagerLocal;
import ejb.TagManagerLocal;
import ejb.UserManagerLocal;
import ejb.util.ReshakaUploadedFile;
import entity.Order;
import entity.Subject;
import entity.Tag;
import entity.User;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.primefaces.context.RequestContext;
import web.FileUploadController;
import web.utils.SessionListener;
import web.utils.StringUtils;

/**
 *
 * @author ASUS
 */
@ManagedBean
@ViewScoped
public class MakeOrderBean implements Serializable {

    @EJB
    SubjectManagerLocal subjMan;

    @EJB
    OrderManagerLocal om;

    @EJB
    UserManagerLocal userMan;

    @EJB
    TagManagerLocal tagMan;

    private transient HttpSession session = null;

    private List<Subject> allSubjects;

    private Order order;

    private boolean agreed;

    private boolean skip;

    private boolean ticked;

    private final List<Subject> subjects;

    private FileUploadController fileUploadController;

    private String tagsText;

    public List<ReshakaUploadedFile> getUploadedFiles() {
        return fileUploadController.getFiles();
    }

    public boolean isTicked() {
        return ticked;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public MakeOrderBean() {
        ticked = false;
        order = new Order();
        fileUploadController = new FileUploadController();
        subjects = new ArrayList<>();
    }

    @PostConstruct
    private void init() {
        SubjectConverter.sm = subjMan;
        subjects.addAll(subjMan.getAllSubjects(true));
        User u = (User)SessionListener.getSessionAttribute("user", false);
        
        if(u != null && u.getId() != null) {
            u = userMan.getUserById(u.getId());
            if(u != null && u.getEmail().contains("@"))
                order.setFromEmail(u.getEmail());
        }
    }

    public String getTagsText() {
        return tagsText;
    }

    public void setTagsText(String tagsText) {
        this.tagsText = tagsText;
    }

    public Long getId() {
        return order.getId();
    }

    public void setId(Long id) {
        order.setId(id);
    }

    public String getDuration() {
        return ""+order.getDuration();
    }

    public void setDuration(String duration) {
        try {
            order.setDuration(Integer.parseInt(duration));
        } catch(Exception ex) {
            order.setDuration(-1);
        }
    }

    public Long getConditionId() {
        return order.getConditionId();
    }

    public void setConditionId(Long conditionId) {
        order.setConditionId(conditionId);
    }

    public Date getDeadline() {
        return order.getDeadline();
    }

    public void setDeadline(Date deadline) {
        System.out.println("try to set deadline ..." + deadline);
        order.setDeadline(deadline);
    }

    public String getDescription() {
        return order.getDescription();
    }

    public void setDescription(String description) {
        order.setDescription(description);
    }
    
    public String getFromEmail(){
        return order.getFromEmail();
    }
    
    public void setFromEmail(String fromEmail){
        order.setFromEmail(fromEmail);
    }

    public User getEmployee() {
        return order.getEmployee();
    }

    public void setEmployee(User employee) {
        order.setEmployee(employee);
    }

    public User getEmployer() {
        return order.getEmployer();
    }

    public void setEmployer(User employer) {
        order.setEmployer(employer);
    }

    public String getPrice() {
        if(order.getPrice() == 0.0)
            return "0.0";
        return NumberFormat.getInstance(new Locale("ru", "RU")).format(order.getPrice());
    }

    public void setPrice(String price) {
        try {
            double val = Double.parseDouble(price);
            order.setPrice(val);
        } catch(Exception ex) {
            order.setPrice(-1);
        }
    }

    public void setStatus(int status) {
        order.setStatus(status);
    }

    public void setSubject(Subject subject) {
        System.out.println("setSubject: " + subject);
        order.setSubject(subject);
    }

    public void setTags(String tags) {
        order.setTags(tags);
    }

    public void setType(int type) {
        order.setType(type);
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public FileUploadController getFileUploadController() {
        return fileUploadController;
    }

    public void setFileUploadController(FileUploadController fileUploadController) {
        this.fileUploadController = fileUploadController;
    }

    public List<Subject> getAllSubjects() {
        return subjects;
//        return subjMan.getAllSubjects();
    }

    private boolean isSignedIn() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (SessionListener.getSessionAttribute("user", true) != null) {
            //  System.out.println("--------->>>> current user is" + SessionListener.getSessionAttribute("user", true).toString());
            return true;
        }
        return false;
    }

    private User getUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        session = (HttpSession) context.getExternalContext().getSession(true);
        return (User) SessionListener.getSessionAttribute("user", true);
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public Subject getSubject() {
        return order.getSubject();
    }

    public String getMinDate() {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
        return f.format(date);

    }

    public List<String> completeTag(String query) {
        List<Tag> s = tagMan.completeTagList(StringUtils.getLastWord(query));
        List<String> list = new ArrayList();
        for (Tag tag : s) {
            if ("".equals(StringUtils.getFirstPartOfText(this.tagsText))) {
                list.add(tag.getText());
            } else {
                list.add(StringUtils.getFirstPartOfText(this.tagsText) + ", " + tag.getText());
            }
        }

        return list;
    }
    
    private boolean validateOnlineOrder(FacesContext fc, RequestContext rc) {
        if(order.getDuration() < 0) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Недопустимое значение поля 'Продолжительность'"));
            rc.addCallbackParam("offline", false);
            return false;
        }
        
        if(order.getPrice() < 0) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Занчение в поле 'Цена' введено некорректно"));
            rc.addCallbackParam("offline", false);
            return false;
        }
        
        if ((order.getDeadline() == null) && ((order.getSubject() == null) || (Subject.NOT_SELECTED_SUBJECT_NAME.equals(order.getSubject().getSubjectName())))) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Вы забыли выбрать дату/время и предмет"));
            rc.addCallbackParam("offline", false);
            return false;
        }

        if (order.getDeadline() == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Вы забыли ввести дату/время"));
            rc.addCallbackParam("offline", false);
            return false;
        }

        if ((order.getSubject() == null) || (Subject.NOT_SELECTED_SUBJECT_NAME.equals(order.getSubject().getSubjectName()))) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Вы забыли выбрать предмет"));
            rc.addCallbackParam("offline", false);
            return false;
        }
        
        if (order.getFromEmail() == null || order.getFromEmail().trim().isEmpty()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Пожалуйста, укажите адрес электронной почты."));
            rc.addCallbackParam("offline", false);
            return false;
        }
        return true;
    }
    
    private boolean validateOfflineOrder(FacesContext fc, RequestContext rc) {
        if(order.getPrice() < 0) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Занчение в поле 'Цена' введено некорректно"));
            rc.addCallbackParam("offline", false);
            return false;
        }
        
        if ((order.getDeadline() == null) && ((order.getSubject() == null) || (Subject.NOT_SELECTED_SUBJECT_NAME.equals(order.getSubject().getSubjectName())))) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Вы забыли выбрать срок и предмет"));
            rc.addCallbackParam("offline", false);
            return false;
        }

        if (order.getDeadline() == null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Вы забыли ввести срок"));
            rc.addCallbackParam("offline", false);
            return false;
        }

        if ((order.getSubject() == null) || (Subject.NOT_SELECTED_SUBJECT_NAME.equals(order.getSubject().getSubjectName()))) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Вы забыли выбрать предмет"));
            rc.addCallbackParam("offline", false);
            return false;
        }

        if(fileUploadController.getFiles().isEmpty()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Пожалуйста, загрузите файл с увловием задачи"));
            rc.addCallbackParam("offline", false);
            return false;
        }
        
        return true;
    }

    public void submitOrder(ActionEvent event) {
        if (order.getDescription() == null) {
            order.setDescription(" ");
        }

        FacesContext fc = FacesContext.getCurrentInstance();
        RequestContext rc = RequestContext.getCurrentInstance();

        if(!validateOfflineOrder(fc, rc))
            return;

        System.out.println("submit order from bean getUser() = " + getUser());

        order.setEmployer(getUser());
        //System.out.println("subject = " + subject);
        Date date = new Date();
        order.setHireDate(date);

//        tagsText = order.getSubject().getSubjectName() + ", " + tagsText;
        order.setTags(tagsText);
        tagMan.addTags(tagsText);
        //order.setType(0);
        order.setStatus(0);

        Order ord = om.submitOrder(order, fileUploadController.getFiles()); // вот она отправка заказа моей мечты. 
        if (order == null) {
            System.out.println("ERROR occured while submiting order");
        }

        try {
            System.out.println("try to understand if this order is the first////");
            if (userMan.getUserById(getUser().getId()).getOrderedAmount() == 1) {
                System.out.println("FIRST ORDER -> Greetings!!! ");
                RequestContext.getCurrentInstance().addCallbackParam("first", true);
            } else {
                System.out.println("order is not the first ");
                RequestContext.getCurrentInstance().addCallbackParam("first", false);
            }
        } catch (Exception exc) {
            System.out.println("exception occured during submitOrder (understanding first or not)");
        }
        rc.addCallbackParam("offline", true);
    }

    public void submitOnline(ActionEvent evt) {
        if (order.getDescription() == null) {
            order.setDescription(" ");
        }
        //fileUploadController.uploadFiles(event);
        if (fileUploadController != null) {
            order.setConditionId(fileUploadController.getFileInfo().getId());
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        RequestContext rc = RequestContext.getCurrentInstance();
        
        if(!validateOnlineOrder(fc, rc))
            return;
        
        System.out.println("submit order from bean getUser() = " + getUser());

        order.setEmployer(getUser());
        //System.out.println("subject = " + subject);
        Date date = new Date();
        order.setHireDate(date);
//        tagsText = order.getSubject().getSubjectName() + ", " + tagsText;
        order.setTags(tagsText);
        tagMan.addTags(tagsText);

        order.setType(Order.ONLINE_TYPE);

        order.setStatus(Order.NEW_ONLINE_ORDER_STATUS);

        Order ord = om.submitOrder(order, fileUploadController.getFiles()); // вот она отправка заказа моей мечты. 
        if (order == null) {
            System.out.println("ERROR occured while submiting order");
        }

        //   if (getUser().getOrderedAm1ount() == 0) {
        try {
            if (userMan.getUserById(getUser().getId()).getOrderedAmount() == 1) {
                RequestContext.getCurrentInstance().addCallbackParam("first", true);
            } else {
                RequestContext.getCurrentInstance().addCallbackParam("first", false);
            }
        } catch (Exception exc) {
        }
        rc.addCallbackParam("online", true);
    }

    public boolean shouldShowFilesList() {
        System.out.println(fileUploadController.getFiles());
        return !fileUploadController.getFiles().isEmpty();
    }

    public List<ReshakaUploadedFile> uploadedFiles() {
        System.out.println("size= " + fileUploadController.getFiles().size());
        return fileUploadController.getFiles();
    }

}
