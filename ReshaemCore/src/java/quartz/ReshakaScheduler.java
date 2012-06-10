package quartz;

import java.util.Date;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import quartz.jobs.ReshakaJob;

/**
 *
 * @author Danon
 */
public class ReshakaScheduler {
    
    private static volatile ReshakaScheduler instance;
    
    private Scheduler scheduler;
    
    private ReshakaScheduler() throws SchedulerException {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
    }
    
    public static ReshakaScheduler getInstance() throws SchedulerException {
        if(instance==null) {
            synchronized(ReshakaScheduler.class) {
                if(instance==null)
                    instance = new ReshakaScheduler();
            }
        }
        return instance;
    }
    
    public void start() throws SchedulerException {
        if(!scheduler.isStarted())
            scheduler.start();
    }
    
    public void shutdown() throws SchedulerException {
        if(scheduler.isStarted())
            scheduler.shutdown();
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
