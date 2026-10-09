//package com.doritech.api.Controller;
//
//import com.doritech.api.Service.BroadcastDataService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/broadcast")
//public class BroadcastTriggerController {
//
//    @Autowired
//    private BroadcastDataService broadcastDataService;
//
//    /**
//     * Endpoint to trigger immediate broadcast when Project A inserts new data
//     */
//    @PostMapping("/trigger")
//    public ResponseEntity<String> triggerBroadcast(@RequestBody Map<String, String> payload) {
//    	System.out.println("⚡ Received broadcast trigger -> ");
//        try {
//            String panelSerialNo = payload.get("panelSerialNo");
//            String senderMobileNo = payload.get("senderMobileNo");
//            
//            System.out.println("⚡ Received broadcast trigger for panel: " + panelSerialNo + 
//                             ", mobile: " + senderMobileNo);
//            
//            // Trigger immediate broadcast
//            broadcastDataService.fetchDataAndBroadcastAsync();
//            
//            return ResponseEntity.ok("Broadcast triggered successfully");
//        } catch (Exception e) {
//            System.err.println("Error triggering broadcast: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.internalServerError()
//                    .body("Failed to trigger broadcast: " + e.getMessage());
//        }
//    }
//    
//    /**
//     * Manual trigger endpoint for testing
//     */
//    @GetMapping("/trigger-manual")
//    public ResponseEntity<String> triggerManualBroadcast() {
//        try {
//            System.out.println("📡 Manual broadcast triggered");
//            broadcastDataService.fetchDataAndBroadcastAsync();
//            return ResponseEntity.ok("Manual broadcast triggered");
//        } catch (Exception e) {
//            System.err.println("Error in manual broadcast: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.internalServerError()
//                    .body("Failed: " + e.getMessage());
//        }
//    }
//}