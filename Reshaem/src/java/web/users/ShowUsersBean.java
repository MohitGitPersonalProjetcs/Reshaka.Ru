package web.users;

import ejb.UserManagerLocal;
import entity.User;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ShowUsersBean {

    @EJB
    UserManagerLocal userMan;

    private List<User> usersList;

    private static final SelectItem[] userGroupFilterOptions = createFilterOptions();
    private static final SelectItem[] userOnlineStatusOptions = createStatusFilterOptions();

//    public List<User> getAllUsers()
//    {
//        return userMan.getFilteredUsers(null, 1, 10000000, null, null);
//    }
    @PostConstruct
    private void init() {
        setUsersList(userMan.getAllRegisteredUsers());
    }

    public void establishAllReshakas() {
        setUsersList(userMan.getAllReshakas());
    }

    public void establishAllUsers() {
        setUsersList(userMan.getAllUsers());
    }

    public void establishAllRegisteredUsers() {
        setUsersList(userMan.getAllRegisteredUsers());
    }

    private static SelectItem[] createFilterOptions() {
        SelectItem[] options2 = new SelectItem[4];

        options2[0] = new SelectItem("", "Выберите");
        options2[1] = new SelectItem(2, "Пользователь");
        options2[2] = new SelectItem(3, "Решака");
        options2[3] = new SelectItem(1, "Администратор");

        return options2;
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public SelectItem[] getUserGroupFilterOptions() {
        return userGroupFilterOptions;
    }
    
    public SelectItem[] getUserOnlineStatusFilterOptions() {
        return userOnlineStatusOptions;
    }

    private static SelectItem[] createStatusFilterOptions() {
        SelectItem[] options2 = new SelectItem[3];

        options2[0] = new SelectItem("", "Выберите");
        options2[1] = new SelectItem("true", "Online");
        options2[2] = new SelectItem("false", "Offline");

        return options2;
    }
    
    
}
