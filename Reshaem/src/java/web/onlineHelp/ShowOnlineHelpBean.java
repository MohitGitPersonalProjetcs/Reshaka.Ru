/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web.onlineHelp;

import ejb.OnlineHelpManagerLocal;
import entity.OnlineHelp;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author rogvold
 */
@ManagedBean
@ViewScoped
public class ShowOnlineHelpBean {

    @EJB
    OnlineHelpManagerLocal onlineMan;

    public List<OnlineHelp> getOnlineHelpList() {
        return onlineMan.getOnlineHelpItems(null, -1);
    }

}
