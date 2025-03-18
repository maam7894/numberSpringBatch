package fr.kata.spring.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import fr.kata.spring.demo.service.FileLaunchService;

import java.util.Date;

public class JobCompletionNotificationListener implements JobExecutionListener {
  

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private long start;
    private String filename;
    private FileLaunchService fileLaunch;
    
    public JobCompletionNotificationListener(String filename, FileLaunchService fileLaunch) {
       this.filename = filename;
       this.fileLaunch = fileLaunch;
       
    }
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        start = System.currentTimeMillis();
        LOGGER.info("Job with id {} is about to start at {}", jobExecution.getJobId(), new Date());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
        long end = System.currentTimeMillis();
        LOGGER.info("Job completed at {}", new Date());
        LOGGER.info("Job execution time in mills {}", (end - start));
        LOGGER.info("Job status {}", jobExecution.getStatus());
        
        switch (jobExecution.getStatus()) {
         case BatchStatus.COMPLETED:
             fileLaunch.moveFileToProcessedFolder(filename);
            break;
        case FAILED:
            fileLaunch.moveFileToErrorFolder(filename);
            break;
        }
        }
    }
}
