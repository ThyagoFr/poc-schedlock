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
    @SchedulerLock(
            // Identificador da task
            name = "TaskScheduler_scheduledTask",
            // O bloqueio ira ser por 2 minutos e meio ,150 segundos, ou seja,mesmo que o job termine sua execução mais rapido que 2 minutos e meio, nenhuma task com mesmo identificador
            // poderar executar dentro dessa janela de tempo .
            lockAtLeastFor = "PT150S",
            // Apos 10 minutos de inicio de execucao, caso ainda não tenha terminado, outra instancia pode executar a task, pois o lock será desfeito.
            // Tomar cuidado pois pode criar inconsistência nos dados.
            // Se esse valor não for passado, como DEFAULT será utilizado o valor definido na annotation @EnableSchedulerLock utilizado na Main class.
            lockAtMostFor = "PT10M"
    )
    public void run() throws Exception{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String message = String.format("Executando instancia : %s no momento %s",this.instanceIdentifier,LocalDateTime.now().format(formatter));
        logger.info(message);
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }
}
