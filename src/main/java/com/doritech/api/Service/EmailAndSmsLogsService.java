package com.doritech.api.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.EmailLogEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SmsLogEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.SmsLogRepo;

@Service
public class EmailAndSmsLogsService {
	@Autowired
	private EmailLogRepo emailLogRepo;
	@Autowired
	private SmsLogRepo smsLogRepo;
	@Autowired
	BranchMasterService branchMasterService;

	@Autowired
	BranchMasterRepository branchMasterRepository;

	/*
	 * public com.doritech.api.Entity.ResponseEntity
	 * getAllEmailLogsByBranchCode(String branchCode) {
	 * com.doritech.api.Entity.ResponseEntity response = new
	 * com.doritech.api.Entity.ResponseEntity(); List<EmailLogEntity> responseList =
	 * new ArrayList<>();
	 * 
	 * try { ResponseEntity branchResponseEntity =
	 * branchMasterService.getAllChildren(branchCode);
	 * 
	 * if (branchResponseEntity == null || branchResponseEntity.getPayload() ==
	 * null) { response.setMessage("No branches found for this branch code: " +
	 * branchCode); response.setStatusCode(HttpStatus.NOT_FOUND.value()); return
	 * response; }
	 * 
	 * @SuppressWarnings("unchecked") List<BranchMasterEntity> branchList =
	 * (List<BranchMasterEntity>) branchResponseEntity.getPayload();
	 * 
	 * if (branchList == null || branchList.isEmpty()) {
	 * response.setMessage("No branches found for this branch code: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * 
	 * List<String> branchCodes = branchList.stream()
	 * .map(BranchMasterEntity::getBranchCode) .filter(Objects::nonNull)
	 * .collect(Collectors.toList());
	 * 
	 * for (String item : branchCodes) { List<EmailLogEntity> emailLogDetails =
	 * emailLogRepo.findByBranchCode(item); if (emailLogDetails != null &&
	 * !emailLogDetails.isEmpty()) { responseList.addAll(emailLogDetails); } }
	 * 
	 * if (responseList.isEmpty()) { response.
	 * setMessage("No email log details found for the given branch code(s).");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(Collections.emptyList()); } else {
	 * 
	 * responseList.sort(Comparator.comparing(EmailLogEntity::getDatetime).reversed(
	 * ));
	 * 
	 * response.setMessage("Email log details found successfully!");
	 * response.setPayload(responseList);
	 * response.setStatusCode(HttpStatus.OK.value()); } } catch (Exception e) {
	 * response.setMessage("Error: " + e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); }
	 * 
	 * return response; }
	 */

	public com.doritech.api.Entity.ResponseEntity getAllEmailLogsByBranchCode(String branchCode, int page, int size,
			boolean downloadExcel) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);

			if (branchResponseEntity == null || branchResponseEntity.getPayload() == null) {
				response.setMessage("No branches found for branch code : " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				response.setCurrentPage(0);
				response.setTotalElements(0);
				response.setTotalPages(0);
				return response;
			}

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

			if (branchList == null || branchList.isEmpty()) {
				response.setMessage("No branches found.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				response.setCurrentPage(0);
				response.setTotalElements(0);
				response.setTotalPages(0);
				return response;
			}

			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.filter(Objects::nonNull).collect(Collectors.toList());

			if (downloadExcel) {
				List<EmailLogEntity> emailLogs = emailLogRepo.findByBranchCodeInOrderByDatetimeDesc(branchCodes);

				if (emailLogs.isEmpty()) {
					response.setMessage("No emailLogs log details found for the given branch code(s).");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setPayload(null);
					response.setCurrentPage(0);
					response.setTotalElements(0);
					response.setTotalPages(0);
					return response;
				}

				emailLogs.forEach(email -> email.setBodyText("N/A"));
				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("Email log details found successfully.");
				response.setPayload(emailLogs);
				response.setCurrentPage(0);
				response.setTotalElements(emailLogs.size());
				response.setTotalPages(1);

			} else {
				Pageable pageable = PageRequest.of(page, size, Sort.by("datetime").descending());

				Page<EmailLogEntity> emailPage = emailLogRepo.findByBranchCodeIn(branchCodes, pageable);

				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("Email log details found successfully.");

				// Only List of records

				List<EmailLogEntity> content = emailPage.getContent();

				content.forEach(emailReponse -> {

					emailReponse.setBodyText("N/A");

				});
				response.setPayload(emailPage.getContent());

				// Pagination Details
				response.setCurrentPage(emailPage.getNumber());
				response.setTotalElements(emailPage.getTotalElements());
				response.setTotalPages(emailPage.getTotalPages());

			}
		} catch (Exception e) {

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage(e.getMessage());
			response.setPayload(Collections.emptyList());
			response.setCurrentPage(0);
			response.setTotalElements(0);
			response.setTotalPages(0);
		}

		return response;
	}

	/*
	 * public com.doritech.api.Entity.ResponseEntity
	 * getAllSmsLogReportByBranchCode(String branchCode) {
	 * com.doritech.api.Entity.ResponseEntity response = new
	 * com.doritech.api.Entity.ResponseEntity(); List<SmsLogEntity> responseList =
	 * new ArrayList<>();
	 * 
	 * try { ResponseEntity branchResponseEntity =
	 * branchMasterService.getAllChildren(branchCode);
	 * 
	 * if (branchResponseEntity == null || branchResponseEntity.getPayload() ==
	 * null) { response.setMessage("No branches found for this branch code: " +
	 * branchCode); response.setStatusCode(HttpStatus.NOT_FOUND.value()); return
	 * response; }
	 * 
	 * @SuppressWarnings("unchecked") List<BranchMasterEntity> branchList =
	 * (List<BranchMasterEntity>) branchResponseEntity.getPayload();
	 * 
	 * if (branchList == null || branchList.isEmpty()) {
	 * response.setMessage("No branches found for this branch code: " + branchCode);
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); return response; }
	 * 
	 * List<String> branchCodes =
	 * branchList.stream().map(BranchMasterEntity::getBranchCode)
	 * .filter(Objects::nonNull).collect(Collectors.toList());
	 * 
	 * for (String item : branchCodes) { List<SmsLogEntity> smsLogDetails =
	 * smsLogRepo.findByBranchCode(item); if (smsLogDetails != null &&
	 * !smsLogDetails.isEmpty()) { responseList.addAll(smsLogDetails); } }
	 * 
	 * if (responseList.isEmpty()) {
	 * response.setMessage("No SMS log details found for the given branch code(s)."
	 * ); response.setStatusCode(HttpStatus.NOT_FOUND.value());
	 * response.setPayload(Collections.emptyList()); } else {
	 * 
	 * responseList.sort(Comparator.comparing(SmsLogEntity::getSmsLogId).reversed())
	 * ; response.setMessage("SMS log details found successfully!");
	 * response.setPayload(responseList);
	 * response.setStatusCode(HttpStatus.OK.value()); } } catch (Exception e) {
	 * response.setMessage("Error: " + e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	 * response.setPayload(null); }
	 * 
	 * return response; }
	 */

	public com.doritech.api.Entity.ResponseEntity getAllSmsLogReportByBranchCode(String branchCode, int page, int size,
			boolean downloadExcel) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);

			if (branchResponseEntity == null || branchResponseEntity.getPayload() == null) {
				response.setMessage("No branches found for this branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				return response;
			}

			@SuppressWarnings("unchecked")
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

			if (branchList.isEmpty()) {
				response.setMessage("No branches found for this branch code: " + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setPayload(Collections.emptyList());
				return response;
			}

			List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
					.filter(Objects::nonNull).collect(Collectors.toList());

			if (downloadExcel) {

				// Fetch ALL records for Excel
				List<SmsLogEntity> smsLogs = smsLogRepo.findByBranchCodeInOrderBySmsLogIdDesc(branchCodes);

				response.setMessage("SMS log details found successfully!");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(smsLogs);
				response.setCurrentPage(0);
				response.setTotalElements(smsLogs.size());
				response.setTotalPages(1);

			} else {

				// Existing Pagination
				Pageable pageable = PageRequest.of(page, size, Sort.by("smsLogId").descending());

				Page<SmsLogEntity> smsPage = smsLogRepo.findByBranchCodeIn(branchCodes, pageable);

				if (smsPage.isEmpty()) {
					response.setMessage("No SMS log details found.");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setPayload(Collections.emptyList());
					return response;
				}

				response.setMessage("SMS log details found successfully!");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(smsPage.getContent());
				response.setCurrentPage(smsPage.getNumber());
				response.setTotalElements(smsPage.getTotalElements());
				response.setTotalPages(smsPage.getTotalPages());
			}

		} catch (Exception e) {

			response.setMessage("Error: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setPayload(Collections.emptyList());
			response.setCurrentPage(0);
			response.setTotalElements(0);
			response.setTotalPages(0);
		}

		return response;
	}

	public ResponseEntity getEmailBodyById(Long emailId) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		java.util.Optional<EmailLogEntity> optionalEmail = emailLogRepo.findById(emailId);

		if (optionalEmail.isPresent()) {

			EmailLogEntity emailLog = optionalEmail.get();

			response.setMessage("Email body fetched successfully.");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(emailLog.getBodyText()); // Replace with your actual field name

		} else {

			response.setMessage("Email not found.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setPayload(Collections.emptyList());

		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity filterEmailLogs(String branchCode, String status, String emailIdTo,
			LocalDate fromDate, LocalDate toDate) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			List<String> branchCodes = null;

			// Get branch and child branches
			if (branchCode != null && !branchCode.trim().isEmpty()) {

				ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);

				if (branchResponseEntity == null || branchResponseEntity.getPayload() == null) {
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setMessage("No branches found for branch code : " + branchCode);
					response.setPayload(Collections.emptyList());
					return response;
				}

				@SuppressWarnings("unchecked")
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

				if (branchList == null || branchList.isEmpty()) {
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setMessage("No branches found.");
					response.setPayload(Collections.emptyList());
					return response;
				}

				branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode).filter(Objects::nonNull)
						.collect(Collectors.toList());
			}

			LocalDateTime from = null;
			LocalDateTime to = null;

			if (fromDate != null) {
				from = fromDate.atStartOfDay();
			}

			if (toDate != null) {
				to = toDate.atTime(LocalTime.MAX);
			}

			List<EmailLogEntity> emailLogs = emailLogRepo.filterEmailLogs(branchCode, status, emailIdTo, from, to);

			if (emailLogs == null || emailLogs.isEmpty()) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("No email log details found.");
				response.setPayload(Collections.emptyList());
				return response;
			}

			emailLogs.forEach(email -> email.setBodyText("N/A"));

			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Email log details found successfully.");
			response.setPayload(emailLogs);

		} catch (Exception e) {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage(e.getMessage());
			response.setPayload(Collections.emptyList());
		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getAllEmails(String branchCode) {
		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		List<String> distinctEmails = emailLogRepo.getDistinctEmailsByBranchCodes(branchCodes);

		if (distinctEmails == null || distinctEmails.isEmpty()) {
			response.setMessage("NO Emails Found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}
		response.setMessage("Emails Fetched Successdfully ");
		response.setStatusCode(200);
		response.setPayload(distinctEmails);

		return response;
	}

	public ResponseEntity getBranchListForEmailLogFilter(String branchCode) {

		ResponseEntity response = new ResponseEntity();

		ResponseEntity branchResponseEntity = branchMasterService.getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();
		List<String> branchCodes = branchList.stream().map(BranchMasterEntity::getBranchCode)
				.collect(Collectors.toList());

		try {

			List<Object[]> result = branchMasterRepository.getBranchListForEmailLogFilter(branchCodes);

			List<Map<String, String>> payload = new ArrayList<>();

			if (result != null && !result.isEmpty()) {

				for (Object[] row : result) {

					Map<String, String> map = new LinkedHashMap<>();

					map.put("branchCode", row[0] != null ? row[0].toString() : "");
					map.put("branchDesc", row[1] != null ? row[1].toString() : "");

					payload.add(map);
				}
			}

			response.setStatusCode(200);
			response.setMessage("Branch List Fetched Successfully");
			response.setTotalElements(payload.size());
			response.setPayload(payload);

		} catch (Exception e) {

			e.printStackTrace();

			response.setStatusCode(500);
			response.setMessage("Failed to fetch branch list.");
			response.setPayload(Collections.emptyList());
		}

		return response;
	}

}
