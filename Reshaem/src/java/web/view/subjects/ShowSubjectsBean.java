/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.view.subjects;

import ejb.SubjectManagerLocal;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import entity.Subject;
import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author rogvold
 */
@ManagedBean
@RequestScoped
public class ShowSubjectsBean {

    @EJB
    SubjectManagerLocal subjMan;

    private LazySubjectDataModel<Subject> lazyModel;
    private int param;
    
    
    
    public ShowSubjectsBean() {
    }

    @PostConstruct
    private void init() {
        lazyModel = new LazySubjectDataModel(subjMan);
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        lazyModel.setParam(param);
    }

    
    
    public List<Subject> getAllSubjects() {
        return subjMan.getAllSubjects(true);
    }

    public LazyDataModel<Subject> getLazyModel() {
        setParam(2);
        return lazyModel;
    }
    
    

    public List<Long> recipients(Long subjectId) {
        return subjMan.getUserIdListBySubjectId(subjectId);
    }
    
    
}
