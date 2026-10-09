package com.doritech.api.Service;

import com.doritech.api.Common.General;
import com.doritech.api.Common.Message;
import com.doritech.api.DTO.GroupMasterDTO;
import com.doritech.api.Entity.EmployeeMasterEntity;
import com.doritech.api.Entity.GroupMasterEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Repository.GroupMasterRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

@Service
public class GroupMasterService {
	@Autowired
	private GroupMasterRepository groupMasterRepo;

	@Autowired
	General general;
	@Autowired
	EmployeeMasterRepository employeeMasterRepository;

	/**
	 * @Author Swati
	 */

	/**
	 * SAVE OR EDIT THE GROUP MASTER
	 */

	public ResponseEntity saveOrUpdateGroupMaster(GroupMasterDTO groupMasterDTO,HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		
		LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = date.format(formatter);

        String loggedInUserEmpId = (String) session.getAttribute("empId");

		try {
			List<Message> validationErrorList = validationList(groupMasterDTO);
			if (validationErrorList.isEmpty()) {
				Optional<GroupMasterEntity> existingGroup = Optional.empty();
				String groupId = "";
				if (!existingGroup.isPresent()) {
					GroupMasterEntity groupEntity = new GroupMasterEntity();
					BeanUtils.copyProperties(groupMasterDTO, groupEntity);
					groupEntity.setCreatedDate(formattedDate);
	                //employee.setUpdatedDate(formattedDate);
					groupEntity.setCount(1); 
					groupEntity.setUpdationStatus("Save"); 
					groupEntity.setCreatedBy(loggedInUserEmpId);
					groupMasterRepo.save(groupEntity);

					response.setPayload(groupEntity);
					response.setMessage("Group Master saved successfully.");
					response.setStatusCode(HttpStatus.OK.value());
				} else {
					GroupMasterEntity group = existingGroup.get();
					BeanUtils.copyProperties(groupMasterDTO, group);
					group.setUpdatedDate(formattedDate);
					int newCount = group.getCount() + 1;
	                group.setCount(newCount); 
	                group.setUpdationStatus("Edit"); 
	                group.setUpdatedBy(loggedInUserEmpId);
					groupMasterRepo.save(group);

					response.setPayload(group);
					response.setMessage("Group Master updated successfully.");
					response.setStatusCode(HttpStatus.OK.value());
				}
			} else {
				response.setMessage("Validation failed");
				response.setPayload(validationErrorList); // You might want to include validation errors in the response
															// payload
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Group Master save failed: " + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/**
	 * GET GROUP BY ID
	 */
	public ResponseEntity getGroupByGroupId(String groupId) {
		ResponseEntity response = new ResponseEntity();
		try {
			GroupMasterEntity groupMaster = groupMasterRepo.findByGroupId(String.valueOf(groupId));

			if (groupMaster != null) {
				GroupMasterDTO groupMasterResponse = new GroupMasterDTO();

				groupMasterResponse.setGroupId(groupMaster.getGroupId());
				groupMasterResponse.setGroupName(general.checkNull(groupMaster.getGroupName()));
				groupMasterResponse.setGroupDescription(general.checkNull(groupMaster.getGroupDescription()));
				groupMasterResponse.setCreatedDate(groupMaster.getCreatedDate());
				groupMasterResponse.setEmpId(general.checkNull(groupMaster.getEmpId()));
				groupMasterResponse
						.setEmpName(employeeMasterRepository.findByEmpId(groupMaster.getEmpId()).getEmpName());

				System.out.println(groupMasterResponse);

				response.setPayload(groupMasterResponse);
				response.setMessage("Group details fetched ");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Group details not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Group details fetch failed:-" + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * GET ALL GROUP MASTER
	 */
	public ResponseEntity getAllGroupMaster() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<GroupMasterEntity> groupList = groupMasterRepo.findAll();
			List<GroupMasterDTO> groupResponseList = new ArrayList<>();

			if (!groupList.isEmpty()) {
				for (GroupMasterEntity group : groupList) {
					GroupMasterDTO groupResponse = new GroupMasterDTO();
					groupResponse.setGroupId(group.getGroupId());
					groupResponse.setGroupName(general.checkNull(group.getGroupName()));
					groupResponse.setGroupDescription(general.checkNull(group.getGroupDescription()));
					groupResponse.setCreatedDate(group.getCreatedDate());
					groupResponse.setEmpId(general.checkNull(group.getEmpId()));
					groupResponse.setEmpName(employeeMasterRepository.findByEmpId(group.getEmpId()).getEmpName());

					groupResponseList.add(groupResponse);

				}
				response.setPayload(groupResponseList);
				response.setMessage("All Group details fetched ");
				response.setStatusCode(HttpStatus.OK);

			}

			else {
				response.setMessage("No record found for Groups");
				response.setStatusCode(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Group details fetch failed:-" + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}

	/**
	 * VALIDATIONS FOR SAVING GROUP MASTER
	 */
	public List<Message> validationList(GroupMasterDTO groupMasterDTO) {

		List<Message> validateErrorMessages = new ArrayList<>();

		if (groupMasterDTO.getGroupName() == null || groupMasterDTO.getGroupName().isEmpty()) {
			validateErrorMessages.add(new Message("Group Name", "Group name can't be null"));
		}
		if (groupMasterDTO.getGroupDescription() == null || groupMasterDTO.getGroupDescription().isEmpty()) {
			validateErrorMessages.add(new Message("Group Description", "Group Description can't be null"));
		}
		if (groupMasterDTO.getCreatedDate() == null) {
			validateErrorMessages.add(new Message("Created Date", "Creation date can't be null"));
		}
		if (groupMasterDTO.getEmpId() == null || groupMasterDTO.getEmpId().isEmpty()) {
			validateErrorMessages.add(new Message("empId", "Employee ID can't be null"));
		}

		return validateErrorMessages;
	}

}