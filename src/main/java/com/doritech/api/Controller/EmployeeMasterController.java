package com.doritech.api.Controller;

import javax.servlet.http.HttpSession;

import org.hibernate.internal.util.beans.BeanInfoHelper.ReturningBeanInfoDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.EmployeeMasterDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.UserSessionRegistry;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Service.EmployeeMasterService;
import com.doritech.api.Service.ParamService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@RestController
@RequestMapping
public class EmployeeMasterController {

	@Autowired
	EmployeeMasterService employeeMasterService;
	@Autowired
	ParamService paramService;
	@Autowired
	ParamRepository paramRepository;
	@Autowired
	UserSessionRegistry userSessionRegistry;

	/**
	 * API TO LOGIN
	 */
	@PostMapping("/employeeLogin")
	@ResponseBody
	public ResponseEntity employeeLogin(@RequestBody EmployeeMasterDTO employeeMasterDTO, HttpSession session) {
		return employeeMasterService.employeeLogin(employeeMasterDTO, session);
	}

	/**
	 * UPDATE PASSWORD BY EMAIL
	 */

	@PutMapping("/updatePasswordByEmail")
	@ResponseBody
	public ResponseEntity updatePasswordByEmail(@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword) {
		return employeeMasterService.updatePasswordByEmail(email, newPassword);
	}

	/**
	 * API TO SAVE NEW EMPLOYEE OR UPDATE SOME ATTRIBUTES AND SAVE AS NEW EMPLOYEE
	 */
	@PostMapping("/saveOrUpdateEmployee")
	@ResponseBody
	public ResponseEntity saveOrUpdateEmployee(@RequestBody EmployeeMasterDTO employeeMasterDTO, HttpSession session)
			throws Exception {

		return employeeMasterService.saveOrUpdateEmployee(employeeMasterDTO, session);
	}

	/**
	 * API TO GET ROLES BY EMPLOYEE ROLE TYPE
	 */
	@PostMapping("/getMenuDetailsByRoleType")
	@ResponseBody
	public ResponseEntity getMenuDetailsByRoleType(@ModelAttribute @RequestBody String empRole) throws Exception {
		return employeeMasterService.getMenuDetailsByRoleType(empRole);
	}

	/**
	 * API TO GET ALL EMPLOYEES
	 */
	@GetMapping("/getAllEmployees")
	@ResponseBody
	public ResponseEntity getAllEmployees() throws Exception {
		return employeeMasterService.getAllEmployees();
	}

	/**
	 * API TO GET MENU DETAILS BY ID
	 */
	@GetMapping("/getMenuAssignedBasedOnEmpId")
	@ResponseBody
	public ResponseEntity getMenuAssignedBasedOnEmpId(@RequestParam("empId") String empId) throws Exception {
		return employeeMasterService.getMenuAssignedBasedOnEmpId(empId);
	}

	@GetMapping("/getEmployeeByGroupId")
	public ResponseEntity getEmployeeByGroupId(@RequestParam String groupId) {
		return employeeMasterService.getEmployeeByGroupId(groupId);
	}

	@GetMapping("/getEmployeeByEmpId")
	public ResponseEntity getEmployeeByEmpId(@RequestParam String empId) {
		return employeeMasterService.getEmployeeByEmpId(empId);
	}

	@GetMapping("/getAllMenuDetailsBasedOnEmpId")
	@ResponseBody
	public ResponseEntity getAllMenuDetailsBasedOnEmpId(@RequestParam String empId) throws Exception {
		return employeeMasterService.getAllMenuDetailsBasedOnEmpId(empId);
	}

	@GetMapping("/getAllEmployeesOfBranch")
	@ResponseBody
	public ResponseEntity getAllEmployeesOfBranch(String branchCode) {
		return employeeMasterService.getAllEmployeesOfBranch(branchCode);
	}

	@GetMapping("/checkEmailExists")
	public org.springframework.http.ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
		boolean exists = employeeMasterService.emailExists(email);
		return new org.springframework.http.ResponseEntity<>(exists, HttpStatus.OK);
	}

	@PostMapping("/updateBranchId")
	public ResponseEntity updateBranchId(@RequestParam("branchId") String branchId, HttpSession session) {
		return employeeMasterService.updateBranchId(branchId, session);
	}

	@DeleteMapping("deleteEmployee")
	public ResponseEntity deleteEmployee(@RequestParam String empId) {
		return employeeMasterService.deleteEmployee(empId);
	}

	@GetMapping("/getEmployeeByActiveLoginFlag")
	public ResponseEntity getEmployeeByActiveLoginFlag() {
		return employeeMasterService.getEmployeeByActiveLoginFlag();
	}

	@PostMapping("changeLoginFlag")
	public ResponseEntity changeLoginFlag(String empId) {
		return employeeMasterService.changeLoginFlag(empId);
	}

	@PostMapping("/logoutAll")
	public ResponseEntity logoutAll(@RequestParam String email) {
		ResponseEntity response = new ResponseEntity();
		try {
			userSessionRegistry.invalidateAllSessions(email);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("All sessions for " + email + " have been logged out.");
		} catch (Exception e) {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Failed to logout all sessions: " + e.getMessage());
		}
		return response;
	}

	@PostMapping("/logoutByEmail")
	public ResponseEntity logoutByEmail(@RequestParam("email") String email) {
		return employeeMasterService.logoutByEmail(email);
	}

	@GetMapping("/auto-refresh/saveOrUpdate")
	public ResponseEntity getStatus(@RequestParam("empId") String empId, @RequestParam("status") String status) {

		return employeeMasterService.getStatus(empId, status);
	}
	@GetMapping("/auto-refresh/getStatusByEmpId")
	public ResponseEntity getByEmpId(@RequestParam("empId") String empId) {

		return employeeMasterService.getByEmpId(empId);
	}
	

}
