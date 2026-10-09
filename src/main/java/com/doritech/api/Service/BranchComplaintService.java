package com.doritech.api.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Common.General;
import com.doritech.api.DTO.BranchComplaintDTO;
import com.doritech.api.Entity.BranchComplainEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.BranchComplaintRepository;

@Service
public class BranchComplaintService {

	@Autowired
	BranchComplaintRepository branchComplaintRepository;
	@Autowired
	General general;

	public ResponseEntity saveBranchComplaint(BranchComplaintDTO branchcomplaintRequest) {
		ResponseEntity response = new ResponseEntity();
		try {

			if (branchcomplaintRequest != null) {
				BranchComplainEntity entity = new BranchComplainEntity();
				BeanUtils.copyProperties(branchcomplaintRequest, entity);

				LocalDateTime now = LocalDateTime.now();
				String dateTimeString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Format as
																										// desired

				entity.setDateTime(dateTimeString);
				branchComplaintRepository.save(entity);
				response.setPayload(entity);
				response.setMessage("Data saved Successfully");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Request is empty");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			}

		} catch (Exception e) {
		}
		return response;
	}
}