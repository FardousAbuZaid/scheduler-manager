package com.schedulerManager.SchedulerManagerSystem;

import com.schedulerManager.SchedulerManagerSystem.services.ReportService;

import com.schedulerManager.SchedulerManagerSystem.entity.Report;
import com.schedulerManager.SchedulerManagerSystem.entity.Task;
import com.schedulerManager.SchedulerManagerSystem.repository.ReportRepository;
import com.schedulerManager.SchedulerManagerSystem.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllReportedTasks_shouldCallCheckAndUpdateTasks() {
    
        List<Report> mockReports = List.of(new Report(), new Report());
        when(reportRepository.findAll()).thenReturn(mockReports);

        List<Report> reports = reportService.getAllReportedTasks();

        verify(reportRepository, times(1)).findAll();
        assertEquals(mockReports.size(), reports.size());
    }


    @Test
    void createLog_shouldReturnOkIfLogCreatedSuccessfully() {
        Report report = new Report();
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        ResponseEntity<String> response = reportService.createLog(report);

        assertEquals(ResponseEntity.ok("Log created successfully"), response);
    }

    @Test
    void createLog_shouldReturnErrorIfLogCreationFails() {
        Report report = new Report();
        when(reportRepository.save(any(Report.class))).thenThrow(new RuntimeException("Error"));

        ResponseEntity<String> response = reportService.createLog(report);

        assertEquals(ResponseEntity.status(500).body("Error saving log"), response);
    }


    @Test
    void logTaskStart_shouldLogStartTaskIfNotLogged() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setStartTime(LocalDateTime.now().minusHours(1));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(reportRepository.existsByMessageContaining("Task " + taskId + " started")).thenReturn(false);

        reportService.logTaskStart(taskId);

        verify(reportRepository, times(1)).save(any(Report.class)); 
    }

    @Test
    void logTaskEnd_shouldLogEndTaskIfNotLogged() {
        Long taskId = 2L;
        Task task = new Task();
        task.setId(taskId);
        task.setEndTime(LocalDateTime.now().minusHours(1));

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(reportRepository.existsByMessageContaining("Task " + taskId + " ended")).thenReturn(false);

        reportService.logTaskEnd(taskId);

        verify(reportRepository, times(1)).save(any(Report.class));  
    }

    @Test
    void logTaskStart_shouldNotLogIfStartAlreadyLogged() {
        Long taskId = 1L;
        when(reportRepository.existsByMessageContaining("Task " + taskId + " started")).thenReturn(true);

        reportService.logTaskStart(taskId);

        verify(reportRepository, never()).save(any(Report.class));  
    }

    @Test
    void logTaskEnd_shouldNotLogIfEndAlreadyLogged() {
        Long taskId = 2L;
        when(reportRepository.existsByMessageContaining("Task " + taskId + " ended")).thenReturn(true);

        reportService.logTaskEnd(taskId);

        verify(reportRepository, never()).save(any(Report.class)); 
    }
}
