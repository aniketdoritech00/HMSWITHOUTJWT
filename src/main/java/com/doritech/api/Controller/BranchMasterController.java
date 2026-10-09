package com.doritech.api.Controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.BranchMasterDTO;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.BranchMasterService;
import com.doritech.api.Service.ParamService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@RestController
//@RequestMapping("/branch")
public class BranchMasterController {
	@Autowired
	private BranchMasterService branchMasterService;
	@Autowired 
	ParamService paramService;

	@GetMapping("getAllConnectedBranches")
	public ResponseEntity getAllConnectedBranches(String branchCode) {
		return branchMasterService.getAllConnectedBranches(branchCode);
	}

	/**
	 * POST API FOR ADD_BRANCH_MASTER_DETAILS
	 */
	@PostMapping("addBranchMaster")
	@ResponseBody
	public ResponseEntity addBranchMasterInformation(@RequestBody BranchMasterDTO branchMasterDTO, HttpSession session)
			throws Exception {
		System.out.println("Aakash");
		return branchMasterService.addOrUpdateBranch(branchMasterDTO, session);
	}

	@PostMapping("editBranch")
	@ResponseBody
	public ResponseEntity editBranch(@RequestBody BranchMasterDTO branchRequest, HttpSession session) {
		return branchMasterService.editBranch(branchRequest, session);
	}

	/**
	 * GET API TO FETCH BRANCH_MASTER_DETAILS BY PARENT CODE
	 */
	@PostMapping("/getBranchByParentCode")
	@ResponseBody
	public ResponseEntity getBranchByParentCode(@RequestParam String parentCode) throws Exception {

		return branchMasterService.getBranchByParentCode(parentCode);
	}

	/**
	 * GET API TO FETCH ALL BRANCH_MASTER_DETAILS
	 */
	@GetMapping("/getAllBranch")
	@ResponseBody
	public ResponseEntity getAllBranch() throws Exception {

		return branchMasterService.getAllBranch();
	}

	@PostMapping("updateActiveStatus")
	@ResponseBody
	public ResponseEntity updateActiveStatus(@RequestParam String branchCode) {
		return branchMasterService.updateActiveStatus(branchCode);
	}

	@PostMapping("deactivateBranch")
	@ResponseBody
	public ResponseEntity deactivateBranch(@RequestParam String branchCode) {
		return branchMasterService.deactivateBranch(branchCode);
	}

	/**
	 * GET API TO FETCH BRANCH_MASTER_DETAILS BY Branch CODE
	 */
	@GetMapping("/getBranchByBranchCode")
	@ResponseBody
	public ResponseEntity getBranchByBranchCode(@RequestParam String branchCode) throws Exception {

		return branchMasterService.getBranchByBranchCode(branchCode);
	}

	/**
	 * GET API TO FETCH ALL BRANCH_MASTER_DETAILS IN HIERARCHY
	 */
//	@GetMapping("getAllBranchForHierarchy")
//	@ResponseBody
//	public ResponseEntity getAllBranchForHierarchy() throws Exception {
//
//		return branchMasterService.getAllBranchForHierarchy();
//	}

	@GetMapping("getAllBranchForHierarchy")
	@ResponseBody
	public ResponseEntity getAllBranchForHierarchy(@RequestParam String loggedInBranch) throws Exception {

		return branchMasterService.getAllBranchForHierarchy(loggedInBranch);
	}

	@GetMapping("getAllHeadOffice")
	@ResponseBody
	public ResponseEntity getAllHeadOffice() {
		return branchMasterService.getAllHeadOffice();
	}

	@GetMapping("getAllHeadOfficeAndZones")
	@ResponseBody
	public ResponseEntity getAllHeadOfficeAndZones(String branchType, String bank) { 
		return branchMasterService.getAllHeadOfficeAndZones(branchType, bank);
	}

	@GetMapping("getAllChildren")
	@ResponseBody
	public ResponseEntity getAllChildren(String branchCode) {
		return branchMasterService.getAllChildren(branchCode);
	}

	@GetMapping("findByBranchCodeContainingPattern")
	@ResponseBody
	public ResponseEntity findByBranchCodeContainingPattern(String branchCode) throws Exception {
		return branchMasterService.findByBranchCodeContainingPattern(branchCode);
	}

	@GetMapping("getBranchMasterByStatus")
	@ResponseBody
	public ResponseEntity getBranchMasterByStatus(String status) {
		return branchMasterService.getBranchMasterByStatus(status);
	}

	@GetMapping("getBranchMasterByBranchCode")
	@ResponseBody
	public ResponseEntity getBranchMasterByBranchCode(String branchCode) {
		return branchMasterService.getBranchMasterByBranchCode(branchCode);
	}

	@GetMapping("findByBranchCodeContainingPatternForZone")
	@ResponseBody
	public ResponseEntity findByBranchCodeContainingPatternForZone(String branchCode) throws Exception {
		return branchMasterService.findByBranchCodeContainingPatternForZone(branchCode);
	}

	@GetMapping("findByBranchCodeContainingPatternForZoneWithStatus")
	@ResponseBody
	public ResponseEntity findByBranchCodeContainingPatternForZoneWithStatus(String branchCode, String status)
			throws Exception {
		return branchMasterService.findByBranchCodeContainingPatternForZone(branchCode, status);
	}

	@GetMapping("getAllZones")
	@ResponseBody
	public ResponseEntity getAllZones() {
		return branchMasterService.getAllZones();
	}

	@GetMapping("getAllBranchesAndDevicesbyBranchCode")
	public ResponseEntity getAllBranchesAndDevicesbyBranchCode(@RequestParam String branchCode) {
		return branchMasterService.getAllBranchesAndDevicesbyBranchCode(branchCode);
	}

	@DeleteMapping("deleteBranchesbyBranchCode")
	public String deleteBranchInformation(@RequestParam String branchCode) throws Exception {
		return branchMasterService.deleteBranchInformation(branchCode);

	}
	

	@GetMapping("getAllDetailsByBranchCodeForReport")
	public ResponseEntity getAllDetailsByBranchCode(@RequestParam String branchCode) {
		return branchMasterService.getAllDetailsByBranchCode(branchCode);
	}

//	@GetMapping("getBranchesByBranchCodes")
//	public ResponseEntity getBranchesByBranchCodes(@RequestParam String branchCode) {
//	 return branchMasterService.getBranchesByBranchCodes(branchCode);	
//	}
	
	@GetMapping("getAllZonesByBranchCode")
	public ResponseEntity getAllZonesByBranchCode(@RequestParam String branchCode) {
		return branchMasterService.getAllZonesByBranchCode(branchCode);
	}
	
	@GetMapping("getHeadOfficeByBranchCode")
	public ResponseEntity getHeadOfficeByBranchCode(String branchCode) {
		return branchMasterService.getHeadOfficeByBranchCode(branchCode);
	}
	@GetMapping("/getBranchDetailsByVendorId")
	public ResponseEntity getBranchDetailsByVendorId(@RequestParam String vendorId) throws Exception {
		return branchMasterService.getBranchDetailsByVendorId(vendorId);
	}
		
	@GetMapping("/updateIfscForSuperHO")
	public String updateIfscForSuperHO() {
		return paramService.updateIfscForSuperHO();
		
	}
	
	@GetMapping("getAllSuperHO")
	public ResponseEntity getAllSuperHO() {
		return branchMasterService.getAllSuperHO();
	}
	@GetMapping("getAllSuperHOAndZones")
	public ResponseEntity getAllSuperHOAndZones() {
		return branchMasterService.getAllSuperHOAndZones();
	}
	
	@GetMapping("getAllBranchByBranchType")
	public ResponseEntity getAllBranchByBranchType(@RequestParam String branchType ,@RequestParam String branchCode) {
		return branchMasterService.getAllBranchByBranchType(branchType ,branchCode);
	}
	@GetMapping("/branch-dropdown")
	public ResponseEntity getBranchDropdown(@RequestParam String branchCode) {
	    return branchMasterService.getBranchDropdown(branchCode);
	}
	
}