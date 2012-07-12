/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class TestBean implements Serializable {

    private List<Entity> list;

    public TestBean() {
        list = generateList();
    }

    public List<Entity> getList() {
        return list;
    }

    public void setList(List<Entity> list) {
        this.list = list;
    }

    public void testAction() {
//        System.out.println("testing remote command");
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        String name1 = (String) map.get("name1");
//        String name2 = (String) map.get("name2");
        System.out.println("name1 = " + name1);
    }

    private List<Entity> generateList() {
        List<Entity> l = new ArrayList();
        for (int i = 0; i < 7; i++) {
            System.out.println("adding new entity");
            l.add(new Entity("sabir", "orsk", "student", 20));
        }
        System.out.println("list = " + l);
        return l;
    }
}
