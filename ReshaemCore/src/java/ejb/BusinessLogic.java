package ejb;

import entity.Attachment;
import entity.Order;
import entity.Subject;
import entity.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.primefaces.model.UploadedFile;

/**
 * This is an implementation Stateless Session Bean which represents active
 * session of current user.
 *
 * @author Danon
 */
@Stateless
public class BusinessLogic implements BusinessLogicLocal {

}
