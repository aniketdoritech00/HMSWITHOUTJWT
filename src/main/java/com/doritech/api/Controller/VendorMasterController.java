package com.doritech.api.Controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.noneDSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.DTO.VendorMasterDTO;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Service.ParamService;
import com.doritech.api.Service.VendorMasterService;

import io.swagger.models.Response;

@RestController
public class VendorMasterController {

	@Autowired
	private VendorMasterService vendorMasterService;

	@Autowired
	private ParamRepository paramRepository;

	@Autowired
	private ParamService paramService;

	@PostMapping("/createVendor")
	public ResponseEntity createVendor(@RequestBody VendorMasterDTO vendorMasterDTO, HttpServletRequest request)
			throws Exception {
		ResponseEntity response = new ResponseEntity();
		
		ParamEntity paramEntity = paramRepository.findByCode("VENDOR_ID");
		vendorMasterDTO.setVendorId(paramService.getVendorIdDescpValue());
		paramService.updateVendorIdDescp2Value(paramEntity);
		return vendorMasterService.saveVendorMaster(vendorMasterDTO, request);
	}

	@GetMapping("/getAllVendors")
	public ResponseEntity getAllVendors(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) throws Exception {
	    return vendorMasterService.getAllVendorDetails(page, size);
	}

	@GetMapping("/getAllActiveVendors")
	public ResponseEntity getAllActiveVendors() throws Exception {
		return vendorMasterService.getAllActiveVendorDetails();
	}

	@GetMapping("/getVendorById")
	public ResponseEntity getVendorById(@RequestParam String vendorId) throws Exception {
		return vendorMasterService.getVendorMasterById(vendorId);
	}

	@PutMapping("/deactivateVendor")
	public ResponseEntity deactivateVendor(@RequestParam String vendorId) throws Exception {
		return vendorMasterService.deactivateVendorByVendorId(vendorId);
	}
	
	@PutMapping("/activateVendor")
	public ResponseEntity activateVendor(@RequestParam String vendorId) throws Exception {
		return vendorMasterService.activateVendorByVendorId(vendorId);
	}

	@PostMapping("/updateVendor")
	public ResponseEntity updateVendor(@RequestBody VendorMasterDTO vendorMasterDTO, HttpServletRequest request)
			throws Exception {
		vendorMasterDTO.setUpdatedAt(new Date());
		return vendorMasterService.updateVendorMaster(vendorMasterDTO, request);
	}

	@DeleteMapping("/deleteVendor")
	public ResponseEntity deleteVendor(@RequestParam String vendorId) throws Exception {
		return vendorMasterService.deleteVendorByVendorId(vendorId);
	}
	
	@GetMapping("/getVendorByName")
	public ResponseEntity getVendorByName(@RequestParam String vendorName) throws Exception {
		return vendorMasterService.getVendorMasterByName(vendorName);
	}
	
	@GetMapping("/check-existence")
	public ResponseEntity checkExistence(
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String mobile,
	        @RequestParam(required = false) String vendorName) {
	    return vendorMasterService.checkExistence(email, mobile, vendorName);
	}
	@GetMapping("/getAllVendorDetail")
	public ResponseEntity getAllVendorDetail() throws Exception {
		return vendorMasterService.getAllVendorDetail();
	}
	
    @GetMapping("/getAllActiveVendorsDetails")
	public ResponseEntity getAllActiveVendorsDetails() {
		return vendorMasterService.getAllActiveVendorsDetails();
	}
}
