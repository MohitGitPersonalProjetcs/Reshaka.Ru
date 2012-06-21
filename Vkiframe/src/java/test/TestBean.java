/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class TestBean implements Serializable{

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

    
    
    private List<Entity> generateList() {
        List<Entity> l = new ArrayList();
        for (int i = 0; i < 10; i++) {
            System.out.println("adding new entity");
            l.add(new Entity("sabir", "orsk", "student", 20));
        }
        System.out.println("list = " + l);
        return l;
    }
}
