package ejb;

import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;

/**
 * System operations
 * @author danon
 */
@Stateless @Singleton
public class SystemManager implements SystemManagerLocal {

    Logger log = Logger.getLogger(SystemManager.class);
    
    @PersistenceContext(unitName = "ReshaemCorePU")
    private EntityManager em;
    
    

    public void resetJPA() {
        em.getEntityManagerFactory().getCache().evictAll();
        if(log.isDebugEnabled()) {
            log.debug("resetJPA(): JPA caches have been cleared.");
        }
    }
    
}
