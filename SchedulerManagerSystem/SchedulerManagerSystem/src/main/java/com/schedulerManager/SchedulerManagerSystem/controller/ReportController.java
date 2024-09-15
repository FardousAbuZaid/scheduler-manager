package com.schedulerManager.SchedulerManagerSystem.controller;

import com.schedulerManager.SchedulerManagerSystem.entity.Report;
import com.schedulerManager.SchedulerManagerSystem.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public List<Report> getReports() {
        return reportService.getAllReportedTasks();
    }

    @PostMapping
    public ResponseEntity<String> createLog(@RequestBody Report taskLog) {
       return reportService.createLog(taskLog);
    }
}
