package com.doritech.api.Controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Service.TicketEscalationService;

/**
 * REST Controller to trigger and manage ticket escalation checks.
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketEscalationController {

    private static final Logger log = LoggerFactory.getLogger(TicketEscalationController.class);

    private final TicketEscalationService ticketEscalationService;

    public TicketEscalationController(TicketEscalationService ticketEscalationService) {
        this.ticketEscalationService = ticketEscalationService;
    }

    /**
     * Trigger escalation processing for all open tickets.
     *
     * POST /api/tickets/escalation/process
     *
     * Steps performed:
     * 1. Find all OPEN tickets (checking duplicate zone entries)
     * 2. Fetch alert time from sms_details via alert_generated_id
     * 3. Determine escalation level (L1/L2/L3) based on elapsed time
     * 4. Look up device_master using paneSerialNo
     * 5. Look up branch_master using branch from device_master
     * 6. Log all steps and outputs
     */
    @PostMapping("/escalation/process")
    public ResponseEntity<Map<String, String>> processEscalations() {
        log.info("[CONTROLLER] Escalation process triggered via API");

        Map<String, String> response = new HashMap<>();
        try {
            ticketEscalationService.processOpenTicketEscalations();
            response.put("status", "SUCCESS");
            response.put("message", "Escalation process completed. Check logs for full details.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[CONTROLLER] Escalation process failed: {}", e.getMessage(), e);
            response.put("status", "FAILED");
            response.put("message", "Escalation process encountered an error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}