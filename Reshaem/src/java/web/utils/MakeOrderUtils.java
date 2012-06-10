/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.utils;

import entity.Subject;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author rogvold
 */
public class MakeOrderUtils {


    private static List<Subject> subjects;
    private static Map<Long,String> dictionary;

    public static ArrayList<String> getStringList()
    {
        ArrayList<String> list = new ArrayList<>(dictionary.values());
        return list;
    }
    
    public static Long getIdBySubjectName(String name)
    {
        System.out.println("try to get subject id by name " + name);
     for (Entry<Long, String> entry : dictionary.entrySet()) {
         if (name.equals(entry.getValue())) {
             return entry.getKey();
         }
     }
        return 0L;
    }
    
    public static Subject getSubjectByName(String name)
    {
        if (name == null) return null;
        Subject subject = new Subject(name);
        subject.setId(getIdBySubjectName(name));
//        subject.setSubjectName(name);
        return subject;
    }
    
    public static String getSubjectNameById(Long id)
    {
        return dictionary.get(id);
    }
     
    public MakeOrderUtils() {
        subjects = new ArrayList<>();
    }

    public static void setDictionary(Map<Long,String> map) {
      dictionary = new TreeMap<>(map);
    }
}
