package quartz.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Danon
 */
public class TestJob implements ReshakaJob {

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.err.println("Quartz Job completed!");
    }

    @Override
    public String getDescription() {
        return "Job for testing";
    }

    @Override
    public String getName() {
        return "Test job";
    }
    
}
