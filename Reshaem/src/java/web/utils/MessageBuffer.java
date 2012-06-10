package web.utils;

import entity.Message;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is needed to store recent messages of a particular online user.
 * Seems to be thread-safe... :)
 * @author Danon
 */
public class MessageBuffer implements Serializable {

    private final List<Message> msgs;
    private int limit = 1000;
    private Date lastUnreadDate;
    
    public MessageBuffer() {
        msgs = Collections.synchronizedList(new LinkedList<Message>());
    }

    public synchronized void clear() {
        msgs.clear();
    }

    public synchronized void addMessage(Message msg) {
        msgs.add(msg);
        while(msgs.size()>limit)
            msgs.remove(msgs.size()-1);
    }
    
    public synchronized Message removeLastMessage() {
        return msgs.remove(0);
    }
    
    public synchronized int size() {
        return msgs.size();
    }
    
    public synchronized void setLimit(int limit) {
        this.limit = limit;
    }
    
    public synchronized int getLimit() {
        return limit;
    }
    
}
