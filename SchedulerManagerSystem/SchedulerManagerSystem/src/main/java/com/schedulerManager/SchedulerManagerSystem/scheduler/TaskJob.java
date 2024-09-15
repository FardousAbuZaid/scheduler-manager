package com.schedulerManager.SchedulerManagerSystem.scheduler;

import com.schedulerManager.SchedulerManagerSystem.services.ReportService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskJob implements Job {

    private final ReportService reportService;

    @Autowired
    public TaskJob(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        Long taskId;
        String action;

        try {
            String[] parts = jobName.split("-");
            if (parts.length != 2) {
                throw new JobExecutionException("Invalid job name format: " + jobName);
            }
            taskId = Long.parseLong(parts[0]); 
            action = parts[1];  
        } catch (NumberFormatException e) {
            throw new JobExecutionException("Invalid task ID format in job name: " + jobName, e);
        } catch (Exception e) {
            throw new JobExecutionException("Error parsing job name: " + jobName, e);
        }

        if ("start".equals(action)) {
            reportService.logTaskStart(taskId);
        } else if ("end".equals(action)) {
            reportService.logTaskEnd(taskId);
        } else {
            throw new JobExecutionException("Invalid action in job name: " + action);
        }
    }
}

