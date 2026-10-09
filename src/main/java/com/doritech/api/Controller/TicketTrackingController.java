package com.doritech.api.Controller;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.TicketDetailDTO;
import com.doritech.api.DTO.TicketTrackingDTO;
import com.doritech.api.Service.TicketDetailService;
import com.doritech.api.Service.TicketTrackingService;

@RestController
@RequestMapping("/api/tickets")
public class TicketTrackingController {

	private static final Logger log = LoggerFactory.getLogger(TicketTrackingController.class);

	private final TicketTrackingService trackingService;
	private final TicketDetailService detailService;

	public TicketTrackingController(TicketTrackingService trackingService, TicketDetailService detailService) {
		this.trackingService = trackingService;
		this.detailService = detailService;
	}

	@GetMapping("/tracking/getAllZoneDesiredNames")
	public ResponseEntity<List<String>> getAllZoneDesiredNames() {
		log.info("TicketTrackingController :: getAllZoneDesiredNames()");
		List<String> zones = trackingService.getAllZoneDesiredNames();
		return ResponseEntity.ok(zones);
	}

	
	@GetMapping("/tracking/getOpenTicketTracking")
	public ResponseEntity<List<TicketTrackingDTO>> getTicketTracking(@RequestParam String zoneDesiredName, @RequestParam String loggedInBranch) {
		log.info("[CONTROLLER] GET /tracking — all open tickets");
		List<TicketTrackingDTO> data = trackingService.getOpenTicketTracking(zoneDesiredName, loggedInBranch);
		return ResponseEntity.ok(data);
	}

	@GetMapping("/tracking/getTicketDetailAndLogByTicketId")
	public ResponseEntity<TicketDetailDTO> getTicketDetailAndLogByTicketId(@RequestParam String ticketNo) {
		TicketDetailDTO dto = detailService.getTicketDetail(ticketNo);
		if (dto == null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/tracking/getZoneDesiredNameWiseCount")
	public ResponseEntity<List<Map<String, Object>>> getZoneDesiredNameWiseCount() {
		log.info("[CONTROLLER] GET /tracking/zone-count");
		List<Map<String, Object>> data = trackingService.getOpenTicketCountByZoneDesiredName();
		return ResponseEntity.ok(data);
	}

	@GetMapping("/open-summary")
    public ResponseEntity<List<Map<String, Object>>> getOpenTicketSummary() {
        List<Map<String, Object>> result = trackingService.getOpenTicketSummary();
        return org.springframework.http.ResponseEntity.ok(result);
    }
	
	// =========================================================================
    // GET — Open ticket tracking based on ZoneReport filtered data
    // =========================================================================
    @GetMapping("/zone-report")
    public ResponseEntity<Map<String, Object>> getTicketTrackingFromZoneReport(
            @RequestParam String branchCode,
            @RequestParam String zoneType,
            @RequestParam String status
            ) {

        log.info("[CONTROLLER] /zone-report called | branchCode={}, zoneType={}, status={}",
                branchCode, zoneType, status);

        try {
            Map<String, Object> result = trackingService
                    .getOpenTicketTrackingFromZoneReport(branchCode, zoneType, status);

            log.info("[CONTROLLER] /zone-report response ready | withTicket={}, withoutTicket={}",
                    result.get("totalWithOpenTicket"), result.get("totalWithoutOpenTicket"));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("[CONTROLLER] /zone-report failed | branchCode={}, error={}", branchCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Something went wrong while processing the request",
                            "details", e.getMessage()
                    ));
        }
    }
    
    
    @GetMapping("/ticket/detail")
    public ResponseEntity<?> getTicketDetail(
            @RequestParam(required = false) String ticketNo,
            @RequestParam(required = false) String branchCode) {
          if ((ticketNo == null || ticketNo.isBlank()) && (branchCode == null || branchCode.isBlank())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Either ticketNo or branchCode must be provided"));
        }  
        Object result = detailService.getTicketDetail(ticketNo, branchCode);
        if (result == null) {
            return ResponseEntity.ok(Map.of("message", "No data found", "data", null));
        }
        if (result instanceof List && ((List<?>) result).isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No tickets found for given criteria", "data", List.of()));
        }
        return ResponseEntity.ok(result);
    }
}