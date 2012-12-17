package web.chat;

import data.SimpleUser;
import ejb.ConfigurationManagerLocal;
import ejb.MessageManagerLocal;
import ejb.UserManagerLocal;
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
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONArray;
import web.FileUploadController;
import web.utils.SessionListener;

/**
 * Backing bean for chat
 * @author Danon <Anton Danshin>
 */
@ManagedBean
@ViewScoped
public class ChatBean implements Serializable {

    private static final Logger log = Logger.getLogger(ChatBean.class);
    
    @EJB
    MessageManagerLocal mm;

    @EJB
    ConfigurationManagerLocal cm;

    @EJB
    UserManagerLocal um;

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

        if (me.getUserGroup() == User.ADMIN) {
            adminMode = true;
        }

        Map<String, String> params = fctx.getExternalContext().getRequestParameterMap();
        friendStr = params.get("friend");
        
        if (log.isTraceEnabled()) {
            log.trace("ChatBean(): friendStr = " + friendStr);
        }

        String period = params.get("period");
        if (period != null) {
            try {
                this.period = Long.parseLong(period);
            } catch (NumberFormatException ex) {
            }
            if(this.period < 0)
                this.period = 1L; // One day
        } else this.period = 1L;
        
        lastUpdate = new Date(System.currentTimeMillis() - 1000*60*60*24*this.period);
    }

    @PostConstruct
    private void init() {
        SimpleUserConverter.mm = this.mm;
        if (friendStr == null) {
            friendId = null;
        } else if (friendStr.equalsIgnoreCase("support")) {
            friendId = cm.getAdminId();
        } else if (friendStr.equalsIgnoreCase("_stream")) {
            if (me.getUserGroup() != 1) {
                friendId = null;
                friend = new SimpleUser();
            } else {
                adminMode = true;
            }
        } else {
            try {
                friendId = Long.parseLong(friendStr);
            } catch (Exception ex) {
                friendId = null;
                friend = null;
            }
        }
        try {
            friend = new SimpleUser(um.getUserById(friendId));
            friend.setOnline(SessionListener.isOnline(friendId));
        } catch (Exception ex) {
            friend = new SimpleUser();
            friendId = null;
        }
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
        if (period==null) period = 1L;
        this.period = period;
    }

    public List<SimpleUser> getRecentUsers() {
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
        mm.sendMessageAndUpload(me.getId(), friendId, "chat", getText(), fileUploadController.getFiles());
        text = null;
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
    
    private Set<ChatMessage> requestStreamMessagesSet(){
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
    
    public String requestAllMessagesJsonArray(){
        
        Set<ChatMessage> s = requestStreamMessagesSet();
        System.out.println("requestAllMessagesJsonArray(): s = " + s);
        return (new JSONArray(s, true)).toString();
    }
    
    public String requestNewMessagesJsonArrayString(){
        if (log.isTraceEnabled()) {
            log.trace("requestNewMessagesJsonArrayString(): invocation of this method occured ");
        }
        Set<ChatMessage> s = requestNewMessagesSet();
        return (new JSONArray(s,true)).toString();
    }

    
    private Set<ChatMessage> requestNewMessagesSet(){
        if (me == null) {
            return new TreeSet();
        }

        if (adminMode && "_stream".equalsIgnoreCase(friendStr)) {
            requestStreamMessages();
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

        log.debug("Requesting messages from "+lastUpdate+" till now");
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

        for(ChatMessage m : s) 
            if(maxDate.before(m.getDateSent()))
                    maxDate = m.getDateSent();
        if(lastUpdate == null || maxDate.after(lastUpdate))
            lastUpdate = maxDate;
        
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
