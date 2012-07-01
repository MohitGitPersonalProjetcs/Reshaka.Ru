package web.view.subjects;

import ejb.SubjectManagerLocal;
import entity.Subject;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import web.utils.Tools;

/**
 *
 * @author rogvold
 */
public class LazySubjectDataModel<T extends Object> extends LazyDataModel<Subject> {

    private SubjectManagerLocal subjMan;

    private int param;

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public LazySubjectDataModel(SubjectManagerLocal subjManager) {
        this.subjMan = subjManager;
//        this.setPageSize(5);
    }

    @Override
    public List<Subject> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        System.out.println("load occured");
        System.out.println("param = " + this.param);
        List<Subject> data = subjMan.getFilteredSubjects(filters, first, pageSize, sortField, Tools.convertSortOrder(sortOrder));
        int dataSize = subjMan.getFilteredSubjectsCount(filters, sortField, Tools.convertSortOrder(sortOrder));
        this.setRowCount(dataSize);
        return data;
    }
    

}
