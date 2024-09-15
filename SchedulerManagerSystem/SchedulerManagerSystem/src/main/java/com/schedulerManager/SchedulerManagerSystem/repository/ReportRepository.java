package com.schedulerManager.SchedulerManagerSystem.repository;

import com.schedulerManager.SchedulerManagerSystem.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByMessageContaining(String message);

}
