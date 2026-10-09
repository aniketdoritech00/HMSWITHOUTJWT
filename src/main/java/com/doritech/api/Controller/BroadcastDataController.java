package com.doritech.api.Controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Service.BroadcastDataService;

@RestController
public class BroadcastDataController {

	
	 @Autowired
	    private BroadcastDataService dataService;

	 
	    @GetMapping("/fetchDataAndBroadcast")
	    public List<String> fetchDataAndBroadcast() {
	        try {
	            return dataService.fetchDataAndBroadcast();
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return null; 
	        }
	    }
	    
//	    @GetMapping("fetchDataAndBroadcastByLatestTime")
//	    public List<String> fetchDataAndBroadcastByLatestDate(@RequestParam("panelSerialNumbers")  List<String> panelSerialNumbers) {
//	        try {
//	        	System.out.println(panelSerialNumbers);
//	            return dataService.fetchDataAndBroadcastByLatestDate(panelSerialNumbers);
//	        } catch (SQLException e) {
//	            e.printStackTrace();
//	            System.out.println(e.getLocalizedMessage());
//	            return null; 
//	        }
//	    }
}
