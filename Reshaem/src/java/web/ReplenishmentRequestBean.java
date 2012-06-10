package web;

import ejb.AttachmentManagerLocal;
import ejb.ReplenishmentRequestManagerLocal;
import entity.User;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ReplenishmentRequestBean implements Serializable {

    @EJB
    ReplenishmentRequestManagerLocal replMan;

    @EJB
    AttachmentManagerLocal attMan;

    private String text;

    private String type;

    private double money;

    private FileUploadController fileUploadController;

    public ReplenishmentRequestBean() {
        fileUploadController = new FileUploadController();
    }

    @PostConstruct
    private void init() {
        fileUploadController.setAttachmentManager(attMan);
    }

    public FileUploadController getFileUploadController() {
        return fileUploadController;
    }

    public void setFileUploadController(FileUploadController fileUploadController) {
        this.fileUploadController = fileUploadController;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addReplenishmentRequest() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(true);
        User user = (User) session.getAttribute("user");
        Long userId = null;
        if (user != null) {
            userId = user.getId();
        }
        Long solId = null;
        if (fileUploadController != null) {
            System.out.println("fileUploadController is not null!!!");
            fileUploadController.uploadFiles(null);
            solId = fileUploadController.getFileInfo().getId();
        }

        System.out.println("try to add repl req : userId/money/type/text/solId = " + userId + "/" + money + "/" + type + "/" + text + "/" + solId);

        replMan.createReplenishmentRequest(userId, money, type, text, solId);
        fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Заявка отправлена", "В ближайшее время администратор рассмотрит вашу заявку и пополнит Ваш внутренний счет в системе"));
    }
}
