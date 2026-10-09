package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Common.Message;
import com.doritech.api.DTO.VendorMasterDTO;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.VendorMasterRepository;

@Service
public class VendorMasterService {
	@Autowired
	VendorMasterRepository vendorMasterRepository;

	@Autowired
	ParamRepository paramRepository;
	@Autowired
	DeviceMasterService deviceMasterService;

	@Autowired
	ParamService paramService;

	public ResponseEntity saveVendorMaster(VendorMasterDTO vendorMasterDTO, HttpServletRequest httpServletRequest) {
		ResponseEntity response = new ResponseEntity();
		List<Message> validationMessages = this.validations(vendorMasterDTO);
		if (!validationMessages.isEmpty()) {
			response.setMessage("Validation failed.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setPayload(validationMessages);
			return response;
		}
		Optional<VendorMasterEntity> existingVendor = this.vendorMasterRepository
				.findByEmail(vendorMasterDTO.getEmail());
		if (existingVendor.isPresent()) {
			response.setMessage(
					"Vendor already present in the given email-id database :: " + vendorMasterDTO.getEmail());
			response.setStatusCode(HttpStatus.CONFLICT.value());
			return response;
		}
		VendorMasterEntity entity = new VendorMasterEntity();
		try {
			BeanUtils.copyProperties(vendorMasterDTO, entity);

		} catch (Exception e) {
			response.setMessage("Error copying properties from DTO to entity.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}
		try {
			entity.setTerminal(httpServletRequest.getRemoteAddr());
			entity.setCreatedAt(new Date());
			entity.setCreatedBy(vendorMasterDTO.getCreatedBy());
		} catch (Exception e) {
			response.setMessage("Failed to fetch client IP address or set creation date.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}

		try {
			this.vendorMasterRepository.save(entity);
		} catch (DataIntegrityViolationException e) {
			response.setMessage("Database constraint violation. Possible duplicate entry.");
			response.setStatusCode(HttpStatus.CONFLICT.value());
			return response;
		} catch (Exception e) {
			response.setMessage("Error saving vendor to the database.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}
		response.setPayload(entity);
		response.setMessage("Vendor saved successfully.");
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

	public ResponseEntity getAllVendorDetails(int page, int size) {

		ResponseEntity response = new ResponseEntity();

		try {

			// STATUS MAP
			Map<String, String> statusMap = getStatusMap();

			Pageable pageable = PageRequest.of(page, size);

			Page<VendorMasterEntity> vendorPage =
					vendorMasterRepository.findAllByOrderByVendorNameAsc(pageable);

			List<VendorMasterEntity> vendorMasterList = vendorPage.getContent();

			if (vendorMasterList.isEmpty()) {

				response.setMessage("Sorry! There is no data present in the database");

				response.setStatusCode(HttpStatus.NOT_FOUND.value());

			} else {

				// STATUS CONVERT
				for (VendorMasterEntity vendor : vendorMasterList) {

					String statusValue = statusMap.get(vendor.getStatus());

					if (statusValue != null) {

						vendor.setStatus(statusValue);
					}
				}

				response.setMessage("Vendor details found successfully!");

				response.setStatusCode(HttpStatus.OK.value());

				response.setPayload(vendorMasterList);

				// PAGINATION DETAILS
				response.setTotalElements(vendorPage.getTotalElements());

				response.setTotalPages(vendorPage.getTotalPages());

				response.setCurrentPage(vendorPage.getNumber());
			}

		} catch (DataAccessException dae) {

			dae.printStackTrace();

			response.setMessage(
					"Database error occurred while fetching Vendor Master Entity");

			response.setStatusCode(
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getAllActiveVendorDetails() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<VendorMasterEntity> vendorMasterList = vendorMasterRepository.findAllByStatus("A");
			if (vendorMasterList.isEmpty()) {
				response.setMessage("Sorry! There is no data present in the database");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setMessage("Active vendor found successfully!");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(vendorMasterList);
			}

		} catch (DataAccessException dae) {
			dae.printStackTrace();
			response.setMessage("Database error occurred while fetching Vendor Master Entity");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getVendorMasterById(String vendorId) {
		ResponseEntity response = new ResponseEntity();
		try {
			Optional<VendorMasterEntity> optionalEntity = vendorMasterRepository.findById(vendorId);
			if (optionalEntity.isPresent()) {
				VendorMasterDTO vendorMasterDTO = new VendorMasterDTO();
				BeanUtils.copyProperties(optionalEntity.get(), vendorMasterDTO);

				if (vendorMasterDTO.getMobile() != null && vendorMasterDTO.getMobile().startsWith("+91")) {
					vendorMasterDTO.setMobile(vendorMasterDTO.getMobile().substring(3));
				}

				response.setPayload(vendorMasterDTO);
				response.setMessage("Vendor Master with given ID has been fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No Vendor Master is present with the given ID in the database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error occurred while fetching Vendor Master Entity.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity deactivateVendorByVendorId(String vendorId) {
		ResponseEntity response = new ResponseEntity();
		try {
			Optional<VendorMasterEntity> optionalItem = vendorMasterRepository.findById(vendorId);
			if (optionalItem.isPresent()) {
				VendorMasterEntity entity = optionalItem.get();
				entity.setStatus("Deactive");
				vendorMasterRepository.save(entity);
				response.setPayload(entity);
				response.setMessage("Vendor deactivated successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Vendor with ID '" + vendorId + "' not found.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error occurred while deactivating vendor.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity activateVendorByVendorId(String vendorId) {
		ResponseEntity response = new ResponseEntity();
		try {
			Optional<VendorMasterEntity> optionalItem = vendorMasterRepository.findById(vendorId);
			if (optionalItem.isPresent()) {
				VendorMasterEntity entity = optionalItem.get();
				entity.setStatus("Active");
				vendorMasterRepository.save(entity);
				response.setPayload(entity);
				response.setMessage("Vendor activated successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("Vendor with ID '" + vendorId + "' not found.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error occurred while deactivating vendor.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity updateVendorMaster(VendorMasterDTO vendorMasterDTO, HttpServletRequest httpServletRequest) {
		ResponseEntity response = new ResponseEntity();
		List<Message> validationMessages = validations(vendorMasterDTO);
		if (!validationMessages.isEmpty()) {
			response.setMessage("Item not updated. Validation failed.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setPayload(validationMessages);
			return response;
		}
		Optional<VendorMasterEntity> existingVendor;

		try {
			existingVendor = vendorMasterRepository.findById(vendorMasterDTO.getVendorId());
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error while fetching vendor details.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}

		if (!existingVendor.isPresent()) {
			response.setMessage("Vendor with ID '" + vendorMasterDTO.getVendorId() + "' not found.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		VendorMasterEntity entity = existingVendor.get();

		if (vendorMasterDTO.getVendorName() != null
				&& !vendorMasterDTO.getVendorName().equals(entity.getVendorName())) {
			entity.setVendorName(vendorMasterDTO.getVendorName());
		}
		if (vendorMasterDTO.getContactPerson() != null
				&& !vendorMasterDTO.getContactPerson().equals(entity.getContactPerson())) {
			entity.setContactPerson(vendorMasterDTO.getContactPerson());
		}
		if (vendorMasterDTO.getEmail() != null && !vendorMasterDTO.getEmail().equals(entity.getEmail())) {
			entity.setEmail(vendorMasterDTO.getEmail());
		}
		if (vendorMasterDTO.getMobile() != null && !vendorMasterDTO.getMobile().equals(entity.getMobile())) {
			entity.setMobile(vendorMasterDTO.getMobile());
		}
		if (vendorMasterDTO.getAddress() != null && !vendorMasterDTO.getAddress().equals(entity.getAddress())) {
			entity.setAddress(vendorMasterDTO.getAddress());
		}
		if (vendorMasterDTO.getCity() != null && !vendorMasterDTO.getCity().equals(entity.getCity())) {
			entity.setCity(vendorMasterDTO.getCity());
		}
		if (vendorMasterDTO.getState() != null && !vendorMasterDTO.getState().equals(entity.getState())) {
			entity.setState(vendorMasterDTO.getState());
		}
		if (vendorMasterDTO.getCountry() != null && !vendorMasterDTO.getCountry().equals(entity.getCountry())) {
			entity.setCountry(vendorMasterDTO.getCountry());
		}
		if (vendorMasterDTO.getPin() != null && !vendorMasterDTO.getPin().equals(entity.getPin())) {
			entity.setPin(vendorMasterDTO.getPin());
		}
		if (vendorMasterDTO.getStatus() != null && !vendorMasterDTO.getStatus().equals(entity.getStatus())) {
			entity.setStatus(vendorMasterDTO.getStatus());
		}
		if (vendorMasterDTO.getUpdatedBy() != null && !vendorMasterDTO.getUpdatedBy().equals(entity.getUpdatedBy())) {
			entity.setUpdatedBy(vendorMasterDTO.getUpdatedBy());
		}
		if (vendorMasterDTO.getUpdatedAt() != null && !vendorMasterDTO.getUpdatedAt().equals(entity.getUpdatedAt())) {
			entity.setUpdatedAt(vendorMasterDTO.getUpdatedAt());
		}
		try {
			vendorMasterRepository.save(entity);
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error while updating vendor.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}

		response.setPayload(entity);
		response.setMessage("Vendor updated successfully.");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	public ResponseEntity deleteVendorByVendorId(String vendorId) {
		ResponseEntity response = new ResponseEntity();

		Optional<VendorMasterEntity> optionalItem;
		try {
			optionalItem = vendorMasterRepository.findById(vendorId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error while fetching vendor details.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}

		if (!optionalItem.isPresent()) {
			response.setMessage("Vendor with ID '" + vendorId + "' not found.");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		VendorMasterEntity entity = optionalItem.get();

		try {
			vendorMasterRepository.deleteById(vendorId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error while deleting vendor.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}

		response.setPayload(entity);
		response.setMessage("Vendor deleted successfully.");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	public List<Message> validations(VendorMasterDTO vendorMasterDTO) {
		List<Message> messageList = new ArrayList<>();
		if (vendorMasterDTO.getVendorName() == null || StringUtils.isEmpty(vendorMasterDTO.getVendorName())) {
			messageList.add(new Message("Vendor Name", "Vendor Name can't be null or empty"));
		}

		if (vendorMasterDTO.getContactPerson() == null || StringUtils.isEmpty(vendorMasterDTO.getContactPerson())) {
			messageList.add(new Message("Contact Person", "Contact Person can't be null or empty"));
		}

		if (vendorMasterDTO.getEmail() == null || StringUtils.isEmpty(vendorMasterDTO.getEmail())) {
			messageList.add(new Message("Email ID", "Email ID can't be null or empty"));
		}

		if (vendorMasterDTO.getMobile() == null || StringUtils.isEmpty(vendorMasterDTO.getMobile())) {
			messageList.add(new Message("Mobile Number", "Mobile Number can't be null or empty"));
		}

		if (vendorMasterDTO.getAddress() == null || StringUtils.isEmpty(vendorMasterDTO.getAddress())) {
			messageList.add(new Message("Address", "Address can't be null or empty"));
		}

		if (vendorMasterDTO.getCity() == null || StringUtils.isEmpty(vendorMasterDTO.getCity())) {
			messageList.add(new Message("City", "City can't be null or empty"));
		}

		if (vendorMasterDTO.getState() == null || StringUtils.isEmpty(vendorMasterDTO.getState())) {
			messageList.add(new Message("State", "State can't be null or empty"));
		}

		if (vendorMasterDTO.getCountry() == null || StringUtils.isEmpty(vendorMasterDTO.getCountry())) {
			messageList.add(new Message("Country", "Country can't be null or empty"));
		}

		if (vendorMasterDTO.getPin() == null || StringUtils.isEmpty(vendorMasterDTO.getPin())) {
			messageList.add(new Message("Pin Code", "Pin Code can't be null or empty"));
		}
		return messageList;
	}

	public ResponseEntity getVendorMasterByName(String vendorName) {
		ResponseEntity response = new ResponseEntity();
		try {
			Optional<VendorMasterEntity> optionalEntity = vendorMasterRepository.findByVendorName(vendorName);
			if (optionalEntity.isPresent()) {
				VendorMasterDTO vendorMasterDTO = new VendorMasterDTO();
				BeanUtils.copyProperties(optionalEntity.get(), vendorMasterDTO);

				if (vendorMasterDTO.getMobile() != null && vendorMasterDTO.getMobile().startsWith("+91")) {
					vendorMasterDTO.setMobile(vendorMasterDTO.getMobile().substring(3));
				}

				response.setPayload(vendorMasterDTO);
				response.setMessage("Vendor Master with given ID has been fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No Vendor Master is present with the given ID in the database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error occurred while fetching Vendor Master Entity.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity checkExistence(String email, String mobile, String vendorName) {
		ResponseEntity response = new ResponseEntity();
		Map<String, Object> resultMap = new HashMap<>();
		List<String> existingFields = new ArrayList<>();

		try {
			// Check if at least one parameter is provided
			if ((email == null || email.isEmpty()) && (mobile == null || mobile.isEmpty())
					&& (vendorName == null || vendorName.isEmpty())) {
				response.setMessage("Please provide at least one parameter: email, mobile, or vendorName");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

			// Check email existence
			if (email != null && !email.isEmpty()) {
				Optional<VendorMasterEntity> existingByEmail = vendorMasterRepository.findByEmail(email);
				if (existingByEmail.isPresent()) {
					existingFields.add("Email '" + email + "' is already registered with Vendor ID: "
							+ existingByEmail.get().getVendorId());
					resultMap.put("emailExists", true);
					resultMap.put("existingVendorByEmail", existingByEmail.get().getVendorId());
				} else {
					resultMap.put("emailExists", false);
				}
			}

			// Check mobile existence
			if (mobile != null && !mobile.isEmpty()) {
				// Handle both with and without +91 prefix for mobile numbers
				String mobileToCheck = mobile;
				String mobileWithPrefix = mobile.startsWith("+91") ? mobile : "+91" + mobile;
				String mobileWithoutPrefix = mobile.startsWith("+91") ? mobile.substring(3) : mobile;

				Optional<VendorMasterEntity> existingByMobile = vendorMasterRepository.findByMobile(mobileToCheck);
				if (!existingByMobile.isPresent() && !mobileToCheck.equals(mobileWithPrefix)) {
					existingByMobile = vendorMasterRepository.findByMobile(mobileWithPrefix);
				}
				if (!existingByMobile.isPresent() && !mobileToCheck.equals(mobileWithoutPrefix)) {
					existingByMobile = vendorMasterRepository.findByMobile(mobileWithoutPrefix);
				}

				if (existingByMobile.isPresent()) {
					existingFields.add("Mobile number '" + mobile + "' is already registered with Vendor ID: "
							+ existingByMobile.get().getVendorId());
					resultMap.put("mobileExists", true);
					resultMap.put("existingVendorByMobile", existingByMobile.get().getVendorId());
				} else {
					resultMap.put("mobileExists", false);
				}
			}

			// Check vendor name existence
			if (vendorName != null && !vendorName.isEmpty()) {
				Optional<VendorMasterEntity> existingByVendorName = vendorMasterRepository.findByVendorName(vendorName);
				if (existingByVendorName.isPresent()) {
					existingFields.add("Vendor Name '" + vendorName + "' is already registered with Vendor ID: "
							+ existingByVendorName.get().getVendorId());
					resultMap.put("vendorNameExists", true);
					resultMap.put("existingVendorByName", existingByVendorName.get().getVendorId());
				} else {
					resultMap.put("vendorNameExists", false);
				}
			}

			// Prepare response
			if (!existingFields.isEmpty()) {
				resultMap.put("existingFields", existingFields);
				resultMap.put("isAvailable", false);
				response.setMessage("Duplicate found: " + String.join(", ", existingFields));
				response.setStatusCode(HttpStatus.CONFLICT.value());
			} else {
				resultMap.put("isAvailable", true);
				response.setMessage("Email, Mobile, and Vendor Name are all available");
				response.setStatusCode(HttpStatus.OK.value());
			}

			response.setPayload(resultMap);

		} catch (DataAccessException e) {
			e.printStackTrace();
			response.setMessage("Database error occurred while checking existence");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getAllVendorDetail() {

		ResponseEntity response = new ResponseEntity();

		try {

			// STATUS MAP
			Map<String, String> statusMap = getStatusMap();

			List<VendorMasterEntity> vendorMasterList =
					vendorMasterRepository.findAll();

			if (vendorMasterList.isEmpty()) {

				response.setMessage(
						"Sorry! There is no data present in the database");

				response.setStatusCode(
						HttpStatus.NOT_FOUND.value());

			} else {

				// STATUS CONVERT
				for (VendorMasterEntity vendor : vendorMasterList) {

					String statusValue =
							statusMap.get(vendor.getStatus());

					if (statusValue != null) {

						vendor.setStatus(statusValue);
					}
				}

				response.setMessage(
						"Vendor details found successfully!");

				response.setStatusCode(
						HttpStatus.OK.value());

				response.setPayload(vendorMasterList);
			}

		} catch (DataAccessException dae) {

			dae.printStackTrace();

			response.setMessage(
					"Database error occurred while fetching Vendor Master Entity");

			response.setStatusCode(
					HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}
	// STATUS MAP
	private Map<String, String> getStatusMap() {

		Map<String, String> statusMap = new HashMap<>();

		List<ParamEntity> params = deviceMasterService.getParamByCode("STATUS");

		if (params != null && !params.isEmpty()) {

			for (ParamEntity param : params) {

				statusMap.put(param.getSerial(), param.getDescp1());
			}
		}

		return statusMap;
	}
	
	public ResponseEntity getAllActiveVendorsDetails() {
		ResponseEntity response = new ResponseEntity();
		
		List<VendorMasterEntity> vendors = vendorMasterRepository.findByStatusInOrderByVendorNameAsc();
		if(vendors.isEmpty() || vendors == null) {
			response.setMessage("Sorry! There is no Active vendors present in the database");
			response.setStatusCode(404);
			return response;
		}
		
		response.setMessage("Vendors Fetched Successfully");
		response.setStatusCode(200);
		response.setPayload(vendors);
		
		return response;
	}
	
	
}
