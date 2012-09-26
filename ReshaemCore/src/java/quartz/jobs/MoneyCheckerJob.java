package quartz.jobs;

import ejb.MoneyManagerLocal;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author danon
 */
public class MoneyCheckerJob implements ReshakaJob {
    private Logger log = Logger.getLogger(MoneyCheckerJob.class);
    
    private volatile static MoneyManagerLocal mm;

    @Override
    public String getDescription() {
        return "Check for incoming money and update internal balance.";
    }

    @Override
    public String getName() {
        return "MoeneyChecker";
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        log.trace(">> execute(): started");
        if (mm==null) {
            try {
                Context context = null;
                context = new InitialContext();
                mm = (MoneyManagerLocal)context.lookup("java:global/ReshaemEE/ReshaemCore/MoneyManager!ejb.MoneyManagerLocal");
            } catch (Exception ex) {
                log.error("Failed to resolve MoneyManager EJB",ex);
                return;
            }
        }
        if(mm!=null) {
            mm.updateMoney();
        }
        log.trace("<< execute(): finished.");
    }
    
}
