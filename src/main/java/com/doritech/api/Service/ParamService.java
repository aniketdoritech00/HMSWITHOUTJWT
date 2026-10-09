package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.ParamEntityDTO;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.ParamRepository;

@Service
public class ParamService {

	@Autowired
	ParamRepository paramRepository;

	@Autowired
	BranchMasterRepository branchMasterRepository;

	/**
	 * UPDATE THE EMPLOYEE CODE IN THE DATABASE
	 */
	public ParamEntity updateEmployeeDescp2Value(ParamEntity paramRequest) {
		// Find the ParamEntity by code and serial
		ParamEntity paramEntity = paramRepository.findByCode(paramRequest.getCode());

		if (paramEntity != null) {
			// Increment the value of descp2
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp2()) + 1);
			System.out.println("Updated Employee Value Is :: " + newValue);
			paramEntity.setDescp2(newValue);

			// Update the entity
			paramRepository.save(paramEntity);
		}

		return paramRequest;
	}

	/**
	 * GET THE COMPLETE EMPLOYEE CODE
	 */
	public String getEmployeeDescpValue() throws Exception {
		ParamEntity paramEntity = paramRepository.findByCode("EMPLOYEE_ID");
		String generateEmployeeCode = "";
		if (paramEntity != null) {
			generateEmployeeCode = paramEntity.getDescp1() + paramEntity.getDescp2();
		} else {
			throw new Exception("Invalid Param Entity");
		}
		return generateEmployeeCode;
	}

	/**
	 * UPDATE THE BRANCH CODE IN THE DATABASE
	 */
	public ParamEntity updateBranchDescp2Value(ParamEntity paramRequest) {
		// Find the ParamEntity by code and serial
		ParamEntity paramEntity = paramRepository.findByCode(paramRequest.getCode());

		if (paramEntity != null) {
			// Increment the value of descp2
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp2()) + 1);
			paramEntity.setDescp2(newValue);

			// Update the entity
			paramRepository.save(paramEntity);
		}

		return paramRequest;
	}

	/**
	 * GET THE COMPLETE BRANCH CODE
	 */
	public String getBranchDescpValue() throws Exception {
		ParamEntity paramEntity = paramRepository.findByCode("BRANCH_ID");
		String generateBranchCode = "";
		if (paramEntity != null) {
			generateBranchCode = paramEntity.getDescp1() + paramEntity.getDescp2();
		} else {
			throw new Exception("Invalid Param Entity");
		}
		return generateBranchCode;
	}

	public ResponseEntity getParamByCodeAndSerial() {
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			ParamEntity paramEntity = paramRepository.getParamByCodeAndSerial("COMPANY", "OWN");
			if (paramEntity != null) {
				ParamEntityDTO dto = new ParamEntityDTO();
				dto.setCode(paramEntity.getCode());
				dto.setSerial(paramEntity.getSerial());
				dto.setDescp1(paramEntity.getDescp1());
				dto.setDescp2(paramEntity.getDescp2());
				responseEntity.setPayload(dto);
				responseEntity.setMessage("Param Fetched..");
				responseEntity.setStatusCode(HttpStatus.OK.value());
			} else {
				responseEntity.setMessage("Param Not Fetched..");
				responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		} catch (Exception e) {
			responseEntity.setMessage("Error Occured!!" + e.getLocalizedMessage());
			responseEntity.setStatusCode(HttpStatus.BAD_REQUEST.value());
		}
		return responseEntity;
	}

	public ResponseEntity getParamForClientByCodeAndSerial() {
		ResponseEntity responseEntity = new ResponseEntity();
		try {
			ParamEntity paramEntity = paramRepository.getParamByCodeAndSerial("COMPANY", "CLIENT");
			if (paramEntity != null) {
				ParamEntityDTO dto = new ParamEntityDTO();
				dto.setCode(paramEntity.getCode());
				dto.setSerial(paramEntity.getSerial());
				dto.setDescp1(paramEntity.getDescp1());
				dto.setDescp2(paramEntity.getDescp2());
				responseEntity.setPayload(dto);
				responseEntity.setMessage("Param Fetched..");
				responseEntity.setStatusCode(HttpStatus.OK.value());
			} else {
				responseEntity.setMessage("Param Not Fetched..");
				responseEntity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		} catch (Exception e) {
			responseEntity.setMessage("Error Occured!!" + e.getLocalizedMessage());
			responseEntity.setStatusCode(HttpStatus.BAD_REQUEST.value());
		}
		return responseEntity;
	}

	public String getDeviceId() {
		ParamEntity paramEntity = paramRepository.findByCode("DEVICE_ID");
		if (paramEntity != null) {
			return paramEntity.getDescp1() + paramEntity.getDescp2();
		}
		return null;
	}

	/**
	 * UPDATE THE GROUP CODE IN THE DATABASE
	 */
	public ParamEntity updateGroupDescp2Value(ParamEntity paramRequest) {
		// Find the ParamEntity by code and serial
		ParamEntity paramEntity = paramRepository.findByCode(paramRequest.getCode());

		if (paramEntity != null) {
			// Increment the value of descp2
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp2()) + 1);
			paramEntity.setDescp2(newValue);

			// Update the entity
			paramRepository.save(paramEntity);
		}

		return paramRequest;
	}

	/**
	 * GET THE COMPLETE GROUP MASTER CODE
	 */
	public String getGroupDescpValue() throws Exception {
		ParamEntity paramEntity = paramRepository.findByCode("GROUP_ID");
		String generateGroupCode = "";
		if (paramEntity != null) {
			generateGroupCode = paramEntity.getDescp1() + paramEntity.getDescp2();
		} else {
			throw new Exception("Invalid Param Entity");
		}
		return generateGroupCode;
	}

	public String getNoOfAttempts() {
		try {
			ParamEntity paramEntity = paramRepository.findByCode("ASSESSMENT");
			String noOfAttempts = "";
			if (paramEntity != null) {
				noOfAttempts = paramEntity.getDescp2();
			}
			return noOfAttempts;
		} catch (Exception e) {
			return "record not found";
		}

	}

	public ParamEntity updateDeviceDescp2Value(ParamEntity paramRequest) {
		// Find the ParamEntity by code and serial
		ParamEntity paramEntity = paramRepository.findByCode(paramRequest.getCode());

		if (paramEntity != null) {
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp2()) + 1);
			paramEntity.setDescp2(newValue);
			paramRepository.save(paramEntity);
		}

		return paramRequest;
	}

	public ParamEntity updateDeviceUniqueId(ParamEntity paramRequest) {
		// Find the ParamEntity by code and serial
		ParamEntity paramEntity = paramRepository.findByCode(paramRequest.getCode());

		if (paramEntity != null) {
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp1()) + 1);
			paramEntity.setDescp1(newValue);
			paramRepository.save(paramEntity);
		}

		return paramRequest;
	}

	public ResponseEntity getRemarksList() {
		ResponseEntity response = new ResponseEntity();
		List<ParamEntity> list = paramRepository.findBySerial("REMARK_TYPE");
		List<String> remarkList = new ArrayList<>();
		if (!list.isEmpty()) {
			list.forEach(x -> {
				remarkList.add(x.getDescp1());
			});

			response.setPayload(remarkList);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} else {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error occured while fetching data");
		}
		return response;
	}

	public ResponseEntity getZonesList() {
		ResponseEntity response = new ResponseEntity();
		List<ParamEntity> list = paramRepository.findBySerial("ZONE_DESIRED_NAME");
		List<String> zoneList = new ArrayList<>();
		if (!list.isEmpty()) {
			list.forEach(x -> {
				zoneList.add(x.getDescp1());
			});

			response.setPayload(zoneList);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} else {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error occured while fetching data");
		}
		return response;
	}

	public ResponseEntity getZonesInputTypeList() {
		ResponseEntity response = new ResponseEntity();
		List<ParamEntity> list = paramRepository.findBySerial("ZONE_INPUT_TYPE");
		List<String> zoneList = new ArrayList<>();
		if (!list.isEmpty()) {
			list.forEach(x -> {
				zoneList.add(x.getDescp1());
			});

			response.setPayload(zoneList);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} else {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error occured while fetching data");
		}
		return response;
	}

	public String getWhatsappNumber() {
		try {
			ParamEntity paramEntity = paramRepository.findByCode("WHATSAPP");
			String whatsappNo = "";
			if (paramEntity != null) {
				whatsappNo = paramEntity.getDescp1();
			}
			return whatsappNo;
		} catch (Exception e) {
			return "record not found";
		}

	}

	public String getParamByCode(String code) {
		ParamEntity entity = paramRepository.findByCode(code);
		String emailString = "";
		if (entity != null) {
			emailString = entity.getDescp1();
		}
		return emailString;
	}

	public String getComplaintId() throws Exception {
		ParamEntity paramEntity = paramRepository.findByCode("COMPLAINT_ID");
		String generateGroupCode = "";
		if (paramEntity != null) {
			generateGroupCode = paramEntity.getDescp1() + paramEntity.getDescp2();
		} else {
			throw new Exception("Invalid Param Entity");
		}
		return generateGroupCode;
	}

	public ParamEntity updateDescp2(String code) {
		// Find the ParamEntity by code and serial
		ParamEntity paramEntity = paramRepository.findByCode(code);

		if (paramEntity != null) {
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp2()) + 1);
			paramEntity.setDescp2(newValue);
			paramRepository.save(paramEntity);
		}

		return paramEntity;
	}

	public String getVendorIdDescpValue() throws Exception {
		ParamEntity paramEntity = this.paramRepository.findByCode("VENDOR_ID");
		String generateVendorId = "";
		if (paramEntity != null) {
			generateVendorId = paramEntity.getDescp1() + paramEntity.getDescp2();
			return generateVendorId;
		} else {
			throw new Exception("Invalid Param Entity");
		}
	}

	public ParamEntity updateVendorIdDescp2Value(ParamEntity paramRequest) {
		ParamEntity paramEntity = this.paramRepository.findByCode(paramRequest.getCode());
		if (paramEntity != null) {
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp2()) + 1);
			paramEntity.setDescp2(newValue);
			this.paramRepository.save(paramEntity);
		}

		return paramRequest;
	}

	public String updateIfscForSuperHO() {
		ParamEntity paramEntity = this.paramRepository.findByCode("IFSC");
		String oldValue = "";
		if (paramEntity != null) {
			oldValue = paramEntity.getDescp2();
			String newValue = String.valueOf(Integer.parseInt(paramEntity.getDescp2()) + 1);
			paramEntity.setDescp2(newValue);
			this.paramRepository.save(paramEntity);
		}

		return oldValue;

	}

	public ResponseEntity getRecordBySerial(String serial) {

		ResponseEntity response = new ResponseEntity();

		try {

			Optional<ParamEntity> record = paramRepository.findBySerial(serial).stream().findFirst();

			if (record.isPresent()) {

				response.setMessage("Record found successfully");
				response.setStatusCode(200);
				response.setPayload(record.get());

			} else {

				response.setMessage("No record found");
				response.setStatusCode(404);
				response.setPayload(null);

			}

		} catch (Exception e) {

			response.setMessage("Something went wrong");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());

		}

		return response;
	}

	public ResponseEntity updateNonSyncSchedulerTime(ParamEntityDTO request) {

		ResponseEntity response = new ResponseEntity();

		try {

			Optional<ParamEntity> optional = paramRepository.findFirstBySerial(request.getSerial());

			if (!optional.isPresent()) {

				response.setMessage("Record not found");
				response.setStatusCode(404);
				response.setPayload(null);

				return response;
			}

			ParamEntity entity = optional.get();

			if (request.getDescp3() != null) {
				entity.setDescp3(request.getDescp3());
			}

			ParamEntity savedEntity = paramRepository.save(entity);

			response.setMessage("Updated successfully");
			response.setStatusCode(200);
			response.setPayload(savedEntity);

		} catch (Exception e) {
			response.setMessage("Something went wrong");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());

		}

		return response;
	}

	public ResponseEntity getDescByCodeAndSerial(String code, String serial) {

		ResponseEntity response = new ResponseEntity();

		try {

			List<ParamEntity> list = paramRepository.getParamDataByCodeAndSerial(code, serial);

			if (list == null || list.isEmpty()) {
				response.setStatusCode(404);
				response.setMessage("Data Not Found");
				response.setPayload(null);
				return response;
			}

			List<Map<String, String>> result = new ArrayList<>();

			for (ParamEntity entity : list) {

				Map<String, String> map = new HashMap<>();
				map.put("descp1", entity.getDescp1());
				map.put("descp2", entity.getDescp2());

				result.add(map);
			}

			response.setStatusCode(200);
			response.setMessage("Success");
			response.setPayload(result);

		} catch (Exception e) {

			response.setStatusCode(500);
			response.setMessage("Something went wrong : " + e.getMessage());
			response.setPayload(null);
		}

		return response;
	}

	public ResponseEntity updateDesc(String code, String serial, String descp1) {

		ResponseEntity response = new ResponseEntity();

		try {

			ParamEntity entity = paramRepository.findByCodeAndSerial(code, serial);

			if (entity == null) {
				response.setMessage("Record not found");
				response.setStatusCode(404);
				response.setPayload(null);
				return response;
			}

			entity.setDescp1(descp1);

			ParamEntity savedEntity = paramRepository.save(entity);

			response.setMessage("Description updated successfully");
			response.setStatusCode(200);
			response.setPayload(savedEntity);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Something went wrong");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

}