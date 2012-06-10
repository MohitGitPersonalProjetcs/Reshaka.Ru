/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import ejb.RequestManagerLocal;
import ejb.SubjectManagerLocal;
import entity.Subject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import web.orders.SubjectConverter2;



/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class RequestBean implements Serializable {

    @EJB
    RequestManagerLocal reqMan;

    @EJB
    SubjectManagerLocal sm;

    private String email;

    private String additionalInfo;

    private String additionalContacts;

    private String name;

    private String experience;

    private String ageAndEducation;

    private String age;
    
    private String education;
    
    private String city;

    private List<entity.Subject> selectedSubjects;

    private final List<entity.Subject> allSubjects;

    public RequestBean() {
        this.allSubjects = new ArrayList<>();       
    }

    @PostConstruct
    private void init(){
        SubjectConverter2.sm = sm;
        this.allSubjects.addAll(sm.getAllSubjects());
    }

    public List<Subject> getAllSubjects() {
//        return allSubjects;
        return sm.getAllSubjects();
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    
    

    public String getAgeAndEducation() {
        return ageAndEducation;
    }

    public void setAgeAndEducation(String ageAndEducation) {
        this.ageAndEducation = ageAndEducation;
    }



    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<entity.Subject> getSelectedSubjects() {
        return selectedSubjects;
    }

    public void setSelectedSubjects(List<entity.Subject> selectedSubjects) {
        this.selectedSubjects = selectedSubjects;
    }

    public String getAdditionalContacts() {
        return additionalContacts;
    }

    public void setAdditionalContacts(String additionalContacts) {
        this.additionalContacts = additionalContacts;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addNewRequest() {
        boolean b = reqMan.canCreateRequest(email);
        
        
        this.additionalInfo = BuildInfo();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!b) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка", "Вы уже подавали заявку, либо не правильно вбили почту"));
            requestContext.addCallbackParam("partner", false);
        } else {
            reqMan.createRequest(email, additionalInfo, additionalContacts);
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Уведомление", "Ваша заявка принята. В ближайшее время с вами свяжется администратор."));
            requestContext.addCallbackParam("partner", true);
        }
        System.out.println("info = " + additionalInfo);
    }
    
    public String BuildInfo(){
        String info = "Имя : " + this.name + " ;\n"
                + "Возраст : " + this.age + " ;\n "
                + "Образование : " + this.education + " ; \n"
                + "Возраст и образование: " + this.ageAndEducation + " ;\n"
                + "Город проживания : " + this.city + " ; \n"
                + "Опыт работы: " + this.experience + "  ;\n\n"
                + "Предметы: ";
        for (Subject s: selectedSubjects){
            info = info+s.getSubjectName()+", ";
        }
        info+="\n\n";
        info+="Несколько слов о себе: "+ this.additionalInfo + " ; \n";
        return info;
    }
}
