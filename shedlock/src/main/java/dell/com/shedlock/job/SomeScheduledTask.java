package dell.com.shedlock.job;


import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class SomeScheduledTask {

    Logger logger = LoggerFactory.getLogger(SomeScheduledTask.class);

    @Value("${random.instance.identifier}")
    private String instanceIdentifier;

    private final Job job;

    private final JobLauncher jobLauncher;

    public SomeScheduledTask(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    @SchedulerLock(name = "task_id", lockAtLeastFor = "PT150S", lockAtMostFor = "PT10M")
    public void run() throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        logger.info("Running instance : {} at {}",this.instanceIdentifier,LocalDateTime.now().format(formatter));
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }
}
