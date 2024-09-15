package com.schedulerManager.SchedulerManagerSystem.services;

import com.schedulerManager.SchedulerManagerSystem.entity.Task;
import com.schedulerManager.SchedulerManagerSystem.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.logging.Logger;


@Service
public class TaskService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    @Autowired
    public TaskRepository taskRepository;

    @Autowired
    private TaskValidationService taskValidationService;

    @Autowired
    private ReportService reportService;

    public List<Task> getAllTasks(){
      return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id){
        return taskRepository.findById(id);
    }

    public Task addTask(Task task) {
        if (taskValidationService.isTaskValid(task)) {
            Task savedTask = taskRepository.save(task);
            scheduleTask(task);
            return savedTask;

        } else {
            LOGGER.log(Level.SEVERE, "Task overlaps with existing tasks");
            throw new IllegalArgumentException("Task overlaps with existing tasks");
        }
    }
    public ResponseEntity<Task> updateTask(Long id, Task task) {
        task.setId(id);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    public void scheduleTask(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        Long taskId = task.getId();

        try {
            reportService.schedulerTask(taskId, startTime, endTime);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    // Handle missed tasks after system restarts
    public void handleMissedTasks(){
        LocalDateTime now = LocalDateTime.now();
        // Fetch tasks that need to be executed
        List<Task> tasks =  findTasksToBeExecuted(now);

        for (Task task: tasks){
            // Check if the task execution time has passed
            if(task.getEndTime().isBefore(now)){
                // Task time has passed, log or handle accordingly
                logMissedTask(task);
            }
            else {
                // Schedule the task for execution
                scheduleTask(task);
            }
        }
    }

    // Fetch tasks that are scheduled for execution
    public List<Task> findTasksToBeExecuted(LocalDateTime now){
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getStartTime().isBefore(now) && task.getEndTime().isAfter(now))
                .collect(Collectors.toList());
    }

    // Log missed tasks
    private void logMissedTask(Task task){
        System.out.println("Missed task: " + task.getId());
    }

    // This method will be called when the application starts
    @PostConstruct
    public void onStartup() {
        handleMissedTasks();
    }
}
