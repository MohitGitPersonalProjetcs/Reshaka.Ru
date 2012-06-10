package web;

import javax.faces.bean.ManagedBean;

/**
 * The entity of academic subjects (maths, physics, etc)
 *
 * @author ASUS
 */
@ManagedBean
public class Subject {

    private String subjectName;

    private String subjectId;

    public Subject(String subjectName, String subjectId) {
        this.subjectName = subjectName;
        this.subjectId = subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
