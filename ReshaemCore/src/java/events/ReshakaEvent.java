package events;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Danon
 */
public class ReshakaEvent implements Comparable<ReshakaEvent>, Serializable {
    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_DISABLED = 0;
    
    public static final int TYPE_SYSTEM = 1;
    public static final int TYPE_ANY = 0;
    
    private long ID;
    private int type;
    private int priority;
    private Date date;
    private int status;
    private String xmlContent;
    private String description;
    
    private static class IDGenerator {
        private static AtomicLong currentID = new AtomicLong(0); 
        public static long nextID() {
            return currentID.addAndGet(1);
        }
        public static long getCurrentID() {
            return currentID.get();
        }
    }

    public ReshakaEvent() {
        date = new Date();
        ID = ReshakaEvent.IDGenerator.nextID();
        status = STATUS_ENABLED;
        xmlContent = null;
        priority = 0;
        type = TYPE_SYSTEM;
        description = "Generic Reshaka Event";
    }
    
    /**
     * Default implementation: compares date, priority and ID.
     * Lower value of priority means higher importance level.
     * @param o other event to compare to
     * @return -1 if this event is more important than the other one, 
     *          0 if events have equal importance,
     *          1 if this event is less important.
     */
    @Override
    public int compareTo(ReshakaEvent o) {
        int r = 0;
        if(this.date!=null && o.date!=null) {
                if(this.date.before(o.date))
                    r = -1;
                else if(this.date.after(o.date))
                    r = 1;
                // => r=0
            }
        if(r==0)
            r = this.priority - o.priority;
        if(r==0)
            if(this.ID>o.ID)
                r = 1;
            else if(this.ID<o.ID)
                r = -1;
        
        return r;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getXmlContent() {
        return xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
