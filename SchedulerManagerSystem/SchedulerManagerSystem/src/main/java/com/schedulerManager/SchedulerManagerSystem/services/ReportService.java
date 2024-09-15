package com.schedulerManager.SchedulerManagerSystem.services;
import org.springframework.transaction.annotation.Transactional;

import com.schedulerManager.SchedulerManagerSystem.entity.Report;
import com.schedulerManager.SchedulerManagerSystem.entity.Task;
import com.schedulerManager.SchedulerManagerSystem.repository.ReportRepository;
import com.schedulerManager.SchedulerManagerSystem.repository.TaskRepository;
import com.schedulerManager.SchedulerManagerSystem.scheduler.TaskJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private  TaskRepository taskRepository;

      /**
     * Retrieves all reported tasks and updates the report if necessary.
     * 
     * @return a list of all reports
     */
    public List<Report> getAllReportedTasks(){
        checkAndUpdateTasks();
        return reportRepository.findAll();
    }

     /**
     * Checks the tasks against the current time and updates the report with start and end logs.
     * This method is called to ensure that task logs are created if tasks have started or ended.
     */
    @Transactional
    public void checkAndUpdateTasks(){
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findAll();

        for(Task task: tasks){
            // Log task start if the current time is within the task's duration
            if(task.getStartTime().isBefore(now) && task.getEndTime().isAfter(now)){
                logTaskStart(task.getId());
            }
            // Log task end if the current time is after the task's end time
           else if (task.getEndTime().isBefore(now)) { 
                logTaskEnd(task.getId());
            }
        }
    }
    
   /**
     * Creates a log entry for a task.
     * 
     * @param taskLog the report to be saved
     * @return a response entity indicating success or failure
     */
    public ResponseEntity<String> createLog( Report taskLog) {
        try {
            reportRepository.save(taskLog);
            return ResponseEntity.ok("Log created successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving log", e);
            return ResponseEntity.status(500).body("Error saving log");
        }
    }
    
   /**
     * Schedules tasks with Quartz Scheduler.
     * 
     * @param taskName the task ID
     * @param startTime the start time of the task
     * @param endTime the end time of the task
     * @throws SchedulerException if there is an issue scheduling the job
     */
    public void schedulerTask(Long taskName, LocalDateTime startTime,  LocalDateTime endTime) throws SchedulerException {
       // Schedule a job to trigger at the task's start time
       JobDetail jobDetail = JobBuilder.newJob(TaskJob.class)
                .withIdentity(taskName + "-start")
                .build();
        Trigger startTrigger = TriggerBuilder.newTrigger()
                .withIdentity(taskName+ "-start")
                .startAt(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        scheduler.scheduleJob(jobDetail, startTrigger);

        // Schedule a job to trigger at the task's end time
        JobDetail endJobDetail = JobBuilder.newJob(TaskJob.class)
                .withIdentity(taskName + "-end")
                .build();
        Trigger endTrigger = TriggerBuilder.newTrigger()
                .withIdentity(taskName + "-end")
                .startAt(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))
                .build();
        scheduler.scheduleJob(endJobDetail, endTrigger);
    }

     /**
     * Logs when a task starts, if not already logged.
     * 
     * @param taskId the ID of the task that started
     */
    public void logTaskStart(Long taskId) {
        if (!reportExists(taskId, "started")) {
            Report log = new Report();
            Task currentTask = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
            log.setTime(currentTask.getStartTime());
            log.setMessage("Task " + taskId + " started");
            reportRepository.save(log);
        }
    }

    /**
     * Logs when a task ends, if not already logged.
     * 
     * @param taskId the ID of the task that ended
     */
    public void logTaskEnd(Long taskId) {
        if (!reportExists(taskId, "ended")) {
            Report log = new Report();
            Task currentTask = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
            log.setTime(currentTask.getEndTime());
            log.setMessage("Task " + taskId + " ended");
            reportRepository.save(log);
        }
    }

    private boolean reportExists(Long taskId, String status) {
        return reportRepository.existsByMessageContaining("Task " + taskId + " " + status);
    }
}
