package quartz.jobs;

import org.quartz.Job;

/**
 *
 * @author Danon
 */
public interface ReshakaJob extends Job {
    String getDescription();
    String getName();
}
