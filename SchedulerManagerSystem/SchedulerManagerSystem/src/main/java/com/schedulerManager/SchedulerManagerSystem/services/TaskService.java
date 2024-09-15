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

    // Logger for logging messages and errors
    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    @Autowired
    public TaskRepository taskRepository;

    @Autowired
    private TaskValidationService taskValidationService;

    @Autowired
    private ReportService reportService;

    /**
     * Retrieves a list of all tasks from the task repository.
     * 
     * @return a list of all tasks
     */
    public List<Task> getAllTasks(){
      return taskRepository.findAll();
    }

     /**
     * Retrieves a task by its ID.
     * 
     * @param id the task ID
     * @return an optional containing the task if found, otherwise empty
     */
    public Optional<Task> getTaskById(Long id){
        return taskRepository.findById(id);
    }

      /**
     * Adds a new task to the repository and schedules it for execution.
     * Validates the task before saving to ensure no overlaps with existing tasks.
     * 
     * @param task the task to be added
     * @return the saved task
     * @throws IllegalArgumentException if the task overlaps with existing tasks
     */
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

      /**
     * Updates an existing task in the repository.
     * 
     * @param id the ID of the task to be updated
     * @param task the task data to update
     * @return the updated task wrapped in a ResponseEntity
     */
    public ResponseEntity<Task> updateTask(Long id, Task task) {
        task.setId(id);
        return ResponseEntity.ok(taskRepository.save(task));
    }

     /**
     * Schedules a task to be executed at its start and end times using the report service.
     * 
     * @param task the task to be scheduled
     */
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

     /**
     * Handles any tasks that were missed while the system was down or restarted.
     * It checks for tasks that should have been executed in the past and logs or schedules them accordingly.
     */   
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
