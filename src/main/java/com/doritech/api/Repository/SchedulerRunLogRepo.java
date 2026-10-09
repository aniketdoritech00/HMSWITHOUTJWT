package com.doritech.api.Repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.api.Entity.SchedulerRunLog;

public interface SchedulerRunLogRepo extends JpaRepository<SchedulerRunLog, Long> {
    
    Optional<SchedulerRunLog> findTopBySchedulerNameAndRunDateAndStatus(
        String schedulerName, LocalDate runDate, String status
    );
}