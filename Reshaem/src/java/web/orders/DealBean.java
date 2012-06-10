package web.orders;

import ejb.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import web.FileUploadController;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class DealBean {

    private transient HttpSession session = null;

    @EJB
    DealManagerLocal dealMan;

    @EJB
    MoneyManagerLocal monMan;

    @EJB
    OrderManagerLocal ordMan;

    @EJB
    UserManagerLocal userMan;

    @EJB
    AttachmentManagerLocal attMan;
    
    private boolean uploaded;
    
    private Long currentOrderId;

    private FileUploadController fileUploadController;

    public DealBean() {
        fileUploadController = new FileUploadController();
    }
    
    @PostConstruct
    private void init(){
        fileUploadController.setAttachmentManager(attMan);
        this.uploaded = false;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    
    
    public Long getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(Long currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public FileUploadController getFileUploadController() {
        return fileUploadController;
    }

    public void setFileUploadController(FileUploadController fileUploadController) {
        this.fileUploadController = fileUploadController;
    }

    public boolean canStartDeal(Long employerId, Long offerId) {
        if (dealMan.canStartDeal(employerId, offerId)) {
            return true;
        }
        return false;
    }
    
    public boolean canStartOnlineDeal(Long employerId, Long offerId) {
        if (dealMan.canStartOnlineDeal(employerId, offerId)) {
            return true;
        }
        return false;
    }

    public boolean canFinishDeal(Long orderId, Long employerId) {
        if (dealMan.canFinishDeal(orderId, employerId)) {
            return true;
        }
        return false;
    }

    public void startDeal(Long orderId, Long offerId) {
        System.out.println("orderId/offerId " + orderId + " / " + offerId);
        dealMan.startDeal(orderId, offerId);
        //updateSessionParameters();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Информация", "Решающий приступил к решению"));
    }
    
    public void startOnlineDeal(Long orderId, Long offerId) {
        System.out.println("orderId/offerId " + orderId + " / " + offerId);
        dealMan.startOnlineDeal(orderId, offerId);
        //updateSessionParameters();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Информация", "Решающий приступил к решению"));
    }

    public void makeFinalPayment(Long orderId) {
        System.out.println("Trying to make final payments in jsf : orderId = " + orderId);
        dealMan.makeFinalPayment(orderId);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Информация", "Теперь вы можете скачать решение"));
    }

    public void submitSolution(Long orderId, Long userId) {
        System.out.println("try to submit solution orderId = " + orderId);
        System.out.println("File upload controller = " + fileUploadController.toString());
        System.out.println("It contains attachment : " + fileUploadController.getFileInfo().getId());
        Long solId;
        if (fileUploadController != null) {
            fileUploadController.uploadFiles(null);
            solId = fileUploadController.getFileInfo().getId();
            System.out.println("solution ID is   ------------ ---  " + solId);
            ordMan.submitSolution(userId,orderId,solId);
            dealMan.postSolution(orderId,solId);
            this.uploaded = true;
        }
    }

    public void initDeal(Long orderId) {
        System.out.println("initDeal : " + orderId);
        setCurrentOrderId(orderId);
    }

}
