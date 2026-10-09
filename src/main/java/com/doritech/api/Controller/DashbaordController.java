package com.doritech.api.Controller;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.DashboardDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.DashboardService;

@RestController
public class DashbaordController {

	@Autowired
	DashboardService dashboardService;
	
	@PostMapping("saveDashboaord")
    public ResponseEntity saveOrUpdateDashboardEntity(@RequestBody DashboardDTO dashboardDTO) throws IllegalAccessException, InvocationTargetException {
     return dashboardService.saveOrUpdateDashboardEntity(dashboardDTO);
    }

	@GetMapping("getDashboardByEmpId")
	public ResponseEntity getByEmpId(@RequestParam String empId) throws IllegalAccessException, InvocationTargetException {
     return dashboardService.getByEmpId(empId);

}
}