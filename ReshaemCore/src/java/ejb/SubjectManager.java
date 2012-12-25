package ejb;

import com.sun.xml.ws.api.tx.at.Transactional;
import ejb.util.ReshakaSortOrder;
import entity.Subject;
import entity.User;
import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;

/**
 *
 * @author rogvold
 */
@Stateless
public class SubjectManager implements SubjectManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Map<Long, String> getAllSubjectsMap() {
        Query q = em.createNamedQuery("findMySubjects");

        List<Subject> subjects = q.getResultList();
        Map<Long, String> map = new HashMap<>();
        for (Subject s : subjects) {
            map.put(s.getId(), s.getSubjectName());
        }
        return map;
    }

    @Override
    public void addSubjectToDatabase(String name) {
        Subject sub = new Subject(name);
        em.persist(sub);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Subject> getSubjectListByStringList(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        Query q;
        Subject subject;
        List<Subject> list = new ArrayList();
        for (String sub : stringList) {
            q = em.createNamedQuery("getSubjectByName").setParameter("subjectName", sub);
            List<Subject> sList = q.getResultList();
            if (sList != null) {
                list.add(sList.get(0));
            }
        }
        return list;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Subject> getAllSubjects(boolean withNotSelectedSubject) {
        Query q = em.createNamedQuery("getAllSubjects");
        List<Subject> list = q.getResultList();
        Subject nss = null;
        int a = -1;
        for (int i = 0; i < list.size(); i++) {
            if (Subject.NOT_SELECTED_SUBJECT_NAME.equals(list.get(i).getSubjectName())) {
                nss = list.get(i);
                a = i;
            }
        }
        if (nss != null) {
            list.remove(a);
            if (withNotSelectedSubject) {
                list.add(0, nss);
            }
        }
        return list;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Subject getSubjectBySubjectMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Subject getSubjectById(Long subjectId) {
        return em.find(Subject.class, subjectId);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Subject getSubjectBySubjectName(String subName) {
        Query q = em.createNamedQuery("getSubjectByName").setParameter("subjectName", subName);
        List<Subject> list = q.getResultList();
        try {
            return list.get(0);
        } catch (Exception exc) {
        }
        return null;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Long> getUserIdListBySubjectId(Long subjectId) {
        if (log.isTraceEnabled()) {
            log.trace("getUserIdListBySubjectId(Long subjectId): subjectId =  " + subjectId);
        }
        Subject subject = em.find(Subject.class, subjectId);
        if (subject == null) {
            return null;
        }
        List<User> list = subject.getUsers();
        List<Long> l = new ArrayList();
        for (User u : list) {
            l.add(u.getId());
        }
        return l;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<User> getUsersListBySubjectId(Long subjectId) {
        if (log.isTraceEnabled()) {
            log.trace("getUserIdListBySubjectId(Long subjectId): subjectId =  " + subjectId);
        }
        Subject subject = em.find(Subject.class, subjectId);
        if (subject == null) {
            return null;
        }
        List<User> list = subject.getUsers();
        return list;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<Subject> getFilteredSubjects(Map<String, String> filters, int first, int pageSize, String sortField, ReshakaSortOrder sortOrder) {
        String jpqlString = "select s from Subject s where 'plug' = 'plug' ";
        String filterProperty, filterValue;
        System.out.println("filters = " + filters);
        try {
            //filtering
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    filterProperty = it.next();
                    filterValue = filters.get(filterProperty);
                    jpqlString += " and s." + filterProperty + " = " + filterValue;
                }
            }
            // ranging
            if (sortField != null) {
                jpqlString += " order by s." + sortField + " ";
                if (sortOrder != null) {
                    if (sortOrder == ReshakaSortOrder.ASCENDING) {
                        jpqlString += "ASC";
                    }
                    if (sortOrder == ReshakaSortOrder.DESCENDING) {
                        jpqlString += "DESC";
                    }
                    if (sortOrder == ReshakaSortOrder.UNSORTED) {
                        jpqlString += "ASC";
                    }
                }
            }
        } catch (Exception exc) {
            System.out.println(exc.toString());
        } //hardcode

        //setting bounds
        System.out.println("jpql = " + jpqlString);
        Query q = em.createQuery(jpqlString);
        q.setMaxResults(first + pageSize + 1);
        List<Subject> data = q.getResultList();

        // System.out.println("orders = " + orders);
        int dataSize = (data == null) ? 0 : data.size();

//        if (first > subjects.size() - 1) {
//            return null;
//        }
//
//        if (first + pageSize > subjects.size()) {
//            return subjects.subList(first, subjects.size());
//        } else {
//            return subjects.subList(first, first + pageSize);
//        }

        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        } else {
            return data;
        }


    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public int getFilteredSubjectsCount(Map<String, String> filters, String sortField, ReshakaSortOrder sortOrder) {
        String jpqlString = "select count(s) from Subject s where 'plug' = 'plug' ";
        String filterProperty, filterValue;
        System.out.println("filters = " + filters);
        try {
            //filtering
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    filterProperty = it.next();
                    filterValue = filters.get(filterProperty);
                    jpqlString += " and s." + filterProperty + " = " + filterValue;
                }
            }
            // ranging
            if (sortField != null) {
                jpqlString += " order by s." + sortField + " ";
                if (sortOrder != null) {
                    if (sortOrder == ReshakaSortOrder.ASCENDING) {
                        jpqlString += "ASC";
                    }
                    if (sortOrder == ReshakaSortOrder.DESCENDING) {
                        jpqlString += "DESC";
                    }
                    if (sortOrder == ReshakaSortOrder.UNSORTED) {
                        jpqlString += "ASC";
                    }
                }
            }
        } catch (Exception exc) {
            System.out.println(exc.toString());
        } //hardcode

        Query q = em.createQuery(jpqlString);
        Object count = q.getSingleResult();
        System.out.println("count = " + count);
        return Integer.parseInt(count.toString()); // indian code

    }
}
