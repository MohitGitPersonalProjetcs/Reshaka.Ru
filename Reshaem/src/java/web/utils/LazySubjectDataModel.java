/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.utils;

import entity.Subject;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author rogvold
 */
public class LazySubjectDataModel extends LazyDataModel<Subject> {
    
    private List<Subject> datasource;

    @Override
    public List<Subject> load(int i, int i1, String string, SortOrder so, Map<String, String> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
