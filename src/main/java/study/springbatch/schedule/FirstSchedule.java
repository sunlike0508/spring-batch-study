package study.springbatch.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class FirstSchedule {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;


    public FirstSchedule(JobLauncher jobLauncher, JobRegistry jobRegistry) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
    }


    //    @Scheduled(cron = "10 * * * * *", zone = "Asia/Seoul")
    //    public void runFirstJob()
    //            throws NoSuchJobException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
    //            JobParametersInvalidException, JobRestartException {
    //
    //        log.info("first schedule start");
    //
    //        JobParameters jobParameters =
    //                new JobParametersBuilder().addString("data", LocalDateTime.now().toString()).toJobParameters();
    //
    //        jobLauncher.run(jobRegistry.getJob("firstJob"), jobParameters);
    //    }

}
