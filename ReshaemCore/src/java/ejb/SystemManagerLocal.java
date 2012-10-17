package ejb;

import javax.ejb.Local;

/**
 *
 * @author danon
 */
@Local
public interface SystemManagerLocal {
    
    /**
     * Reset JPA caches.
     */
    public void resetJPA();
    
}
