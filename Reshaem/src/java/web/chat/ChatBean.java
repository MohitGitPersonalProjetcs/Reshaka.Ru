package web.chat;

import data.SimpleUser;
import ejb.AttachmentManagerLocal;
import ejb.ConfigurationManagerLocal;
import ejb.MessageManagerLocal;
import ejb.UserManagerLocal;
import ejb.util.StringUtils;
import entity.Attachment;
import entity.Message;
import entity.User;
import java.io.Serializable;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONArray;
import web.FileUploadController;
import web.utils.SessionListener;

/**
 * Backing bean for chat
 * <p/>
 * @author Danon <Anton Danshin>
 */
@ManagedBean
@ViewScoped
public class ChatBean implements Serializable {

    private static final Logger log = Logger.getLogger(ChatBean.class);

    public static final String DIALOGS_MODE = "DIALOGS";

    public static final String MESSAGES_MODE = "MESSAGES";

    @EJB
    private MessageManagerLocal mm;

    @EJB
    private ConfigurationManagerLocal cm;

    @EJB
    private UserManagerLocal um;

    @EJB
    private AttachmentManagerLocal am;

    private String mode = DIALOGS_MODE;

    private User me;

    private Long friendId;

    private String text;

    private Date lastUpdate = null;

    private SimpleUser selectedUser = null;

    private FileUploadController fileUploadController;

    private String friendStr;

    private SimpleUser friend;

    private boolean adminMode;

    private boolean displayUploadControl;

    private Long period = 1L;

    private int unreadMessagesNumber;

    private List<ChatDialog> dialogs;

    private List<SimpleUser> peers;

    /**
     * Creates a new instance of ChatBean
     */
    public ChatBean() {
        fileUploadController = new FileUploadController();

        FacesContext fctx = FacesContext.getCurrentInstance();
        User u = (User) SessionListener.getSessionAttribute("user", true);
        if (u == null) {
            u = new User();
            u.setLogin("Guest");
            u.setId(0L);
        }
        me = u;

        Map<String, String> params = fctx.getExternalContext().getRequestParameterMap();
        friendStr = StringUtils.getValidString(params.get("friend"));

        if (log.isTraceEnabled()) {
            log.trace("ChatBean(): friendStr = " + friendStr);
        }

        this.period = (long) StringUtils.getValidInt(params.get("period"));
        if (period == 0) {
            period = 30L; // default period - 1 month
        }
        lastUpdate = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * this.period);
        dialogs = new ArrayList<>();
    }

    @PostConstruct
    private void init() {
        SimpleUserConverter.mm = this.mm;
        if (friendStr.equalsIgnoreCase("support")) {
            friendId = cm.getAdminId();
        } else if (friendStr.equalsIgnoreCase("_stream")) {
            if (me.getUserGroup() != User.ADMIN) {
                friendId = null;
                friend = new SimpleUser();
            } else {
                adminMode = true;
                friendId = null;
                friend = new SimpleUser();
            }
        } else {
            try {
                friendId = StringUtils.getValidLong(friendStr);
                if (friendId == 0) {
                    friendId = null;
                    friend = new SimpleUser();
                }

            } catch (Exception ex) {
                friendId = null;
                friend = new SimpleUser();
            }
        }
        try {
            if (friendId != null) {
                friend = new SimpleUser(um.getUserById(friendId));
                friend.setOnline(SessionListener.isOnline(friendId));
            }
        } catch (Exception ex) {
            friend = new SimpleUser();
            friendId = null;
        }
        if (friendId != null && friendId != 0) {
            mode = MESSAGES_MODE;
        }
        if (DIALOGS_MODE.equalsIgnoreCase(mode)) {
            loadDialogs();
        }
        parsePeers();
    }

    private void parsePeers() {
        peers = new ArrayList<>();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        String peersParam = StringUtils.getValidString(request.getParameter("peers"), StringUtils.getValidString(SessionListener.getSessionAttribute(session,"chat_peers")));
        String[] p = peersParam.split("\\_");
        for (String peer : p) {
            long peerId = StringUtils.getValidLong(peer);
            if (peerId == 0) {
                continue;
            }
            User u = um.getUserById(peerId);
            if (u == null) {
                continue;
            }
            SimpleUser su = new SimpleUser(u);
            su.setOnline(SessionListener.isOnline(peerId));
            su.setUnreadMessages(mm.getUnreadMessagesNumber(me.getId(), peerId));
            if (!peers.contains(su)) {
                peers.add(su);
            }
        }
        while(peers.size() > 10) {
            peers.remove(peers.size()-1);
        }
        if (friendId != null && friendId != 0 && !peers.contains(friend)) {
            peers.add(friend);
            friend.setOnline(SessionListener.isOnline(friendId));
            friend.setUnreadMessages(mm.getUnreadMessagesNumber(me.getId(), friendId));
        }
        SessionListener.setSessionAttribute(session, "chat_peers", getValidPeersParam()+"_"+StringUtils.getValidString(SessionListener.getSessionAttribute(session, "chat_peers")));
    }

    public String getValidPeersParam() {
        String result = "";
        for (SimpleUser su : peers) {
            result += su.getId() + "_";
        }
        if (result.isEmpty()) {
            return "";
        } else {
            return result.substring(0, result.length() - 1);
        }
    }

    public String getValidPeersParamAndRemove(String peer) {
        String result = "";
        Long toRemove = StringUtils.getValidLong(peer);
        for (SimpleUser su : peers) {
            if (!toRemove.equals(su.getId())) {
                result += su.getId() + "_";
            }
        }
        if (result.isEmpty()) {
            return "";
        } else {
            return result.substring(0, result.length() - 1);
        }
    }

    private void loadDialogs() {
        dialogs.clear();
        Set<SimpleUser> users = mm.getRecentUsers(me.getId());
        SimpleUser me = new SimpleUser(this.me);
        me.setOnline(true);
        for (SimpleUser su : users) {
            Message lastMessage = mm.getLastMessage(me.getId(), su.getId());
            ChatDialog cd = new ChatDialog();
            cd.setLastMessage(new ChatMessage(lastMessage));
            cd.setUser1(me);
            cd.setUser2(su);
            su.setOnline(SessionListener.isOnline(su.getId()));
            cd.setLastMessageDate(lastMessage.getDateSent());
            cd.setNewMessages(mm.getUnreadMessagesNumber(me.getId(), su.getId()));
            dialogs.add(cd);
        }

        Collections.sort(dialogs, new Comparator<ChatDialog>() {
            @Override
            public int compare(ChatDialog o1, ChatDialog o2) {
                return -o1.getLastMessageDate().compareTo(o2.getLastMessageDate());
            }
        });
    }
    
    public void refreshDialogs() {
        loadDialogs();
    }
    
    public void refreshPeers() {
        parsePeers();
    }

    public String getActiveUserClass(SimpleUser su) {
        String result = "";
        if (su != null) {
            if (su.isOnline()) {
                result += "online";
            } else {
                result += "offline";
            }
            if (su.getUnreadMessages() > 0) {
                result += " new";
            }
            if (friendId != null && friendId != 0 && friendId.equals(su.getId())) {
                result += " selected";
            }
        }
        return result;
    }

    public List<ChatDialog> getAllDialogs() {
        return dialogs;
    }

    public List<SimpleUser> getPeers() {
        return peers;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public FileUploadController getFileUploadController() {
        return fileUploadController;
    }

    public SimpleUser getSelectedUser() {
        return selectedUser;
    }

    public boolean isDisplayUploadControl() {
        return displayUploadControl;
    }

    public void setDisplayUploadControl(boolean displayUploadContol) {
        this.displayUploadControl = displayUploadContol;
    }

    public void setSelectedUser(SimpleUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public User getMe() {
        return me;
    }

    public void setMe(User me) {
        this.me = me;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SimpleUser getFriend() {
        return friend;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        if (period == null) {
            period = 1L;
        }
        this.period = period;
    }

    public List<SimpleUser> getRecentUsers() {
        //return Collections.EMPTY_LIST;
        if (me == null) {
            return null;
        }
        Set<SimpleUser> s = mm.getRecentUsers(me.getId());
        if (s != null) {
            List<SimpleUser> lst = new ArrayList<>(s);
            for (SimpleUser u : lst) {
                u.setOnline(SessionListener.isOnline(u.getId()));
                u.setUnreadMessages(mm.getUnreadMessagesNumber(me.getId(), u.getId()));
            }
            return lst;
        } else {
            return null;
        }
    }

    public void sendMessage(ActionEvent evt) {
        if (me == null) {
            return;
        }
        if((text != null && !text.isEmpty()) || !fileUploadController.getFiles().isEmpty()) {
            mm.sendMessageAndUpload(me.getId(), friendId, "chat", StringUtils.getValidString(getText()).replace("\"", "&quot;"), fileUploadController.getFiles());
            fileUploadController.clearFiles(evt);
            text = null;
        }

        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String[] attachedFiles = request.getParameterValues("attached_files[]");
        if (attachedFiles != null) {
            for (String fileId : attachedFiles) {
                long id = StringUtils.getValidLong(fileId);
                if (id != 0) {
                    sendFileMessage(id);
                }
            }
        }
    }

    private void sendFileMessage(long fileId) {
        Attachment a = am.getUploadedFile(me.getId(), fileId);
        if (a != null && a.getUser().contains(me)) {
            am.shareFile(fileId, me.getId(), friendId);
            mm.sendMessage(me.getId(), friendId, "Attachment", "Файл: " + a.getName() + " [" + a.getMimeType() + ", " + a.getSize() / 1024 + "КБ]", fileId);
        }
    }

    private void requestStreamMessages() {
        if (me == null) {
            return;
        }
        RequestContext ctx = RequestContext.getCurrentInstance();
        Set<ChatMessage> s = requestStreamMessagesSet();
        ctx.addCallbackParam("ok", !s.isEmpty());
        ctx.addCallbackParam("messages", new JSONArray(s, true).toString());
    }

    private Set<ChatMessage> requestStreamMessagesSet() {
        if (log.isTraceEnabled()) {
            log.trace("requestStreamMessagesSet(): me =  " + me);
        }
        if (me == null) {
            return new TreeSet();
        }

        RequestContext ctx = RequestContext.getCurrentInstance();

        Set<ChatMessage> s = new TreeSet<>(new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage t, ChatMessage t1) {
                if (t.getDateSent().after(t1.getDateSent())) {
                    return 1;
                }
                if (t.getDateSent().before(t1.getDateSent())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });



        List<Message> msgs = mm.getAnyMessages(me.getId(), null, lastUpdate, null);
        if (msgs != null) {
            for (Message m : msgs) {
                s.add(new ChatMessage(m));
            }
            msgs.clear();
        }

        for (ChatMessage m : s) {
            if (lastUpdate == null || lastUpdate.before(m.getDateSent())) {
                lastUpdate = m.getDateSent();
            }
        }
        return s;
    }

    public String requestAllMessagesJsonArray() {

        Set<ChatMessage> s = requestStreamMessagesSet();
        System.out.println("requestAllMessagesJsonArray(): s = " + s);
        return (new JSONArray(s, true)).toString();
    }

    public String requestNewMessagesJsonArrayString() {
        if (log.isTraceEnabled()) {
            log.trace("requestNewMessagesJsonArrayString(): invocation of this method occured ");
        }
        Set<ChatMessage> s = requestNewMessagesSet();
        return (new JSONArray(s, true)).toString();
    }

    private Set<ChatMessage> requestNewMessagesSet() {
        if (me == null) {
            return new TreeSet();
        }

        if (adminMode && "_stream".equalsIgnoreCase(friendStr)) {
            requestStreamMessages();
            return Collections.EMPTY_SET;
        }

        RequestContext ctx = RequestContext.getCurrentInstance();

        Set<ChatMessage> s = new TreeSet<>(new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage t, ChatMessage t1) {
                if (t.getDateSent().after(t1.getDateSent())) {
                    return 1;
                }
                if (t.getDateSent().before(t1.getDateSent())) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        log.debug("Requesting messages from " + lastUpdate + " till now");
        List<Message> msgs = mm.getIncomingMessages(me.getId(), friendId, lastUpdate, null);
        if (msgs != null) {
            for (Message m : msgs) {
                s.add(new ChatMessage(m));
            }
            msgs.clear();
        }

        msgs = mm.getOutcomingMessages(me.getId(), friendId, lastUpdate, null);
        if (msgs != null) {
            for (Message m : msgs) {
                s.add(new ChatMessage(m));
            }
            msgs.clear();
        }

        Date maxDate = new Date(0);

        for (ChatMessage m : s) {
            if (maxDate.before(m.getDateSent())) {
                maxDate = m.getDateSent();
            }
        }
        if (lastUpdate == null || maxDate.after(lastUpdate)) {
            lastUpdate = maxDate;
        }

        return s;
    }

    public void requestNewMessages(ActionEvent evt) {
        if (me == null) {
            return;
        }

        if (adminMode && "_stream".equalsIgnoreCase(friendStr)) {
            requestStreamMessages();
            return;
        }

        RequestContext ctx = RequestContext.getCurrentInstance();
        Set<ChatMessage> s = requestNewMessagesSet();
        ctx.addCallbackParam("ok", !s.isEmpty());
        ctx.addCallbackParam("messages", new JSONArray(s, true).toString());
    }

    public List<SimpleUser> completeLogin(String query) {
        if (me == null) {
            return null;
        }
        List<SimpleUser> s = mm.filterUsersByLogin(me.getId(), query);
        return s;
    }

    public void changeUser(SelectEvent evt) {
        SimpleUser u = (SimpleUser) evt.getObject();
        System.out.println("selected=" + u);
        if (u != null && u.getId() != null) {
            RequestContext.getCurrentInstance().addCallbackParam("friend", u.getId());
        }
    }

    public String dummy() {
        return "chat.xhtml?faces-redirect=true";
    }

    public void showUploadControl(ActionEvent evt) {
        displayUploadControl = true;
    }

    public void hideUploadControl(ActionEvent evt) {
        displayUploadControl = false;
    }

    public boolean hasNewMessages() {
        if (me == null) {
            return false;
        }
        unreadMessagesNumber = mm.getUnreadMessagesNumber(me.getId(), null);
        return unreadMessagesNumber > 0;
    }

    public int getNewMessagesNumber() {
        // The method should be called right after hasNewMessages()
        if (me == null) {
            return 0;
        } else {
            return unreadMessagesNumber; // updated in hasNewMessages()
        }
    }
}
