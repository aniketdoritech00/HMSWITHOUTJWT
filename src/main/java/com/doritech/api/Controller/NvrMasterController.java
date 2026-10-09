package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.NvrRequest;
import com.doritech.api.Entity.NvrConnectionDetails;
import com.doritech.api.Service.NvrMasterService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@RestController
@RequestMapping("/api/nvr-master")
public class NvrMasterController {

	@Autowired
	private NvrMasterService service;

	@PostMapping()
	public ResponseEntity<?> create(@RequestBody NvrRequest request) {
		return service.createNvr(request);
	}

	@GetMapping
	public ResponseEntity<?> getAll() {
		return service.getAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return service.getById(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody NvrRequest request) {

		return service.updateNvr(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		return service.delete(id);

	}

	@GetMapping("/camera-types/active")
	public com.doritech.api.Entity.ResponseEntity getAllActiveCameraTypes() {
		return service.getAllActiveCameraTypes();
	}

	@GetMapping("/disk-types/active")
	public com.doritech.api.Entity.ResponseEntity getAllActiveDiskTypes() {
		return service.getAllActiveDiskTypes();
	}

	@GetMapping("hard-disk/health-status")
	public com.doritech.api.Entity.ResponseEntity getDiskHealthStatus(@RequestParam String code,
			@RequestParam String serial) {
		return service.getDiskHealthStatus(code, serial);
	}

	@PostMapping("/createNvrConnection")
	public com.doritech.api.Entity.ResponseEntity createNvrConnection(
			@RequestBody NvrConnectionDetails nvrConnectionDetails) {
		System.out.println(nvrConnectionDetails);

		return service.save(nvrConnectionDetails);
	}

	@GetMapping("/getAllNvrConnectionDetails")
	public com.doritech.api.Entity.ResponseEntity getAllNvrConnectionDetails() {
		return service.getAllNvrConnectionDetails();
	}

	@GetMapping("/getByIdNvrConnectionDetails")
	public com.doritech.api.Entity.ResponseEntity getByIdNvrConnectionDetails(@RequestParam Long id) {
		return service.getByIdNvrConnectionDetails(id);
	}

	@PutMapping("/updateNvrConnectionDetails")

	public com.doritech.api.Entity.ResponseEntity updateNvrConnectionDetails(
			@RequestBody NvrConnectionDetails nvrConnectionDetails, @RequestParam Long id) {
		return service.updateNvrConnectionDetails(id, nvrConnectionDetails);
	}

	@DeleteMapping("/deleteNvrConnectionDetails")

	public com.doritech.api.Entity.ResponseEntity deleteNvrConnectionDetails(@RequestParam Long id) {
		return service.deleteNvrConnectionDetails(id);
	}

	@GetMapping("/getNvrConnectionDetailsByNvrId/{nvrId}")
	public com.doritech.api.Entity.ResponseEntity getNvrConnectionDetailsByNvrId(@PathVariable String nvrId) {
		return service.getNvrConnectionDetailsByNvrId(nvrId);
	}

	@DeleteMapping("/deleteNvrConnectionDetailsByNvrId/{nvrId}")
	public com.doritech.api.Entity.ResponseEntity deleteByNvrId(@PathVariable String nvrId) {
		return service.deleteNvrConnectionDetailsByNvrId(nvrId);
	}

	@GetMapping("/getActiveNvrDeatilsByVendorId")
	public com.doritech.api.Entity.ResponseEntity getActiveNvrDeatilsByVendorId(@RequestParam String venderId){
		return service.getActiveNvrDetailsByVendorId(venderId);
	}

	@GetMapping("/getAllNvrNamesAndId")
	public com.doritech.api.Entity.ResponseEntity getAllNvrNamesAndId() {
		return service.getAllNvrNamesAndId();

	}

	@GetMapping("/checkNvrSerialNo")
	public com.doritech.api.Entity.ResponseEntity checkNvrSerialNo(@RequestParam(required = false) Long nvrId,
			@RequestParam String nvrSerialNo) {

		return service.checkNvrSerialNo(nvrId, nvrSerialNo);
	}

	@GetMapping("/getNvrAllDetailsByPanelSerialNo")
	public com.doritech.api.Entity.ResponseEntity getNvrAllDetailsByPanelSerialNo(@RequestParam String panelSerialNo) {
		return service.getNvrAllDetailsByPanelSerialNo(panelSerialNo);
	}

	@GetMapping("/getCameraDetailsByPanelSerialNo")
	public com.doritech.api.Entity.ResponseEntity getCameraDetailsByPanelSerialNo(@RequestParam String panelSerialNo) {
		return service.getCameraDetailsByPanelSerialNo(panelSerialNo);
	}

	@GetMapping("/getNvrHardDiskDetailsByPanelSerialNo")
	public com.doritech.api.Entity.ResponseEntity getNvrHardDiskDetailsByPanelSerialNo(
			@RequestParam String panelSerialNo) {
		return service.getNvrHardDiskDetailsByPanelSerialNo(panelSerialNo);
	}
	
	@GetMapping("/getCameraByPanelSerialNoAndChannelNo")
	public com.doritech.api.Entity.ResponseEntity getCameraByPanelSerialNoAndChannelNo(@RequestParam String panelSerialNo, @RequestParam Integer channelNo) {
		return service.getCameraByPanelSerialNoAndChannelNo(panelSerialNo, channelNo);
	}
	
	@GetMapping("/getAllHikvisionNvrs")
	public com.doritech.api.Entity.ResponseEntity getAllHikvisionNvrs() {
		return service.getAllHikvisionNvrs();
	}
	
	@GetMapping("/getNvrDetailsByPanelSerialNo")
	public com.doritech.api.Entity.ResponseEntity getNvrDetailsByPanelSerialNo(@RequestParam String panelSerialNo) {
		return service.getNvrDetailsByPanelSerialNo(panelSerialNo);
	}
	
	@GetMapping("/getAllNvrAndVendorDetailsByPanelSerialNo")
	public com.doritech.api.Entity.ResponseEntity getAllNvrAndVendorDetailsByPanelSerialNo(@RequestParam String panelSerialNo) {
		return service.getAllNvrAndVendorDetailsByPanelSerialNo(panelSerialNo);
	}
	
}