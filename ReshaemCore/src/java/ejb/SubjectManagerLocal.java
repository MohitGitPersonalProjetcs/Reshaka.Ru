package ejb;

import ejb.util.ReshakaSortOrder;
import entity.Subject;
import entity.User;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface SubjectManagerLocal {

    public Map<Long, String> getAllSubjectsMap();

    public void addSubjectToDatabase(String name);

    List<Subject> getSubjectListByStringList(List<String> stringList);

    public List<Subject> getAllSubjects();

    public Subject getSubjectBySubjectMap();

    public Subject getSubjectById(Long subjectId);

    public Subject getSubjectBySubjectName(String subName);

    public List<Long> getUserIdListBySubjectId(Long subjectId);

    public List<User> getUsersListBySubjectId(Long subjectId);

    public List<Subject> getFilteredSubjects(Map<String, String> filters, int first, int pageSize, String sortField, ReshakaSortOrder sortOrder);

    public int getFilteredSubjectsCount(Map<String, String> filters, String sortField, ReshakaSortOrder sortOrder);
}
