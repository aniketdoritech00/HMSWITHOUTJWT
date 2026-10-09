package com.doritech.api.Config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Service.TicketEscalationService;

@Component
public class EscalationScheduler {

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private TicketEscalationService escalationService;

    @Autowired
    private ParamRepository paramRepository;

    private volatile ScheduledFuture<?> scheduledTask;

    public EscalationScheduler() {
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("Application started. Kicking off escalation scheduler...");
        scheduleNextRun();
    }

    public void scheduleNextRun() {
        LocalDateTime nextExecutionTime = getNextExecutionTime();

        Date executionDate = Date.from(
                nextExecutionTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        scheduledTask = taskScheduler.schedule(() -> {
            try {

                escalationService.processOpenTicketEscalations();
                scheduleNextRun();
            } catch (Exception e) {
                System.err.println("Escalation process failed: " + e.getMessage());
                scheduleNextRun();
            }
        }, executionDate);

        System.out.println("Next escalation scheduled at: " + nextExecutionTime);
    }

    public LocalDateTime getNextExecutionTime() {
        ParamEntity paramEntity = paramRepository.findByCodeAndSerial(
                "TICKET",
                "ESCALATION_INTERVAL"
        );

        int defaultInterval = 30;

        if (paramEntity != null) {
            try {
                int interval = Integer.parseInt(paramEntity.getDescp1());

                return LocalDateTime.now().plusSeconds(interval);

            } catch (NumberFormatException e) {
                System.err.println(
                        "Invalid interval value in DB, defaulting to 30 seconds"
                );
            }
        } else {
            System.err.println(
                    "No interval config found in DB, defaulting to 30 seconds"
            );
        }

        return LocalDateTime.now().plusSeconds(defaultInterval);
    }
}
