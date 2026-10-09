package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.SiaMasterDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.SiaMasterService;

@RestController
@RequestMapping("/siaMaster")
public class SiaMasterController {

	@Autowired
	private SiaMasterService siaMasterService;

	@PostMapping("/saveOrUpdate")
	public ResponseEntity saveOrUpdate(@RequestBody SiaMasterDTO dto) {

		return siaMasterService.saveOrUpdate(dto);
	}

	@DeleteMapping("/deleteById")
	public ResponseEntity softDeleteById(@RequestParam Long id, @RequestParam String status) {

		return siaMasterService.softDeleteById(id, status);
	}

	@GetMapping("/getById")
	public ResponseEntity getById(@RequestParam Long id) {

		return siaMasterService.getById(id);
	}

	@GetMapping("/getAllDataWithPagination")
	public ResponseEntity getAllDataWithPagination(@RequestParam String branchCode,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		return siaMasterService.getAllDataWithPagination(branchCode, page, size);
	}

	@GetMapping("/getAllData")
	public ResponseEntity getAllData(@RequestParam String branchCode) {

		return siaMasterService.getAllData(branchCode);
	}

	@GetMapping("/checkSiaSerialNo")
	public ResponseEntity checkSiaSerialNo(@RequestParam String siaSerialNo) {

		return siaMasterService.checkSiaSerialNo(siaSerialNo);
	}

}