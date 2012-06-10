package events;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

/**
 *
 * @author Danon
 */
@Singleton
public class EventEJB implements EventEJBLocal {

    @Resource(mappedName= "jms/ReshakaEventsFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName= "jms/ReshakaEvents")
    private Topic topic;
    
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    @PostConstruct
    private void init() {
        try {
            connection = connectionFactory.createConnection();
        } catch (Exception ex) {
            System.err.println("Failed to initialize EventEJB!");
        }
    }
    
    @Override
    public void postEvent(ReshakaEvent evt) {
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Message msg = session.createObjectMessage(evt);
            messageProducer = session.createProducer(topic);
            messageProducer.send(msg);
            System.err.println("Message sent!");
            messageProducer.close();
            session.close();
        } catch (Exception ex) {
            System.err.println("Failed to send message!");
            System.err.println(ex);
        }
    }
    
    @PreDestroy
    private void destroy() {
        try {
            connection.close();
        } catch (Exception ex) {}
    }
}
