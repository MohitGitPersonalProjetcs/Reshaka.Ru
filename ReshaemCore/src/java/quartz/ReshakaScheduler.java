package quartz;

import java.util.Date;
import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import quartz.jobs.ReshakaJob;

/**
 * Simple singleton class for accessing Quartz Scheduler
 * @author Danon
 */
public class ReshakaScheduler {
    
    private Scheduler scheduler;
    private static Logger log = Logger.getLogger(ReshakaScheduler.class);
    
    private static abstract class InstanceHolder {
        
        private static ReshakaScheduler newInstance()
        {
            try {
                return new ReshakaScheduler();
            } catch (SchedulerException ex) {
                throw new RuntimeException("Unable to create Scheduler.", ex);
            }
        }
        
        private static ReshakaScheduler instance = newInstance();
    }
    
    private ReshakaScheduler() throws SchedulerException {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
    }
    
    
    public static ReshakaScheduler getInstance() {
        return InstanceHolder.instance;
    }
    
    public void start() throws SchedulerException {
        if(!scheduler.isStarted()) {
            scheduler.start();
        }
    }
    
    public void shutdown() throws SchedulerException {
        if(scheduler.isStarted()) {
            scheduler.shutdown();
        }
    }
    

    public Date schedule(Class<? extends ReshakaJob> jclazz, Trigger trigger) {
        try {         
            // define the job and tie it to our HelloJob class
            JobDetail job = JobBuilder.newJob(jclazz).build();

            // Tell quartz to schedule the job using our trigger
            return scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
        return null;
    }
}
