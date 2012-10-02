package ejb;

import ejb.util.ReshakaSortOrder;
import ejb.util.ReshakaUploadedFile;
import ejb.util.URLUtils;
import entity.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
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
public class UserManager implements UserManagerLocal {

    private static Logger log = Logger.getLogger(AttachmentManager.class.getName());
    @PersistenceContext(unitName = "ReshaemCorePU")
    EntityManager em;
    @EJB
    AttachmentManagerLocal am;
    @EJB
    OrderManagerLocal ordMan;
    @EJB
    MailManagerLocal mailMan;

    @Override
    public boolean userExists(String email) {
        Query q = em.createNamedQuery("findByEmail").setParameter("userEmail", email);
        List<User> users = q.getResultList();
        if (!users.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User logIn(String login, String password) {
        Query q = em.createNamedQuery("findByLoginAndPassword").setParameter("userLogin", login);
        q.setParameter("userPassword", password);
        List<User> users = q.getResultList();
        System.out.println("named query returned " + users.size() + " users");
        if (users.size() == 1) {
            User user = users.get(0);
            Date date = new Date();
            user.setLastActivityDate(date);
            em.merge(user);
            return user;
        }
        return null;
    }

    @Override
    public User register(User user) {
        System.out.println("trying to reg user " + user);

        if (log.isTraceEnabled()) {
            log.trace(">> register(): user=" + user);
        }
        List<User> users = em.createNamedQuery("findByLogin").setParameter("userLogin", user.getLogin()).getResultList();
        if (users.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("register(): no users with such login");
            }
            Date date = new Date();
            user.setRegistrationDate(date);
//            System.out.println("user subjects:" + user.getSubjects());
            user.setSettings(new UserSettings());
            user.setOpenId(new OpenId());

            user = em.merge(user);// try to use merge instead of persist
            return user;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("<< register(): such user already registered!");
            }
            return null;
        }
    }

    @Override
    public User openIdRegistration(Map<String, String> map) {
        OpenId openId = new OpenId();
        String key = "";
        for (String s : map.keySet()) {
            key = s;
        }
        String value = map.get(key);

        if (log.isTraceEnabled()) {
            log.trace("openIdRegistration(): registering using " + map);
        }
        openId.setParameter(map);

        User user = new User();
        user.setOpenId(openId);
        user.setUserGroup(2);
        user.setPassword("");
        user = em.merge(user);
        user.setLogin("user" + user.getId().toString());
        user.setEmail(key + value);
        user.setRegistrationDate(new Date());

        user = em.merge(user);

        return user;
    }

    @Override
    public User openIdAuthorisation(Map<String, String> map) {
        String key = "";
        try {

            for (String s : map.keySet()) {
                key = s;
            }
            String value = map.get(key);
            String jpqlString = "select u from User u where u.openId." + key + "='" + value + "'";
            if (log.isTraceEnabled()) {
                log.trace("openIdAuthorisation(): jpql query: " + jpqlString);
            }
            Query q = em.createQuery(jpqlString);
            List<User> users = q.getResultList();

            if (log.isTraceEnabled()) {
                log.trace("openIdAuthorisation(): ResultList: " + users);
            }

            if ((users != null) && (users.isEmpty())) {
                users = null;
            }
            if (users == null) {
                if (log.isTraceEnabled()) {
                    log.trace("openIdAuthorisation(): no users with such login");
                }
                return openIdRegistration(map);
            } else {
//                User user = em.find(user, value)
                User user = users.get(0);
                user.setLastActivityDate(new Date());
                user = em.merge(user);
                return user;
            }
        } catch (Exception exc) {
            return null;
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<User> getAdministrators() {
        List<User> users = em.createNamedQuery("findAdmins").getResultList();
        return users;
    }

    @Override
    public void addAttachmentToUser(Long userId, Long attachmentId) {
        User user = em.find(User.class, userId);
        try {
            Attachment att = em.find(Attachment.class, attachmentId);
//            Set<Attachment> aset = new HashSet(user.getAttachments());
//            aset.add(att);
//            user.setAttachments(aset);
            //user.getAttachments().add(att);
            att.getUser().add(user);
            //em.merge(user);
            em.merge(att);
        } catch (Exception exc) {
            if (log.isTraceEnabled()) {
                log.trace("can not attach solution file to (): ");
            }
        }
    }

    @Override
    public User updateProfile(User user, UserSettings settings) {
        if (log.isTraceEnabled()) {
            log.trace(">> updateProfile(): user=" + user);
        }
        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("updateProfile(): user is null!");
            }
            return null;
        }

        User u = em.find(User.class, user.getId());
        if (u == null) {
            if (log.isDebugEnabled()) {
                log.debug("updateProfile(): cannot find such user in the database");
            }
            return null;
        }

        u.setId(user.getId());
        u.setLogin(user.getLogin());
        u.setPassword(user.getPassword());
        u.setEmail(user.getEmail());
        u.setPhone(user.getPhone());
        u.setIcq(user.getIcq());
        u.setSkype(user.getSkype());
        u.setAdditionalContacts(user.getAdditionalContacts());
        u.setAdditionalInformation(user.getAdditionalInformation());
        u.setSubjects(user.getSubjects());

        u.setEducation(user.getEducation());
        u.setCity(user.getCity());
        u.setAge(user.getAge());


        System.out.println("updateProfile: settings.newStatus/newMessage = " + settings.isNewStatus() + "/" + settings.isNewMessage());

        settings = em.merge(settings);


        u.setSettings(settings);

        u = em.merge(u);

        //TODO(Sabir) 
        // когда мы тут ставим настройки (setSettings) они успешно добавляются в таблицу.
        // однако же когда из SubjectManager пытаемся достать всех пользователй, связанных 
        // с эти предметом, то у нас ничего не получается - как будто в БД ничего не рпоизошло.
        //  если передеплоить проект то все изменения как бы вступают в силу.
        // мы что-то не понимаем в транзакциях.

        // P.S. мы не шарим JPA


//        em.getTransaction().commit();

        if (log.isTraceEnabled()) {
            log.trace("<< updateProfile(): user data have been updated");
        }
        return u;
    }

    @Override
    public User updatePassword(Long id, String oldPassword, String password) {
        if (log.isTraceEnabled()) {
            log.trace(">> updatePassword(): id=" + id);
        }
        if (id == null) {
            return null;
        }
        User u = em.find(User.class, id);
        if (u == null) {
            if (log.isDebugEnabled()) {
                log.debug("updatePasswrod(): there isn't user with id = " + id);
            }
            return null;
        }
        if (u.getPassword().equals(oldPassword)) {
            u.setPassword(password);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("updatePassword(): incorrect old password");
            }
            return null;
        }
        u = em.merge(u);

        if (log.isTraceEnabled()) {
            log.trace("updatePassword(): password has been updated");
        }
        return u;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public Attachment getUserAvatar(Long avatarId) {
        if (log.isTraceEnabled()) {
            log.trace(">> getUserAvatar(): avatarId=" + avatarId);
        }
        Attachment avatar = am.getUploadedFile(null, avatarId);
        if (log.isTraceEnabled()) {
            log.trace("<< getUserAvatar(): " + avatar);
        }
        return avatar;
    }

    @Override
    public User updateAvatar(Long id, ReshakaUploadedFile uploadedAvatar) {
        if (log.isTraceEnabled()) {
            log.trace(">> updateAvatar(): userId=" + id);
        }
        if (uploadedAvatar == null) {
            if (log.isTraceEnabled()) {
                log.trace("updateAvatar(): uploadedAvatar=null >> user's avatar will be reset");
            }
            try {
                User u = em.getReference(User.class, id);
                u.setAvatarId(null);
                return u;
            } catch (Exception ex) {
                if (log.isDebugEnabled()) {
                    log.debug("updateAvatar(): failed to reset avatar", ex);
                }
            }
            return null;
        }
//        File tmp = null;
//        try {
//            tmp = File.createTempFile("tmp_avatar_", uploadedAvatar.getFileName());
//        } catch (IOException ex) {
//            if (log.isDebugEnabled()) {
//                log.debug("updateAvatar(): failed to create temporaty file", ex);
//            }
//            return null;
//        }
//
//        try (FileImageOutputStream imageOut = new FileImageOutputStream(tmp)) {
//            imageOut.write(uploadedAvatar.getContents());
//        } catch (IOException ex) {
//            if (log.isDebugEnabled()) {
//                log.debug("updateAvatar(): failed to write to temporary file", ex);
//            }
//            return null;
//        }
//        FileItem item = new DiskFileItemFactory((int)tmp.length(), tmp)
//                .createItem("avatar", uploadedAvatar.getContentType(), false, uploadedAvatar.getFileName());
//        
//        UploadedFile f = new DefaultUploadedFile(item);
        List<ReshakaUploadedFile> l = new LinkedList<>();
        l.add(uploadedAvatar);

        Attachment a = am.uploadFiles(em.find(User.class, id), l, "avatar");
        if (log.isTraceEnabled()) {
            log.trace("updateAvatar(): avatar updated");
        }
        //FileUtils.deleteFile(tmp.getAbsolutePath());

        try {
            User u = em.find(User.class, id);
            u.setAvatarId(a.getId());
            u = em.merge(u);
            return u;
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("updateAvatar(): failed to set avatarId", ex);
            }
        }

        return null;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public User getUserById(Long userId) {
        User user;
        if (userId == null) {
            return null;
        }
        user = em.find(User.class, userId);
        return user;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public List<User> getFilteredUsers(Map<String, String> filters, int first, int pageSize, String sortField, ReshakaSortOrder sortOrder) {
        String jpqlString = "select u from User u where 'plug' = 'plug' ";
        String filterProperty, filterValue;
        try {
            //filtering
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    filterProperty = it.next();
                    filterValue = filters.get(filterProperty);
                    jpqlString += " and u." + filterProperty + "=" + filterValue;
                }
            }
            // ranging
            if (sortField != null) {
                jpqlString += " order by u." + sortField + " ";
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
        q.setMaxResults(first + pageSize);
        List<User> users = q.getResultList();
        System.out.println("EJB: found users: " + users.toString());
        if (first > users.size() - 1) {
            return null;
        }

        if (first + pageSize > users.size()) {
            return users.subList(first, users.size());
        } else {
            return users.subList(first, first + pageSize);
        }
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public String getStringRoleByUserGroup(int userGroup) {
        switch (userGroup) {
            case 1:
                return "Admin";
            case 2:
                return "User";
            case 3:
                return "Reshaka";
        }
        return "";
    }

    @Override
    public void setUserStatus(Long userId, int status) {
        try {
            User user = em.find(User.class, userId);
            user.setUserGroup(status);
            em.merge(user);
        } catch (Exception exc) {
        }
    }

//    @Override
//    public void addComment(Long userId, Long authorId, String text) {
//        System.out.println("try to add comment for " + userId);
//        User user = em.find(User.class, userId);
//        
//        Comment comment = new Comment();
//        
//        comment.setAuthorId(authorId);
//        comment.setText(text);
//        comment.setOwner(user);
//        comment.setCreationDate(new Date());
//        
//        
//        user.getComments().add(comment);
////        Set<Comment> comms = user.getComments();
////        comms.add(comment);
////        user.setComments(comms);
////        em.merge(user);
//
//
////        em.merge(comment);
//
//        em.merge(user);
//        System.out.println("add comment in ejb occured");
//
////        em.persist(comment);
//        // em.getTransaction().commit();
//    }
//    @Override
//    public List<Comment> getComments(Long userId) {
//        try {
//            System.out.println("try to find comments of user id = " + userId);
//            
//            User user = em.find(User.class, userId);
//            System.out.println("user.getComments = " + user.getComments());
//            Set<Comment> comms = user.getComments();
//            if (comms == null) {
//                return null;
//            }
//            ArrayList list = new ArrayList(comms);
//            System.out.println("comments : " + comms);
//            if (log.isTraceEnabled()) {
//                log.trace("getComments(): comments of user ID = " + userId + " ; comments = " + list);
//            }
//            return list;
//        } catch (Exception exc) {
//        }
//        return null;
//    }
    @Override
    public void updateUserSettings(long userId, UserSettings userSettings) {
        User user = em.find(User.class, userId);
        System.out.println("settings to update: " + userSettings);
        user.setSettings(userSettings);
        em.merge(user);
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public UserSettings getUserSettings(Long userId) {
        User user = em.find(User.class, userId);
        return user.getSettings();
    }

    @Override
    public User makeOpenIdBundle(Long userId, Map<String, String> map) {
        //--
        String key = "";
        for (String s : map.keySet()) {
            key = s;
        }
        String value = map.get(key);
        String jpqlString = "select u from User u where u.openId." + key + "='" + value + "'";
        if (log.isTraceEnabled()) {
            log.trace("openIdAuthorisation(): jpql query: " + jpqlString);
        }
        Query q = em.createQuery(jpqlString);
        List<User> users = q.getResultList();

        if (log.isTraceEnabled()) {
            log.trace("openIdAuthorisation(): ResultList: " + users);
        }

        if ((users != null) && (users.isEmpty())) {
            users = null;
        }
        if (users != null) {
            return null;
        }
        //--
        User user = em.find(User.class, userId);
        OpenId openId = user.getOpenId();
        openId.setParameter(map);
        user.setOpenId(openId);
        user = em.merge(user);
        return user;
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public OpenId getOpenIdByUserId(Long userId) {
        User user = em.find(User.class, userId);
        System.out.println("getOpenIdByUserId(): user =  " + user);

        return user.getOpenId();
    }

    @Override
    @javax.ejb.TransactionAttribute(javax.ejb.TransactionAttributeType.SUPPORTS)
    public double getBalanceByUserId(Long userId) {
        double d = 0.0;
        DecimalFormat df = new DecimalFormat("#.##");
        try {
            User user = em.find(User.class, userId);
            d = user.getCurrentBalance();
            return Double.parseDouble(df.format(d));
        } catch (Exception exc) {
        }
        return 0;
    }

    @Override
    public User logInByEmail(String email, String password) {
        Query q = em.createNamedQuery("findByEmailAndPassword").setParameter("userEmail", email);
        q.setParameter("userPassword", password);
        List<User> users = q.getResultList();
        System.out.println("named query returned " + users.size() + " users");
        if (users.size() == 1) {
            User user = users.get(0);
            Date date = new Date();
            user.setLastActivityDate(date);
            em.merge(user);
            return user;
        }
        return null;
    }

    @Override
    public User registerByEmail(User user) {
        System.out.println("trying to reg user " + user);
        Pattern p = Pattern.compile("[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]");
        //Match the given string with the pattern
        Matcher m = p.matcher(user.getEmail());
//check whether match is found 
        if (m.matches() == false) {
            return null;
        }


        if (log.isTraceEnabled()) {
            log.trace(">> register(): user=" + user);
        }
        List<User> users = em.createNamedQuery("findByEmail").setParameter("userEmail", user.getEmail()).getResultList();
        if (users.isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("register(): no users with such email");
            }
            Date date = new Date();
            user.setRegistrationDate(date);
//            System.out.println("user subjects:" + user.getSubjects());
            user.setSettings(new UserSettings());
            user.setOpenId(new OpenId());

            user = em.merge(user);// try to use merge instead of persist
            user.setLogin("user" + user.getId());
            user = em.merge(user);
            String text = "Здравствуйте, теперь вы зарегистрированы на сайте Reshaka.Ru !"
                    + "\n\nЛогин: " + user.getEmail() + "\n"
                    + "Пароль: " + user.getPassword() + ""
                    + "\n\n\n C уважением, администрация Reshaka.Ru";
            mailMan.sendMail(user.getEmail(), "Регистрация на сайте Reshaka.Ru", text);

            return user;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("<< register(): such user already registered!");
            }
            return null;
        }
    }

    @Override
    public User logInByEmailMD5(String email, String password) {
        Query q = em.createNamedQuery("findByEmail").setParameter("userEmail", email);
        List<User> users = q.getResultList();
        System.out.println("named query returned " + users.size() + " users");
        if (users.size() == 1) {
            User user = users.get(0);
            if (user != null && getMD5(user.getPassword()).equalsIgnoreCase(password)) {
                Date date = new Date();
                user.setLastActivityDate(date);
                em.merge(user);
                return user;
            }
        }
        return null;
    }

    @Override
    public User logInMD5(String login, String password) {
        Query q = em.createNamedQuery("findByLogin").setParameter("userLogin", login);
        List<User> users = q.getResultList();
        System.out.println("named query returned " + users.size() + " users");
        if (users.size() == 1) {
            User user = users.get(0);
            if (user != null && getMD5(user.getPassword()).equalsIgnoreCase(password)) {
                Date date = new Date();
                user.setLastActivityDate(date);
                em.merge(user);
                return user;
            }
        }
        return null;
    }

    public String getMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            byte byteData[] = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception exc) {
        }

        return "";
    }

    @Override
    public boolean canChangeNickname(Long userId) {
        User user = em.find(User.class, userId);
        try {
            String s = user.getLogin();
            if (log.isTraceEnabled()) {
                log.trace("UserManager: canChangeNickname() login = " + s);
            }
            //String s2 = "user" + Long.toString(userId);
            //String s3 = "reshaka" + Long.toString(userId);
            if ((s.indexOf("user") >= 0) || (s.indexOf("reshaka") >= 0)) {
                return true;
            }
        } catch (Exception exc) {
            if (log.isTraceEnabled()) {
                log.trace("canChangeNickname(): exception =  " + exc);
            }
        }
        return false;
    }

    @Override
    public List<User> getAllUsers() {
        Map<String, String> map = new HashMap();
        map.put("userGroup", "3");
        return getFilteredUsers(map, 0, 1000000, null, ReshakaSortOrder.UNSORTED);
    }

    @Override
    public List<User> getAllReshakas() {
        Map<String, String> map = new HashMap();
        map.put("userGroup", "3");
        return getFilteredUsers(map, 0, 1000000, null, ReshakaSortOrder.UNSORTED);
    }

    @Override
    public List<User> getAllRegisteredUsers() {
        return getFilteredUsers(null, 0, 1000000, null, ReshakaSortOrder.UNSORTED);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = em.find(User.class, userId);
        em.remove(user);
    }

    @Override
    public void makeReshakaFromUser(Long userId) {
        User user = em.find(User.class, userId);
        if (user.getUserGroup() == 2) {
            //System.out.println("changing user role...");
            user.setUserGroup(3);
            em.merge(user);
        }
    }

    @Override
    public int getGroupById(Long userId) {
        try {
            User user = em.find(User.class, userId);
            return user.getUserGroup();
        } catch (Exception e) {
        }
        return 0;
    }

    @Override
    public void banUser(Long userId) {
        User user = em.find(User.class, userId);
        user.setUserGroup(4);
        em.merge(user);
    }

    @Override
    public void banReshaka(Long userId) {
        User user = em.find(User.class, userId);
        user.setUserGroup(5);
        em.merge(user);
    }

    private String generateRandomString(int length) {
        String str = "G12HIJdefgPQRSTUVWXYZabc56hijklmnopqAB78CDEF0KLMNO3rstu4vwxyz9";
        String ar = "";
        Random r = new Random();
        for (int i = 1; i <= length; i++) {
            ar = ar + str.charAt(r.nextInt(62));
        }
        return ar;
    }

    @Override
    public void registerReshaka(String email) {
        if (userExists(email) == true) {
            if (log.isTraceEnabled()) {
                log.trace("registerReshaka(): reshaka with such email already exists ");
            }
            return;
        }

        User user = new User();
        user.setUserGroup(3);
        user.setEmail(email);
        user.setSkype("-");
        user.setPhone("-");
        user.setIcq("-");
        user.setSettings(new UserSettings());
        user.setOpenId(new OpenId());
        user.setRegistrationDate(new Date());
        String password = generateRandomString(6);
        user.setPassword(password);

        user = em.merge(user);
        user.setLogin("reshaka" + Long.toString(user.getId()));

        String text = "Здравствуйте, теперь вы зарегистрированы на сайте Reshaka.Ru в качестве решающего!"
                + "\n\nЛогин: " + user.getEmail() + "\n"
                + "Пароль: " + user.getPassword() + ""
                + "\n\nВы можете поменять пароль в разделе \"Мой профиль\" "
                + "\n\n\n C уважением, администрация Reshaka.Ru";
        mailMan.sendMail(email, "Регистрация на сайте Reshaka.Ru", text);
        if (log.isTraceEnabled()) {
            log.trace("registerReshaka(): registered reshaka :  " + user);
        }

    }

    @Override
    public void recoverPassword(String email) {
        String newPass = generateRandomString(6);
        Query q = em.createNamedQuery("findByEmail").setParameter("userEmail", email);
        List<User> list = q.getResultList();
        if (list == null) {
            return;
        }
        User user = list.get(0);
        user.setPassword(newPass);
        em.merge(user);
        String text = "Ваш новый пароль: " + newPass + " .\n"
                + "С уважением, администрация "+URLUtils.createLink(URLUtils.getReshakaURL(), "_blank", "Reshaka.Ru")+".";

        mailMan.sendMail(email, "Восстановление пароля", text);
    }

    @Override
    public List<Subject> getSubjectsByUserId(Long userId) {
        User user = em.find(User.class, userId);
        return user.getSubjects();
    }

    @Override
    public UserSettings getUserSettingsByUserId(Long userId) {
        User user = em.find(User.class, userId);
        return user.getSettings();
    }

    @Override
    public boolean userExistsById(Long userId) {
        User user;
        if (userId == null) {
            return false;
        }
        try {
            user = em.find(User.class, userId);
            if (user != null) {
                return true;
            }
        } catch (Exception exc) {
            return false;
        }
        return false;
    }
}
