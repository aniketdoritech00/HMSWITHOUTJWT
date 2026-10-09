package com.doritech.api.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.DTO.EmailSchedularDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.JspRenderer;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Service.AlertNotificationService;
import com.doritech.api.Service.BranchMasterService;
import com.doritech.api.Service.EmailSchedularService;
import com.doritech.api.Service.ReadMail;
import com.doritech.api.Service.SmsDetailsService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@RestController
public class EmailSchedularController {

	@Autowired
	EmailSchedularService emailSchedulerService;
	@Autowired
	SmsDetailsService smsDetailsService;
	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	DeviceMasterRepository deviceMasterRepository;
	@Autowired
	ReadMail readMail;
	@Autowired
	JspRenderer jspRenderer;
	@Autowired
	BranchMasterService branchMasterService;
	@Autowired
	SmsDetailsService smsService;
//	@Autowired
//	BranchReportsSchedulerService branchReportsSchedulerService;
	@Autowired
	AlertNotificationService alertNotificationService;

	@PostMapping("saveEmailSchedularService")
	public ResponseEntity<Object> saveEmailSchedularService(@RequestBody EmailSchedularDTO emailSchedularDTO) {
		return emailSchedulerService.saveEmailSchedularService(emailSchedularDTO);
	}

	@GetMapping("getEmailSchedulerByBranchIdAndTimePeriod")
	public ResponseEntity<Object> getEmailSchedulerByBranchIdAndTimePeriod(String branchId, String timePeriod) {
		return emailSchedulerService.getEmailSchedulerByBranchIdAndTimePeriod(branchId, timePeriod);
	}

	@GetMapping("getAllRecords")
	public com.doritech.api.Entity.ResponseEntity getAllRecords() {
		return emailSchedulerService.getAllRecords();
	}

	@PostMapping("/sendEmailWithAttachment")
	public com.doritech.api.Entity.ResponseEntity sendEmailWithAttachment(
			@RequestParam("attachments") MultipartFile[] attachments) {
		return emailSchedulerService.sendEmailWithAttachment(attachments);
	}

	@PostMapping("/send")
	public ResponseEntity<String> sendEmailWithImage(@RequestParam String to, @RequestParam String subject,
			@RequestParam String text, @RequestParam("image") MultipartFile image) {

		try {
			emailSchedulerService.sendEmailWithAttachment(null);
			return ResponseEntity.ok("Email sent successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
		}
	}

//	@Scheduled(cron = "0 0 11 * * ?")
//	public void send24HoursAlertReportDaily() {
//		List<BranchMasterEntity> branchMasterEntities = branchMasterRepository.findAll();
//		branchMasterEntities.forEach(item -> {
//			try {
//				smsDetailsService.getReportForAlertsMoreThan24Hours(item.getBranchCode());
//			} catch (MessagingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		});
//		System.out.println("24-hours alert report sent at 11:00 AM");
//	}

//	 @Scheduled(cron = "0 0 11 * * ?")
//	 public void sendStatusCommandToCMSDevices() {
//		 
//		 List<DeviceMasterEntity> deviceMasterEntities=deviceMasterRepository.findByProductType("CMS");
//		 if(!deviceMasterEntities.isEmpty()) {
//			deviceMasterEntities.forEach(item->{
//				readMail.hitUrlForStatus(item.getDeviceSerialNo());
//			}); 
//			 
//		 }
//		 
//	 }

//	@GetMapping("/sendBranchReports")
//	public String sendBranchReports(HttpServletRequest request, HttpServletResponse response) {
//		try {
//			com.doritech.api.Entity.ResponseEntity responseEntity = branchMasterService.getAllSuperHO();
//
//			@SuppressWarnings("unchecked")
//			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
//
//			for (BranchMasterEntity branch : branchList) {
//				String branchCode = branch.getBranchCode();
//				String branchName = branch.getBranchDesc();
//
//				List<String> emailList = new ArrayList<>();
//				addIfNotEmpty(emailList, branch.getEmail());
//				addIfNotEmpty(emailList, branch.getEmail2());
//				addIfNotEmpty(emailList, branch.getEmail3());
//				addIfNotEmpty(emailList, branch.getEmail4());
//				addIfNotEmpty(emailList, branch.getEmail5());
//				addIfNotEmpty(emailList, branch.getEmail6());
//
//				if (emailList.isEmpty())
//					continue;
//
//				// 👉 Set attributes exactly like branchReportMail does
//				request.setAttribute("branchCode", branchCode);
//				request.setAttribute("branchReport", smsService.getSystemWiseBranchReport(branchCode, "", ""));
//				request.setAttribute("uptimeReport", smsService.getUpTimeReport(branchCode));
//				request.setAttribute("cctvTatReport", smsService.getTATReport(branchCode, "CCTV"));
//				request.setAttribute("fasTatReport", smsService.getTATReport(branchCode, "FIRE ALARM"));
//				request.setAttribute("sasTatReport", smsService.getTATReport(branchCode, "SECURITY ALARM"));
//				request.setAttribute("bacsTatReport", smsService.getTATReport(branchCode, "BACS"));
//				request.setAttribute("etlTatReport", smsService.getTATReport(branchCode, "ETL"));
//
//				// Render JSP into HTML for this branch
//				String htmlContent = jspRenderer.renderJspToString("/WEB-INF/view/NewFile.jsp", request, response);
//
//				// Send mail to all branch emails
//				for (String email : emailList) {
//					emailSchedulerService.sendHtmlMail(email, "Branch Report - " + branchName + " (" + branchCode + ")",
//							htmlContent);
//				}
//			}
//
//			return "Mails sent successfully to all branches";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "Failed: " + e.getMessage();
//		}
//	}
//
//	private void addIfNotEmpty(List<String> list, String email) {
//		if (email != null && !email.trim().isEmpty()) {
//			list.add(email);
//		}
//	}
	
//	 @GetMapping("/sendBranchReports")
//	    public String sendBranchReportsManual() {
//	        return branchReportsSchedulerService.sendBranchReports();
//	    }
	 
	 @GetMapping("/process")
	    public ResponseEntity<String> processAllBranches() {
	        try {
	            alertNotificationService.scheduleProcessAndCheckAlarms();
	            return ResponseEntity.ok("✅ Alarm processing triggered for all SUPER HO branches.");
	        } catch (Exception e) {
	            return ResponseEntity.internalServerError()
	                    .body("❌ Error while processing alarms: " + e.getMessage());
	        }
	    }

}
