package ejb;

import events.EventManager;
import events.ReshakaEvent;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 
 * @author Danon
 */
@MessageDriven(mappedName = "jms/ReshakaEvents", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "ReshakaEventMDB"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "ReshakaEventMDB")
})
public class ReshakaEventMDB implements MessageListener {
    
    public ReshakaEventMDB() {
    }
    
    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage) {
            try {
                Object o = ((ObjectMessage)message).getObject();
                if(o instanceof ReshakaEvent)
                    EventManager.getInstance().processMessage((ReshakaEvent)o);
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    }
}
