package com.schedulerManager.SchedulerManagerSystem;

import com.schedulerManager.SchedulerManagerSystem.entity.Task;
import com.schedulerManager.SchedulerManagerSystem.repository.TaskRepository;
import com.schedulerManager.SchedulerManagerSystem.services.TaskService;
import com.schedulerManager.SchedulerManagerSystem.services.ReportService;
import com.schedulerManager.SchedulerManagerSystem.services.TaskValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskValidationService taskValidationService;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private TaskService taskService;

    private Task validTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validTask = new Task();
        validTask.setId(1L);
        validTask.setStartTime(LocalDateTime.now().plusHours(1));
        validTask.setEndTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(validTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_shouldReturnTaskWhenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(validTask));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals(validTask, result.get());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_shouldReturnEmptyWhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getTaskById(1L);

        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void addTask_shouldSaveAndScheduleValidTask() {
        when(taskValidationService.isTaskValid(validTask)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);

        Task savedTask = taskService.addTask(validTask);

        assertNotNull(savedTask);
        assertEquals(validTask, savedTask);
        verify(taskRepository, times(1)).save(validTask);
        verify(taskValidationService, times(1)).isTaskValid(validTask);
    }

    @Test
    void addTask_shouldThrowExceptionWhenTaskIsInvalid() {
        when(taskValidationService.isTaskValid(validTask)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(validTask));
        verify(taskValidationService, times(1)).isTaskValid(validTask);
        verify(taskRepository, never()).save(validTask);
    }

    @Test
    void updateTask_shouldUpdateExistingTask() {
        when(taskRepository.save(validTask)).thenReturn(validTask);

        ResponseEntity<Task> response = taskService.updateTask(1L, validTask);

        assertNotNull(response);
        assertEquals(validTask, response.getBody());
        verify(taskRepository, times(1)).save(validTask);
    }

    @Test
    void scheduleTask_shouldInvokeReportServiceForScheduling() throws SchedulerException {
        taskService.scheduleTask(validTask);
        verify(reportService, times(1)).schedulerTask(validTask.getId(), validTask.getStartTime(), validTask.getEndTime());
    }

}
