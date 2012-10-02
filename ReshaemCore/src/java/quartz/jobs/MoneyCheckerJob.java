package quartz.jobs;

import ejb.MoneyManagerLocal;
import ejb.util.EJBUtils;
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
           mm = EJBUtils.resolve("java:global/ReshaemEE/ReshaemCore/MoneyManager!ejb.MoneyManagerLocal", MoneyManagerLocal.class);
           if(mm == null){
                log.error("Failed to resolve MoneyManager EJB");
                return;
           }
        }
        if(mm!=null) {
            mm.updateMoney();
        }
        log.trace("<< execute(): finished.");
    }
    
}
