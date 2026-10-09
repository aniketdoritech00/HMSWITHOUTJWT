package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.ParamEntityDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.ParamService;

@RestController
@RequestMapping("/param")
public class ParamController {

	@Autowired
	private ParamService paramService;

	@GetMapping("/getNonSync/{serial}")
	public ResponseEntity getBySerial(@PathVariable String serial) {

		return paramService.getRecordBySerial(serial);

	}

	@PutMapping("/updateNonSyncSchedulerTime")
	public ResponseEntity updateNonSyncSchedulerTime(@RequestBody ParamEntityDTO request) {
		return paramService.updateNonSyncSchedulerTime(request);

	}

	@GetMapping("/getDescByCodeAndSerial")
	public ResponseEntity getDescByCodeAndSerial(@RequestParam String code, @RequestParam String serial) {
		return paramService.getDescByCodeAndSerial(code, serial);
	}

	@PostMapping("/updateDesc")
	public ResponseEntity updateDesc(
	        @RequestParam("code") String code,
	        @RequestParam("serial") String serial,
	        @RequestParam("descp1") String descp1) {

	    return paramService.updateDesc(code, serial, descp1);
	}
}
