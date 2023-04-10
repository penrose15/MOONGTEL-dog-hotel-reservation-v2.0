package com.doghotel.reservation.global.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@Component
public class TestScheduler {
    private final Job job;
    private final JobLauncher jobLauncher;

    @Scheduled(cron = "0 59 23 * * *")
    public void startJob() {
        try {
            Map<String, JobParameter> jobParameterMap = new HashMap<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = new Date();
            String time1 = format.format(time);
            jobParameterMap.put("requestDate", new JobParameter(time1));
            JobParameters parameters = new JobParameters(jobParameterMap);

            JobExecution jobExecution = jobLauncher.run(job, parameters);
            while (jobExecution.isRunning()) {
                log.info("is running");
            }
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
        } catch (JobRestartException e) {
            e.printStackTrace();
        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}
