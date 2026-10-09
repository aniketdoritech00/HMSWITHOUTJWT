//package com.doritech.api.Security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
//import org.springframework.security.web.savedrequest.NullRequestCache;
//
//import com.doritech.api.Service.EmployeeMasterService;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//	@Autowired
//	private EmployeeMasterService employeeMasterService;
//
//	@Autowired
//	private EncryptDecrypt encryptDecrypt;
//
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new PasswordEncoder() {
//			@Override
//			public String encode(CharSequence rawPassword) {
//				return encryptDecrypt.encrypt(rawPassword.toString());
//			}
//
//			@Override
//			public boolean matches(CharSequence rawPassword, String encodedPassword) {
//				String decryptedPassword = encryptDecrypt.decrypt(encodedPassword);
//				return rawPassword.toString().equals(decryptedPassword);
//			}
//		};
//	}
//
//	@Bean
//	public DaoAuthenticationProvider authenticationProvider() {
//		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//		authProvider.setUserDetailsService(employeeMasterService);
//		authProvider.setPasswordEncoder(passwordEncoder());
//		return authProvider;
//	}
//
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(authenticationProvider());
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable().authorizeRequests()
//				.antMatchers("/api/**", "/welcome", "/employeeLogin", "/deviceMaster", "/employeeMasterScreen",
//	            "/groupMasterScreen", "/alarmAuditLogs", "/branchMaster", "/menuAccessScreen", "/overDueScreen",
//	            "/allMasters", "/reports", "/deviceLogDetails", "/activeDeviceLogDetails", "/logDetails",
//	            "/utility", "/whatsAppConfiguration", "/hmsDashboard","/generateQr","/BranchQrReport","/QRCode","/generateQRCode", "/getStatusReport", "/manualSync",
//	            "/getAllAlarmAuditLogs", "/getStatusCountofOneDay", "/getLatestAuditRecord", "/getStatusCountForAll",
//	            "/getStatusCountByDate", "/getAllRecordsByDate", "/getStatusCountByMonth", "/getStatusCountByYear",
//	            "/getAllRecordsByMonth", "/getAllRecordsByYear", "/getStatusCountByActiveZones", "/getAllRecordsByActiveZone",
//	            "/getAllConnectedBranches", "/addBranchMaster", "/editBranch", "/getBranchByParentCode", "/getAllBranch",
//	            "/updateActiveStatus", "/deactivateBranch", "/getBranchByBranchCode", "/getAllBranchForHierarchy",
//	            "/getAllHeadOffice", "/getAllHeadOfficeAndZones", "/getAllChildren", "/findByBranchCodeContainingPattern",
//	            "/getBranchMasterByStatus", "/getBranchMasterByBranchCode", "/findByBranchCodeContainingPatternForZone",
//	            "/findByBranchCodeContainingPatternForZoneWithStatus", "/getAllZones", "/addDeviceMaster",
//	            "/editDeviceMaster", "/getAllDevicesByBranchCode", "/getDeviceAndZoneDetailsByDeviceId",
//	            "/getAllDevicesAndSmsDetailsByBranchCode", "/getDeviceCountByBranchCode", "/getDeviceLogDetails",
//	            "/getActiveDeviceLogDetails", "/deleteDeviceMasterByDeviceId", "/getDeviceMasterByDeviceId",
//	            "/checkBranch", "/getCountOfZonesByType", "/getDeviceDetailsByBranchCode", "/getAllDeviceMaster",
//	            "/getInputStatusDetails", "/addMultipleDeviceZoneDetails", "/editMultipleDeviceZoneDetails",
//	            "/saveOrUpdateEmployee", "/getMenuDetailsByRoleType", "/getAllEmployees", "/getMenuAssignedBasedOnEmpId",
//	            "/getEmployeeByGroupId", "/getEmployeeByEmpId", "/getAllMenuDetailsBasedOnEmpId",
//	            "/importEmployeeMasterCSV", "/importEmployeeMaster", "/downloadEmployeesData",
//	            "/importBranchMasterCSV", "/importBranchMaster", "/sendOTP", "/verifyOTP", "/updatePasswordByEmail",
//	            "/saveOrUpdateGroupMaster", "/getGroupById", "/getAllGroupMaster", "/saveOrEditRoleMaster",
//	            "/getAllRoles", "/assignMenuDetailsToRole", "/revokeMenuDetailsFromTheRole", "/getMenuDetailsByRole",
//	            "/saveOrEditMenuDetails", "/getAllMenuDetails", "/Mail/check", "/getReadMailDetails",
//	            "/getAllSmsDetails", "/getSmsDetailsById", "/getActiveDevices", "/getNotSyncedDeviceCount",
//	            "/getIsolatedDevicesCount", "/getLatestRecord", "/inputStatusDetails","/sendSmsByBranchCode", "/getStatusReport",
//	            "/sendMailByBranchCode","/changePassword", "/encryptAndDecryptMessage", "/changeFlag", "/getCountOfWorkingDevices",
//	            "/getAllNotWorkingDevices","/saveAudit", "/resources/**",
//						"/registration**", "/js/", "/js/**", "/css/**", "/img/**", "/externalCSS/**", "/externalJS/**",
//						"/images/**", "/assets/**", "/audio/**", "/dashboardJS/**", "/dashboardCSS/**", "/logo/**",
//						"/partials/**", "/scss/**", "/vendor/**","/static/**","/RegistrationForm","/decrypt")
//				.permitAll().
//				antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//				.anyRequest().
//				authenticated().
//				and().
//				formLogin().
//				loginPage("/").
//				permitAll()
//				.successHandler(new SavedRequestAwareAuthenticationSuccessHandler()).
//				and().
//				logout().
//				logoutUrl("/logout")
//				.logoutSuccessUrl("/").
//				logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
//				.permitAll()
//				.and().
//				sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//				.requestCache()
//				.requestCache(new NullRequestCache())
//				.and().
//				headers().
//				frameOptions().
//				sameOrigin().
//				and()
//				.exceptionHandling().accessDeniedPage("/accessDenied");
//	}
//}
