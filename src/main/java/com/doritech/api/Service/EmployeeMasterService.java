package com.doritech.api.Service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Common.General;
import com.doritech.api.Common.Message;
import com.doritech.api.DTO.EmployeeMasterDTO;
import com.doritech.api.DTO.MenuMasterDTO;
import com.doritech.api.Entity.AutoRefreshConfigEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.EmployeeMasterEntity;
import com.doritech.api.Entity.GroupMasterEntity;
import com.doritech.api.Entity.LoginLog;
import com.doritech.api.Entity.MenuAccessEntity;
import com.doritech.api.Entity.MenuMasterEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.UserSessionRegistry;
import com.doritech.api.Repository.AutoRefreshConfigRepository;
import com.doritech.api.Repository.BankMasterRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Repository.GroupMasterRepository;
import com.doritech.api.Repository.LoginLogRepository;
import com.doritech.api.Repository.MenuAccessRepository;
import com.doritech.api.Repository.MenuMasterRepository;
import com.doritech.api.Repository.ParamRepository;

@Service
public class EmployeeMasterService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeMasterService.class);
	@Autowired
	EmployeeMasterRepository empMasterRepo;
	@Autowired
	MenuAccessRepository menuAccessRepository;
	@Autowired
	MenuMasterRepository menuMasterRepository;
	@Autowired
	ParamService paramService;
	@Autowired
	ParamRepository paramRepository;
	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	BranchMasterService branchMasterService;
	@Autowired
	GroupMasterRepository groupMasterRepository;

	@Autowired
	UserSessionRegistry userSessionRegistry;

	@Autowired
	BankMasterRepository bankMasterRepository;
	@Autowired
	private MailService mailService;
	@Autowired
	LoginLogRepository loginLogRepository;

	@Autowired
	AutoRefreshConfigRepository autoRefreshConfigRepository;

	@Autowired
	General general;
	/**
	 * SEND OTP FOR PASSWORD CHANGE
	 */
	Random random = new Random();

	/**
	 * USER LOGIN API
	 */
	public ResponseEntity employeeLogin(EmployeeMasterDTO employeeMasterDTO, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		try {
			// Check license date
			String dateString = "2026-11-30";
			LocalDate localDate = LocalDate.now();
			Date currentDate = java.sql.Date.valueOf(localDate);
			Date loginDate = null;

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try {
				loginDate = dateFormat.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (currentDate.after(loginDate)) {
				response.setMessage("Your license has expired");
				response.setStatusCode(HttpStatus.LOCKED.value());
				return response;
			}

			if (employeeMasterDTO.getEmail().isEmpty() || employeeMasterDTO.getPassword().isEmpty()) {
				logger.debug("Either Email Id or Password is empty.");
				response.setMessage("Either Emp Role or Email Id or Password is empty.");
				response.setStatusCode(HttpStatus.NO_CONTENT.value());
				return response;
			}

			EmployeeMasterEntity employee = empMasterRepo.findByEmail(employeeMasterDTO.getEmail());
			if (employee == null) {
				logger.debug("Provided email id does not match any email id in the database");
				response.setMessage("Provided email id does not match any email id in the database");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			if ("0".equals(employee.getLoginFlag())) {
				response.setMessage("Employee needs to change the password first.");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(employee);
				return response;
			}

			if (!"Active".equalsIgnoreCase(employee.getStatus())) {
				logger.debug("Employee is not active");
				response.setMessage("Employee is not active");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

//	        if (!employeeMasterDTO.getEmpRole().equalsIgnoreCase(employee.getEmpRole())) {
//	            logger.debug("User Role not matched.");
//	            response.setMessage("User Role not matched.");
//	            response.setStatusCode(HttpStatus.CONFLICT.value());
//	            return response;
//	        }

			if (!employeeMasterDTO.getPassword().equals(employee.getPassword())) {
				logger.debug("Password does not match.");
				response.setMessage("Password does not match.");
				response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
				return response;
			}

			BranchMasterEntity branch = branchMasterRepository.findBybranchCode(employee.getBranch());
			GroupMasterEntity group = groupMasterRepository.findByGroupId(employee.getGroupId());
			if (branch != null) {

				session.setAttribute("branchName", branch.getBranchDesc());

				String bankCode = branch.getBank();

				if (bankCode != null && !bankCode.trim().isEmpty()) {

					bankMasterRepository.findByBankCode(bankCode).ifPresent(bank -> {

						employeeMasterDTO.setBankName(bank.getBankName());

						session.setAttribute("bankName", bank.getBankName());

					});
				}
			}

			session.setAttribute("groupName", group.getGroupName());

			session.setAttribute("loggedInUser", employee.getEmpName());
			session.setAttribute("empId", employee.getEmpId());
			session.setAttribute("branchId", employee.getBranch());
			session.setAttribute("empRole", employee.getEmpRole());
			session.setAttribute("email", employee.getEmail());
			// session.setAttribute("empPan", employee.getEmpPan());
			session.setAttribute("mobile", employee.getMobile());
			session.setAttribute("address", employee.getAddress());
			session.setAttribute("city", employee.getCity());
			session.setAttribute("state", employee.getState());
			session.setAttribute("country", employee.getCountry());
			session.setAttribute("pin", employee.getPin());
			session.setAttribute("groupId", employee.getGroupId());
			session.setAttribute("password", "");

			// Update Active Login Flag
			employee.setActiveLoginFlag("Y");
			empMasterRepo.save(employee);

			BeanUtils.copyProperties(employee, employeeMasterDTO);
			employeeMasterDTO.setPassword("");
			logger.info("Authentication Successful");

			response.setPayload(employeeMasterDTO);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Authentication Successful");

			// After successful login
			LoginLog loginLog = new LoginLog();
			loginLog.setEmpId(employee.getEmpId());
			loginLog.setEmpName(employee.getEmpName());
			loginLog.setEmail(employee.getEmail());
			loginLog.setBranch(branch != null ? branch.getBranchDesc() : null);
			loginLog.setLoginTime(LocalDateTime.now());
			loginLogRepository.save(loginLog);

			// Store loginLog id in session for logout tracking

			session.setAttribute("loginLogId", loginLog.getId());
			System.out.println("loginLogId-->>" + session.getAttribute("loginLogId"));
			// After successful login
			userSessionRegistry.registerSession(employee.getEmail(), session);

		} catch (Exception e) {
			logger.error("Error in Login", e);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Login failed: " + e.getLocalizedMessage());
		}

		return response;
	}

	public ResponseEntity logoutByEmail(String email) {
		ResponseEntity response = new ResponseEntity();
		try {
			System.out.println("Logout requested for email: " + email);

			// Find employee by email
			EmployeeMasterEntity employee = empMasterRepo.findByEmail(email);
			if (employee == null) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("No employee found with email: " + email);
				return response;
			}

			// Find the latest login log for this employee
			Optional<LoginLog> loginLogOptional = loginLogRepository.findTopByEmailOrderByLoginTimeDesc(email);
			if (!loginLogOptional.isPresent()) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("No login record found for email: " + email);
				return response;
			}

			LoginLog loginLog = loginLogOptional.get();
			// Update logout time and duration
			loginLog.setLogoutTime(LocalDateTime.now());
			if (loginLog.getLoginTime() != null) {
				Duration duration = Duration.between(loginLog.getLoginTime(), loginLog.getLogoutTime());
				long hours = duration.toHours();
				long minutes = duration.toMinutes() % 60;
				long seconds = duration.getSeconds() % 60;
				String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				loginLog.setDuration(formattedDuration);
			}
			loginLogRepository.save(loginLog);

			// Update employee active flag
			employee.setActiveLoginFlag("N");
			empMasterRepo.save(employee);

			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Logout successful for " + email);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Logout failed: " + e.getLocalizedMessage());
		}
		return response;
	}

	public ResponseEntity sendOTP(String email, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		try {
			EmployeeMasterEntity employee = empMasterRepo.findByEmail(email);
			System.out.println("employee-->>" + employee);

			if (employee == null) {
				response.setPayload(email);
				response.setMessage("Email ID is not present in the database!");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			String empEmail = employee.getEmail();
			if (empEmail == null || empEmail.isBlank()) {
				response.setMessage("No email address registered for this employee.");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

			session.setAttribute("email", empEmail);

			int otp = random.nextInt(9000) + 1000; // ✅ always 4 digits (1000–9999)

			String subject = "OTP from Digitals";
			String message = "<div style='border:1px solid #e2e2e2;padding:20px'>" + "Your One Time Password: " + "<b>"
					+ otp + "</b>" + "</div>";

			boolean flag = mailService.sendOTPToEmail(subject, message, empEmail); // ✅ instance call

			if (flag) {
				response.setPayload(otp);
				response.setMessage("OTP sent successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Failed to send OTP. Please try again.");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error sending OTP: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	/**
	 * VERIFY OTP
	 */
	public ResponseEntity verifyOtp(Integer otps, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		try {
			Integer myOtp = (Integer) session.getAttribute("otp");
			String email = (String) session.getAttribute("email");
			System.out.println("from frontend" + otps);
			System.out.println("from session" + myOtp);

			if (myOtp.equals(otps)) {
//                return "changePassword";
				response.setMessage("OTP verified successfully!");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				session.setAttribute("message", "You have entered the wrong OTP!..");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setMessage("You have entered wrong OTP!");
//                return "verifyOTP";
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error verifying OTP");
		}
		return response;
	}

	/**
	 * UPDATE PASSWORD BY EMAIL
	 */
	public ResponseEntity updatePasswordByEmail(String email, String newPassword) {
		ResponseEntity response = new ResponseEntity();
		try {
			EmployeeMasterEntity employee = empMasterRepo.findByEmail(email);
			if (employee != null) {
				employee.setPassword(newPassword);
				employee.setLoginFlag("1");
				empMasterRepo.save(employee);

				response.setPayload(true);
				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("Password updated successfully");
			} else {
				response.setPayload(false);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Email ID is not present in database. Please try again");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error in updating password. Please Try again !");
		}
		return response;
	}

	/**
	 * SAVE OR EDIT THE EMPLOYEE
	 */
	public ResponseEntity saveOrUpdateEmployee(EmployeeMasterDTO employeeMasterDTO, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDate = date.format(formatter);

		String loggedInUserEmpId = (String) session.getAttribute("empId");
		try {
			List<Message> validationMessages = validations(employeeMasterDTO);
			if (validationMessages.isEmpty()) {
				Optional<EmployeeMasterEntity> existingEmployee = empMasterRepo.findById(employeeMasterDTO.getEmpId());
				String empId = "";
				if (!existingEmployee.isPresent()) {
					EmployeeMasterEntity employee = new EmployeeMasterEntity();
					ParamEntity paramEntity = paramRepository.findByCode("EMPLOYEE_ID");
					System.out.println(paramEntity);

					employeeMasterDTO.setEmpId(paramService.getEmployeeDescpValue());
					BeanUtils.copyProperties(employeeMasterDTO, employee);
					employee.setPassword(employee.getMobile());
					employee.setStatus("Active");
					employee.setLoginFlag("0");
					employee.setCreatedDate(formattedDate);
					// employee.setUpdatedDate(formattedDate);
					employee.setCount(1);
					employee.setUpdationStatus("Save");
					employee.setCreatedBy(loggedInUserEmpId);
					empMasterRepo.save(employee);
					paramService.updateEmployeeDescp2Value(paramEntity);

					response.setPayload(employee);
					response.setMessage("New employee added successfully.");
					response.setStatusCode(HttpStatus.OK.value());
					logger.info("New employee added successfully with code: {}", employee.getEmpId());
				} else {

					EmployeeMasterEntity entity = existingEmployee.get();
					String existingPassword = entity.getPassword();
					String existingLoginFlag = entity.getLoginFlag();

					BeanUtils.copyProperties(employeeMasterDTO, entity);
					entity.setStatus("Active");
					entity.setPassword(existingPassword);
					entity.setLoginFlag(existingLoginFlag);
//					
//					EmployeeMasterEntity entity = existingEmployee.get();
//					BeanUtils.copyProperties(employeeMasterDTO, entity);
//					entity.setStatus("Active");
					entity.setUpdatedDate(formattedDate);
					int newCount = entity.getCount() + 1;
					entity.setCount(newCount);
					entity.setUpdationStatus("Edit");
					entity.setUpdatedBy(loggedInUserEmpId);
					empMasterRepo.save(entity);

					response.setPayload(entity);
					response.setMessage("Employee updated successfully.");
					response.setStatusCode(HttpStatus.OK.value());
					logger.info("Employee updated successfully with code: {}", entity.getEmpId());
				}
			} else {
				response.setMessage("Employee requested is Invalid.");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setPayload(validationMessages);
				logger.warn("Invalid request for employee with code: {}", employeeMasterDTO.getEmpId());
			}
		} catch (Exception e) {
			logger.error("Error processing employee data", e);
			response.setMessage("Employee can't be added in the database");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/**
	 * GET ALL EMPLOYEES
	 */
	public ResponseEntity getAllEmployees() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<EmployeeMasterEntity> employees = empMasterRepo.findAll();
			List<EmployeeMasterDTO> employeeMasterDTOList = new ArrayList<EmployeeMasterDTO>();
			for (EmployeeMasterEntity employee : employees) {
				EmployeeMasterDTO employeeMasterDTO = new EmployeeMasterDTO();
				employee.setPassword("");
				BeanUtils.copyProperties(employee, employeeMasterDTO);

				employeeMasterDTOList.add(employeeMasterDTO);
			}
			response.setPayload(employeeMasterDTOList);
			response.setMessage("Employees retrieved successfully.");
			response.setStatusCode(HttpStatus.OK.value());
			logger.info("Employees retrieved successfully.");
		} catch (Exception e) {
			logger.error("Error processing employee data", e);
			response.setMessage("Employees can't be retrieved from the database");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/**
	 * GET MENU DETAILS FROM EMPLOYEE ID
	 */
	public ResponseEntity getMenuAssignedBasedOnEmpId(String empId) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<MenuMasterDTO> menuMasterList = new ArrayList<>();
			String empRole = empMasterRepo.findByEmpId(empId).getEmpRole();
			if (general.checkNull(empRole) != null) {
				// Fetching MenuId from Role Master Entity
				List<MenuAccessEntity> userRolesList = menuAccessRepository.findByRoleName(empRole);

				userRolesList.forEach(x -> {
					MenuMasterEntity menuMaster = menuMasterRepository.getById(x.getMenuId());
					MenuMasterDTO menuDto = new MenuMasterDTO();
					BeanUtils.copyProperties(menuMaster, menuDto);
					menuMasterList.add(menuDto);
				});
			}
			response.setPayload(menuMasterList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Menu Details based on user ID fetched successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			response.setMessage("Failed to get Menu Details based on User ID: " + e.getLocalizedMessage());
		}

		return response;
	}

	/**
	 * GET MENU DETAILS BY EMPLOYEE ROLE TYPE
	 */
	public ResponseEntity getMenuDetailsByRoleType(String empRole) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			HashMap<String, List<MenuMasterDTO>> roleAndMenu = new HashMap<>();
			if (general.checkNull(empRole) != null) {
				// Fetching MenuId from Role Master Entity
				List<MenuAccessEntity> employeeRolesList = menuAccessRepository.findByRoleName(empRole);
				List<MenuMasterDTO> menuMasterList = new ArrayList<>();

				employeeRolesList.forEach(role -> {
					MenuMasterEntity menuMaster = menuMasterRepository.getById(role.getMenuId());
					MenuMasterDTO menuDto = new MenuMasterDTO();
					BeanUtils.copyProperties(menuMaster, menuDto);
					menuMasterList.add(menuDto);
				});

				roleAndMenu.put(empRole, menuMasterList);

				response.setPayload(roleAndMenu);
				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("User Type and Roles fetched successfully.");
			} else {
				response.setMessage("User Type not found: " + empRole);
				response.setStatusCode(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			response.setMessage("User Role fetching failed: " + e.getLocalizedMessage());
		}
		return response;
	}

	/**
	 * VALIDATIONS FOR SAVING EMPLOYEE
	 */
	public List<Message> validations(EmployeeMasterDTO employeeMasterDTO) {
		List<Message> messageList = new ArrayList<>();

		if (StringUtils.isEmpty(employeeMasterDTO.getEmpRole()) || employeeMasterDTO.getEmpRole() == null) {
			messageList.add(new Message("Employee Role", "Employee Roll can't be null or empty"));
		}

		if (StringUtils.isEmpty(employeeMasterDTO.getEmpName()) || employeeMasterDTO.getEmpName() == null) {
			messageList.add(new Message("Employee Name", "Employee Name can't be null or empty"));
		}

		if (StringUtils.isEmpty(employeeMasterDTO.getAddress()) || employeeMasterDTO.getAddress() == null) {
			messageList.add(new Message("Address", "Address can't be null or empty"));
		}

		if (StringUtils.isEmpty(employeeMasterDTO.getCity()) || employeeMasterDTO.getCity() == null) {
			messageList.add(new Message("City", "City can't be null or empty"));
		}

		if (StringUtils.isEmpty(employeeMasterDTO.getPin()) || employeeMasterDTO.getPin() == null) {
			messageList.add(new Message("Pin Code", "Pin Code can't be null or empty"));
		}

		if (StringUtils.isEmpty(employeeMasterDTO.getState()) || employeeMasterDTO.getState() == null) {
			messageList.add(new Message("State", "State can't be null or empty"));
		}

		if (StringUtils.isEmpty(employeeMasterDTO.getCountry()) || employeeMasterDTO.getCountry() == null) {
			messageList.add(new Message("Country", "Country can't be null or empty"));
		}

		Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
		if (employeeMasterDTO.getEmail() == null || !emailPattern.matcher(employeeMasterDTO.getEmail()).matches()) {
			messageList.add(new Message("Email ID", "Invalid Email ID"));
		}

		if (StringUtils.isEmpty(employeeMasterDTO.getBranch()) || employeeMasterDTO.getBranch() == null) {
			messageList.add(new Message("Branch", "Branch can't be null or empty"));
		}
		return messageList;
	}

	public ResponseEntity getEmployeeByGroupId(String groupId) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<EmployeeMasterDTO> responseList = new ArrayList<>();
			List<EmployeeMasterEntity> employeeMaster = empMasterRepo.findByGroupId(groupId);
			if (!employeeMaster.isEmpty()) {
				for (EmployeeMasterEntity entity : employeeMaster) {
					EmployeeMasterDTO dto = new EmployeeMasterDTO();
					BeanUtils.copyProperties(entity, dto);
					dto.setEmpId(entity.getEmpId());
					responseList.add(dto);
				}
				response.setPayload(responseList);
				response.setMessage("Employee Master data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
				logger.info("Employee Master data fetched successfully.", responseList.size());
			} else {
				response.setMessage("Employee Master data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				logger.warn("Employee Master data not found");
			}
		} catch (Exception e) {
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error("Error fetching Employee Master data", e);
		}
		return response;
	}

	public ResponseEntity getEmployeeByEmpId(String empId) {
		ResponseEntity response = new ResponseEntity();
		try {
			Optional<EmployeeMasterEntity> optional = empMasterRepo.findById(empId);
			if (optional.isPresent()) {
				EmployeeMasterEntity employeeMaster = optional.get();

				EmployeeMasterDTO dto = new EmployeeMasterDTO();
				BeanUtils.copyProperties(employeeMaster, dto);
//  				    dto.setEmpCode(entity.getEmpId());

				response.setPayload(dto);
				response.setMessage("Employee Master data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
				logger.info("Employee Master data fetched successfully.", response);
			} else {
				response.setMessage("Employee Master data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				logger.warn("Employee Master data not found");
			}
		} catch (Exception e) {
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			logger.error("Error fetching Employee Master data", e);
		}
		return response;
	}

	public ResponseEntity getAllMenuDetailsBasedOnEmpId(String empId) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			if (empId != null) {
				String empRole = empMasterRepo.findByEmpId(empId).getEmpRole();
				List<MenuAccessEntity> menuAccessEntities = menuAccessRepository.findByRoleName(empRole);

				List<MenuMasterEntity> menuMaster = menuMasterRepository.findAll();
				List<MenuMasterEntity> responseList = new ArrayList<>();

				for (MenuAccessEntity menu : menuAccessEntities) {
					for (MenuMasterEntity master : menuMaster) {
						if (menu.getMenuId() == master.getId()) {
							responseList.add(master);
						} else {
							continue;
						}
					}
				}
				response.setPayload(responseList);
				response.setMessage("All menu details have been fetched");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				response.setMessage("Provided empId is not present in the database");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error fetching all menu details");
		}

		return response;
	}

	public ResponseEntity changeUserPassword(String empId) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			EmployeeMasterEntity employee = empMasterRepo.findByEmpId(empId);
			if (employee != null) {
				if (employee.getLoginFlag().equals("0")) {

				} else {
					response.setMessage("");
				}
			} else {
				response.setMessage("Employee is not present in database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error is Setting Password");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllEmployeesOfBranch(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<EmployeeMasterEntity> employeeList = new ArrayList<>();

			ResponseEntity responseList = branchMasterService.getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseList.getPayload();

			if (!branchList.isEmpty()) {

				for (BranchMasterEntity branch : branchList) {
					List<EmployeeMasterEntity> employees = empMasterRepo.findByBranch(branch.getBranchCode());
					employeeList.addAll(employees);
				}
			}

			response.setPayload(employeeList);
			response.setMessage("Employees retrieved successfully.");
			response.setStatusCode(HttpStatus.OK.value());
			logger.info("Employees retrieved successfully.");
		} catch (Exception e) {
			logger.error("Error processing employee data", e);
			response.setMessage("Employees can't be retrieved from the database");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public boolean emailExists(String email) {
		return empMasterRepo.existsByEmail(email);
	}

	public ResponseEntity updateBranchId(String branchId, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		try {
			BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchId);

			if (branch == null) {
				response.setMessage("Invalid branch ID: Branch not found.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			session.setAttribute("branchId", branchId);
			session.setAttribute("branchName", branch.getBranchDesc());

			response.setMessage("Branch ID updated successfully in session.");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(Map.of("branchId", branchId, "branchName", branch.getBranchDesc()));
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Failed to update branch ID: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity deleteEmployee(String empId) {
		ResponseEntity response = new ResponseEntity();

		EmployeeMasterEntity employeeMasterEntity = empMasterRepo.findByEmpId(empId);

		if (employeeMasterEntity == null) {
			response.setMessage("No employee found for this emp id :" + empId);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {
			empMasterRepo.deleteById(empId);
			response.setMessage("employee by emp id :" + empId + " is deleted successfully");
			response.setStatusCode(HttpStatus.OK.value());

		}

		return response;
	}

	public ResponseEntity getEmployeeByActiveLoginFlag() {
		ResponseEntity response = new ResponseEntity();

		List<EmployeeMasterEntity> employeeMasterEntity = empMasterRepo.findByActiveLoginFlag("Y");

		if (employeeMasterEntity == null) {
			response.setMessage("No employee found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {
			response.setPayload(employeeMasterEntity);
			response.setMessage("employee fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		}

		return response;
	}

	public ResponseEntity changeLoginFlag(String empId) {
		ResponseEntity response = new ResponseEntity();

		EmployeeMasterEntity employeeMasterEntity = empMasterRepo.findByEmpId(empId);

		if (employeeMasterEntity == null) {
			response.setMessage("No employee found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		} else {
			employeeMasterEntity.setActiveLoginFlag("N");
			empMasterRepo.save(employeeMasterEntity);
			response.setPayload(employeeMasterEntity);
			response.setMessage("employee fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		}

		return response;
	}

	public ResponseEntity getStatus(String empId, String status) {

		ResponseEntity response = new ResponseEntity();

		try {

			if (empId == null || empId.trim().isEmpty()) {
				response.setMessage("Employee ID is required");
				response.setStatusCode(400);
				response.setPayload(null);
				return response;
			}

			if (status == null || status.trim().isEmpty()) {
				response.setMessage("Status is required");
				response.setStatusCode(400);
				response.setPayload(null);
				return response;
			}

			Optional<AutoRefreshConfigEntity> existing = autoRefreshConfigRepository.findByEmpId(empId);

			AutoRefreshConfigEntity entity;

			if (existing.isPresent()) {

				// Existing employee -> update with requested status
				entity = existing.get();
				entity.setStatus(status);

				entity = autoRefreshConfigRepository.save(entity);

				response.setMessage("Status updated successfully");
				response.setStatusCode(200);

			} else {

				// New employee -> first time status will always be N
				entity = new AutoRefreshConfigEntity();
				entity.setEmpId(empId);
				entity.setStatus("N");

				entity = autoRefreshConfigRepository.save(entity);

				response.setMessage("New employee added with default status N");
				response.setStatusCode(201);
			}

			response.setPayload(entity);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Error while fetching/updating status");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	public ResponseEntity getByEmpId(String empId) {

		ResponseEntity response = new ResponseEntity();

		try {

			if (empId == null || empId.trim().isEmpty()) {
				response.setMessage("Employee ID is required");
				response.setStatusCode(400);
				response.setPayload(null);
				return response;
			}

			Optional<AutoRefreshConfigEntity> existing = autoRefreshConfigRepository.findByEmpId(empId);

			if (existing.isPresent()) {

				response.setMessage("Data fetched successfully");
				response.setStatusCode(200);
				response.setPayload(existing.get());

			} else {

				response.setMessage("No data found for employee ID: " + empId);
				response.setStatusCode(404);
				response.setPayload(null);
			}

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Error while fetching data");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

}
