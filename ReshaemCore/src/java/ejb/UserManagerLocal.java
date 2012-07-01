package ejb;

import ejb.util.ReshakaSortOrder;
import ejb.util.ReshakaUploadedFile;
import entity.*;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author rogvold
 */
@Local
public interface UserManagerLocal {

    public String getStringRoleByUserGroup(int userGroup);
    
    public boolean userExists(String login);

    public User logIn(String login, String password);

    public User register(User user);
    
    public User logInByEmail(String email, String password);
    
    public User logInByEmailMD5(String email, String password);
    
    public User logInMD5(String login, String password);
    
    public User registerByEmail(User user);
    
    public User openIdRegistration(Map<String,String> map);
    
    public User openIdAuthorisation(Map<String,String> map);

    public List<User> getAdministrators();

    public void addAttachmentToUser(Long userId, Long attachmentId);

    public User updateProfile(User user,UserSettings settings);

    public User updatePassword(Long id, String oldPassword, String password);

    public Attachment getUserAvatar(Long avatarId);

    public User updateAvatar(Long id, ReshakaUploadedFile uploadedAvatar);

    public User getUserById(Long userId);
    
    public List<User> getFilteredUsers(Map<String, String> filters, int first, int pageSize , String sortField, ReshakaSortOrder sortOrder);
    
    public void setUserStatus(Long userId, int status);

    void updateUserSettings(long userId, UserSettings userSettings);
    
    public UserSettings getUserSettings(Long userId);
    
    public User makeOpenIdBundle(Long userId, Map<String,String>  map);
    
    public OpenId getOpenIdByUserId(Long userId);
    
    public double getBalanceByUserId(Long userId);
    
    public boolean canChangeNickname(Long userId);
    
    public List<User> getAllUsers();
    
    public List<User> getAllReshakas();
    
    public List<User> getAllRegisteredUsers();
    
    public void deleteUser(Long userId);
    
    public void makeReshakaFromUser(Long userId);
    
    public int getGroupById(Long userId);
    
    public void banUser(Long userId);
    
    public void banReshaka(Long userId);
    
    public void registerReshaka(String email);
    
    public void recoverPassword(String email);
    
    public List<Subject> getSubjectsByUserId(Long userId);
    
    public UserSettings getUserSettingsByUserId(Long userId);
    
    public boolean userExistsById(Long userId);
}
