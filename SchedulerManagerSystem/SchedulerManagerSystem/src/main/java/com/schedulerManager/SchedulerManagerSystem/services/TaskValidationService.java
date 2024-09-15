package com.schedulerManager.SchedulerManagerSystem.services;

import com.schedulerManager.SchedulerManagerSystem.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.schedulerManager.SchedulerManagerSystem.entity.Task;
import java.util.List;

@Service
public class TaskValidationService {

    @Autowired
    private TaskRepository taskRepository;

    public boolean isTaskValid(Task newTask){
        List<Task> allTasks = taskRepository.findAll();

        for(Task existingTask: allTasks){
            if (tasksOverlap(existingTask, newTask)) {
                return false;
            }
        }
        return true;
    }

    private boolean tasksOverlap(Task existingTask, Task newTask) {
        return newTask.getStartTime().isBefore(existingTask.getEndTime()) &&
                newTask.getEndTime().isAfter(existingTask.getStartTime());
    }
}
