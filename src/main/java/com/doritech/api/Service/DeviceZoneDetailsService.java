package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.api.DTO.DeviceZoneDetailsDTO;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.DynamicConfigurationEntity;
import com.doritech.api.Entity.NonSiaZoneCameraMapping;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.DynamicConfigurationRepository;
import com.doritech.api.Repository.NonSiaZoneCameraMappingRepository;
import com.doritech.api.Repository.ParamRepository;

@Service
public class DeviceZoneDetailsService {
	@Autowired
	DeviceZoneDetailsRepository deviceZoneDetailsRepository;
	@Autowired
	ParamRepository paramRepository;
	@Autowired
	ParamService paramService;
	@Autowired
	DynamicConfigrationService dynamicConfigrationService;
	@Autowired
	DynamicConfigurationRepository dynamicConfigurationRepository;
	
	@Autowired
    DeviceMasterRepository deviceMasterRepository;

	@Autowired
	NonSiaZoneCameraMappingRepository nonSiaZoneCameraMappingRepository;

	/*
	 * public ResponseEntity addMultipleDeviceZoneDetails(List<DeviceZoneDetailsDTO>
	 * deviceZoneDetailsDTOs) {
	 * 
	 * 
	 * ResponseEntity response = new ResponseEntity(); try {
	 * List<DeviceZoneDetailsEntity> responseList = new ArrayList<>(); ParamEntity
	 * paramEntity = paramRepository.findByCode("DEVICE_ZONE_ID"); int deviceZoneId
	 * = Integer.parseInt(paramEntity.getDescp2());
	 * System.out.println("Initial DeviceId-->" + deviceZoneId);
	 * 
	 * for (DeviceZoneDetailsDTO deviceZoneDetailsDTO : deviceZoneDetailsDTOs) {
	 * System.out.println("devicezoneDTO--->>>"+deviceZoneDetailsDTO);
	 * 
	 * Optional<DeviceZoneDetailsEntity> optionalDeviceZoneDetails =
	 * deviceZoneDetailsRepository.findById(deviceZoneDetailsDTO.getId());
	 * 
	 * if (!optionalDeviceZoneDetails.isPresent()) { DeviceZoneDetailsEntity entity
	 * = new DeviceZoneDetailsEntity(); String id = paramEntity.getDescp1() +
	 * String.valueOf(deviceZoneId); System.out.println("Generated Id-->" + id);
	 * entity.setId(id);
	 * 
	 * deviceZoneDetailsDTO.setId(id); if (deviceZoneDetailsDTO.getMessage() != null
	 * && !deviceZoneDetailsDTO.getMessage().isEmpty()) {
	 * System.out.println("Enter--");
	 * org.springframework.http.ResponseEntity<Object> dynamicConfiguration
	 * =dynamicConfigrationService.saveDyanamicConfiguration(deviceZoneDetailsDTO);
	 * System.out.println("Dynamic Configuration is : -----------------"
	 * +dynamicConfiguration); }
	 * 
	 * entity.setDeviceId(deviceZoneDetailsDTO.getDeviceId());
	 * entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
	 * entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
	 * entity.setAlertCode(deviceZoneDetailsDTO.getAlertCode());
	 * entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
	 * entity.setInputType(deviceZoneDetailsDTO.getInputType());
	 * entity.setPrimaryFlag(deviceZoneDetailsDTO.getPrimaryFlag());
	 * entity.setCriticalAlertFlag(deviceZoneDetailsDTO.getCriticalAlertFlag());
	 * entity.setVendorId(deviceZoneDetailsDTO.getVendorId());
	 * entity.setVendors(deviceZoneDetailsDTO.getVendors());
	 * deviceZoneDetailsRepository.save(entity);
	 * 
	 * System.out.println("Saved entity-->" + entity);
	 * 
	 * responseList.add(entity);
	 * 
	 * deviceZoneId++; System.out.println("Updated DeviceId-->" + deviceZoneId); }
	 * else { System.out.println("Device details already present in the database.");
	 * response.setMessage("Device details already present in the database.");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); return response; } }
	 * paramEntity.setDescp2(String.valueOf(deviceZoneId));
	 * paramRepository.save(paramEntity);
	 * 
	 * response.setPayload(responseList);
	 * response.setMessage("Device zone details saved successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); } catch (Exception e) {
	 * e.printStackTrace();
	 * System.out.println("Error adding device zone details items: " +
	 * e.getMessage());
	 * response.setMessage("Error adding device zone details items: " +
	 * e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); } return
	 * response; } public ResponseEntity
	 * editMultipleDeviceZoneDetails(List<DeviceZoneDetailsDTO>
	 * deviceZoneDetailsDTOs) { ResponseEntity response = new ResponseEntity(); try
	 * {
	 * 
	 * ParamEntity paramEntity = paramRepository.findByCode("DEVICE_ZONE_ID"); int
	 * deviceZoneId = Integer.parseInt(paramEntity.getDescp2());
	 * System.out.println("Initial DeviceId-->" + deviceZoneId);
	 * List<DeviceZoneDetailsEntity> responseList = new ArrayList<>(); for
	 * (DeviceZoneDetailsDTO deviceZoneDetailsDTO : deviceZoneDetailsDTOs) {
	 * Optional<DeviceZoneDetailsEntity> optionalDeviceZoneDetails =
	 * deviceZoneDetailsRepository.findById(deviceZoneDetailsDTO.getId()); if
	 * (optionalDeviceZoneDetails.isPresent()) { DeviceZoneDetailsEntity entity =
	 * optionalDeviceZoneDetails.get();
	 * entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
	 * entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
	 * entity.setAlertCode(deviceZoneDetailsDTO.getAlertCode());
	 * entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
	 * entity.setInputType(deviceZoneDetailsDTO.getInputType());
	 * entity.setPrimaryFlag(deviceZoneDetailsDTO.getPrimaryFlag());
	 * entity.setCriticalAlertFlag(deviceZoneDetailsDTO.getCriticalAlertFlag());
	 * entity.setVendorId(deviceZoneDetailsDTO.getVendorId());
	 * entity.setVendors(deviceZoneDetailsDTO.getVendors());
	 * deviceZoneDetailsRepository.save(entity);
	 * 
	 * //if(deviceZoneDetailsDTO.getMessage()!=null) { DynamicConfigurationEntity
	 * dynamicConfigrationEntity =
	 * dynamicConfigurationRepository.findByDeviceIdAndDeviceZoneId(
	 * deviceZoneDetailsDTO.getDeviceId(), deviceZoneDetailsDTO.getId());
	 * 
	 * if(dynamicConfigrationEntity!=null) {
	 * 
	 * 
	 * dynamicConfigrationEntity.setMessage(deviceZoneDetailsDTO.getMessage());
	 * //dynamicConfigrationEntity.setZone(deviceZoneDetailsDTO.getZoneOriginalName(
	 * ));
	 * dynamicConfigrationEntity.setAlertType(deviceZoneDetailsDTO.getAlertType());
	 * dynamicConfigurationRepository.save(dynamicConfigrationEntity); }
	 * 
	 * // } responseList.add(entity); } else {
	 * 
	 * 
	 * 
	 * DeviceZoneDetailsEntity entity = new DeviceZoneDetailsEntity(); String id =
	 * paramEntity.getDescp1() + String.valueOf(deviceZoneId);
	 * System.out.println("Generated Id-->" + id); entity.setId(id);
	 * entity.setDeviceId(deviceZoneDetailsDTO.getDeviceId());
	 * entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
	 * entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
	 * entity.setAlertCode(deviceZoneDetailsDTO.getAlertCode());
	 * entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
	 * entity.setInputType(deviceZoneDetailsDTO.getInputType());
	 * entity.setPrimaryFlag(deviceZoneDetailsDTO.getPrimaryFlag());
	 * entity.setCriticalAlertFlag(deviceZoneDetailsDTO.getCriticalAlertFlag());
	 * entity.setVendorId(deviceZoneDetailsDTO.getVendorId());
	 * entity.setVendors(deviceZoneDetailsDTO.getVendors());
	 * deviceZoneDetailsRepository.save(entity);
	 * System.out.println("Saved entity-->" + entity);
	 * 
	 * responseList.add(entity);
	 * 
	 * deviceZoneId++; responseList.add(entity);
	 * 
	 * // response.setMessage("New Device added succesfully " +
	 * deviceZoneDetailsDTO.getId()); //
	 * response.setStatusCode(HttpStatus.OK.value()); // return response; } }
	 * 
	 * paramEntity.setDescp2(String.valueOf(deviceZoneId));
	 * paramRepository.save(paramEntity);
	 * 
	 * response.setPayload(responseList);
	 * response.setMessage("Device zone details updated successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); } catch (Exception e) {
	 * e.printStackTrace();
	 * System.out.println("Error editing device zone details items: " +
	 * e.getMessage());
	 * response.setMessage("Error editing device zone details items: " +
	 * e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); } return
	 * response; }
	 */

	public ResponseEntity addMultipleDeviceZoneDetails(List<DeviceZoneDetailsDTO> deviceZoneDetailsDTOs) {

		ResponseEntity response = new ResponseEntity();

		try {

			List<DeviceZoneDetailsEntity> responseList = new ArrayList<>();

			ParamEntity paramEntity = paramRepository.findByCode("DEVICE_ZONE_ID");

			int deviceZoneId = Integer.parseInt(paramEntity.getDescp2());

			System.out.println("Initial DeviceId-->" + deviceZoneId);

			for (DeviceZoneDetailsDTO deviceZoneDetailsDTO : deviceZoneDetailsDTOs) {

				System.out.println("devicezoneDTO--->>>" + deviceZoneDetailsDTO);

				Optional<DeviceZoneDetailsEntity> optionalDeviceZoneDetails = deviceZoneDetailsRepository
						.findById(deviceZoneDetailsDTO.getId());

				if (!optionalDeviceZoneDetails.isPresent()) {

					DeviceZoneDetailsEntity entity = new DeviceZoneDetailsEntity();

					String id = paramEntity.getDescp1() + String.valueOf(deviceZoneId);

					System.out.println("Generated Id-->" + id);

					entity.setId(id);

					deviceZoneDetailsDTO.setId(id);

					if (deviceZoneDetailsDTO.getMessage() != null && !deviceZoneDetailsDTO.getMessage().isEmpty()) {

						System.out.println("Enter--");

						org.springframework.http.ResponseEntity<Object> dynamicConfiguration = dynamicConfigrationService
								.saveDyanamicConfiguration(deviceZoneDetailsDTO);

						System.out.println("Dynamic Configuration is : -----------------" + dynamicConfiguration);
					}

					entity.setDeviceId(deviceZoneDetailsDTO.getDeviceId());
					entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
					entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
					entity.setAlertCode(deviceZoneDetailsDTO.getAlertCode());
					entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
					entity.setInputType(deviceZoneDetailsDTO.getInputType());
					entity.setPrimaryFlag(deviceZoneDetailsDTO.getPrimaryFlag());
					entity.setCriticalAlertFlag(deviceZoneDetailsDTO.getCriticalAlertFlag());
					entity.setVendorId(deviceZoneDetailsDTO.getVendorId());
					entity.setVendors(deviceZoneDetailsDTO.getVendors());

					deviceZoneDetailsRepository.save(entity);

					if (deviceZoneDetailsDTO.getCameraMappings() != null
							&& !deviceZoneDetailsDTO.getCameraMappings().isEmpty()) {

						List<NonSiaZoneCameraMapping> cameraMappings = deviceZoneDetailsDTO.getCameraMappings().stream()
								.map(cameraDto -> {

									NonSiaZoneCameraMapping mapping = new NonSiaZoneCameraMapping();

									mapping.setZoneId(id);
									mapping.setCameraChannel(cameraDto.getCameraChannel());
									mapping.setNvrId(cameraDto.getNvrId());

									return mapping;

								}).toList();

						nonSiaZoneCameraMappingRepository.saveAll(cameraMappings);
					}

					System.out.println("Saved entity-->" + entity);

					responseList.add(entity);

					deviceZoneId++;

					System.out.println("Updated DeviceId-->" + deviceZoneId);

				} else {

					System.out.println("Device details already present in the database.");

					response.setMessage("Device details already present in the database.");
					response.setStatusCode(HttpStatus.BAD_REQUEST.value());

					return response;
				}
			}

			paramEntity.setDescp2(String.valueOf(deviceZoneId));

			paramRepository.save(paramEntity);

			response.setPayload(responseList);
			response.setMessage("Device zone details and camera mappings saved successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {

			e.printStackTrace();

			System.out.println("Error adding device zone details items: " + e.getMessage());

			response.setMessage("Error adding device zone details items: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity editMultipleDeviceZoneDetails(List<DeviceZoneDetailsDTO> deviceZoneDetailsDTOs) {

		ResponseEntity response = new ResponseEntity();

		try {

			ParamEntity paramEntity = paramRepository.findByCode("DEVICE_ZONE_ID");
			int deviceZoneId = Integer.parseInt(paramEntity.getDescp2());

			System.out.println("Initial DeviceId-->" + deviceZoneId);

			List<DeviceZoneDetailsEntity> responseList = new ArrayList<>();

			for (DeviceZoneDetailsDTO deviceZoneDetailsDTO : deviceZoneDetailsDTOs) {

				Optional<DeviceZoneDetailsEntity> optionalDeviceZoneDetails = deviceZoneDetailsRepository
						.findById(deviceZoneDetailsDTO.getId());

				if (optionalDeviceZoneDetails.isPresent()) {

					DeviceZoneDetailsEntity entity = optionalDeviceZoneDetails.get();

					entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
					entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
					entity.setAlertCode(deviceZoneDetailsDTO.getAlertCode());
					entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
					entity.setInputType(deviceZoneDetailsDTO.getInputType());
					entity.setPrimaryFlag(deviceZoneDetailsDTO.getPrimaryFlag());
					entity.setCriticalAlertFlag(deviceZoneDetailsDTO.getCriticalAlertFlag());
					entity.setVendorId(deviceZoneDetailsDTO.getVendorId());
					entity.setVendors(deviceZoneDetailsDTO.getVendors());

					deviceZoneDetailsRepository.save(entity);

					DynamicConfigurationEntity dynamicConfigrationEntity = dynamicConfigurationRepository
							.findByDeviceIdAndDeviceZoneId(deviceZoneDetailsDTO.getDeviceId(),
									deviceZoneDetailsDTO.getId());

					if (dynamicConfigrationEntity != null) {

						dynamicConfigrationEntity.setMessage(deviceZoneDetailsDTO.getMessage());
						dynamicConfigrationEntity.setAlertType(deviceZoneDetailsDTO.getAlertType());

						dynamicConfigurationRepository.save(dynamicConfigrationEntity);
					}

					// ================= Camera Mapping Update =================

					if (deviceZoneDetailsDTO.getCameraMappings() != null
							&& !deviceZoneDetailsDTO.getCameraMappings().isEmpty()) {

						// Remove existing camera mappings of this zone
						nonSiaZoneCameraMappingRepository.deleteByZoneId(deviceZoneDetailsDTO.getId());

						// Create new mappings
						List<NonSiaZoneCameraMapping> cameraMappings = deviceZoneDetailsDTO.getCameraMappings().stream()
								.map(cameraDto -> {

									NonSiaZoneCameraMapping mapping = new NonSiaZoneCameraMapping();

									mapping.setZoneId(deviceZoneDetailsDTO.getId());

									mapping.setCameraChannel(cameraDto.getCameraChannel());
									mapping.setNvrId(cameraDto.getNvrId());

									return mapping;

								}).toList();

						nonSiaZoneCameraMappingRepository.saveAll(cameraMappings);
					}

					// =========================================================

					responseList.add(entity);

				} else {

					DeviceZoneDetailsEntity entity = new DeviceZoneDetailsEntity();

					String id = paramEntity.getDescp1() + String.valueOf(deviceZoneId);

					System.out.println("Generated Id-->" + id);

					entity.setId(id);
					entity.setDeviceId(deviceZoneDetailsDTO.getDeviceId());
					entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
					entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
					entity.setAlertCode(deviceZoneDetailsDTO.getAlertCode());
					entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
					entity.setInputType(deviceZoneDetailsDTO.getInputType());
					entity.setPrimaryFlag(deviceZoneDetailsDTO.getPrimaryFlag());
					entity.setCriticalAlertFlag(deviceZoneDetailsDTO.getCriticalAlertFlag());
					entity.setVendorId(deviceZoneDetailsDTO.getVendorId());
					entity.setVendors(deviceZoneDetailsDTO.getVendors());

					deviceZoneDetailsRepository.save(entity);

					System.out.println("Saved entity-->" + entity);

					responseList.add(entity);

					deviceZoneId++;

					responseList.add(entity);
				}
			}

			paramEntity.setDescp2(String.valueOf(deviceZoneId));
			paramRepository.save(paramEntity);

			response.setPayload(responseList);
			response.setMessage("Device zone details updated successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {

			e.printStackTrace();

			System.out.println("Error editing device zone details items: " + e.getMessage());

			response.setMessage("Error editing device zone details items: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

//	public ResponseEntity editMultipleDeviceZoneDetails(List<DeviceZoneDetailsDTO> deviceZoneDetailsDTOs) {
//	    ResponseEntity response = new ResponseEntity();
//	    try {
//	    	
//	    	ParamEntity paramEntity = paramRepository.findByCode("DEVICE_ZONE_ID");
//	        int deviceZoneId = Integer.parseInt(paramEntity.getDescp2());
//	        System.out.println("Initial DeviceId-->" + deviceZoneId);
//	        List<DeviceZoneDetailsEntity> responseList = new ArrayList<>();
//	        for (DeviceZoneDetailsDTO deviceZoneDetailsDTO : deviceZoneDetailsDTOs) {
//	        	 
//	            Optional<DeviceZoneDetailsEntity> optionalDeviceZoneDetails = deviceZoneDetailsRepository.findById(deviceZoneDetailsDTO.getId());
//	            if (optionalDeviceZoneDetails.isPresent()) {
//	                DeviceZoneDetailsEntity entity = optionalDeviceZoneDetails.get();
//	                entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
//	                entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
//	                entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
//	                entity.setInputType(deviceZoneDetailsDTO.getInputType());
//	                deviceZoneDetailsRepository.save(entity);
//	                responseList.add(entity);
//	            } else {
//	            	
//	            	
//	            	
//	            	DeviceZoneDetailsEntity entity = new DeviceZoneDetailsEntity();
//	                String id = paramEntity.getDescp1() + String.valueOf(deviceZoneId);
//	                System.out.println("Generated Id-->" + id);
//	                entity.setId(id);
//	                entity.setDeviceId(deviceZoneDetailsDTO.getDeviceId());
//	                entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
//	                entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
//	                entity.setAlertType(deviceZoneDetailsDTO.getAlertType());
//	                entity.setInputType(deviceZoneDetailsDTO.getInputType());
//	                deviceZoneDetailsRepository.save(entity);
//	                System.out.println("Saved entity-->" + entity);
//
//	                responseList.add(entity);
//
//	                deviceZoneId++;  
//	                responseList.add(entity);
//	                
////	                response.setMessage("New Device added succesfully " + deviceZoneDetailsDTO.getId());
////	                response.setStatusCode(HttpStatus.OK.value());
//	               // return response;
//	            }
//	        }
//	        
//	        paramEntity.setDescp2(String.valueOf(deviceZoneId));
//	        paramRepository.save(paramEntity);
//	        
//	        response.setPayload(responseList);
//	        response.setMessage("Device zone details updated successfully.");
//	        response.setStatusCode(HttpStatus.OK.value());
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        System.out.println("Error editing device zone details items: " + e.getMessage());
//	        response.setMessage("Error editing device zone details items: " + e.getMessage());
//	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	    }
//	    return response;
//	}
//	

	/*
	 * public ResponseEntity addMultipleDeviceZoneDetails(List<DeviceZoneDetailsDTO>
	 * deviceZoneDetailsDTOs) { ResponseEntity response = new ResponseEntity(); try
	 * { List<DeviceZoneDetailsEntity> responseList = new ArrayList<>(); ParamEntity
	 * paramEntity = paramRepository.findByCode("DEVICE_ZONE_ID"); int deviceZoneId
	 * = Integer.parseInt(paramEntity.getDescp2());
	 * System.out.println("Initial DeviceId-->" + deviceZoneId);
	 * 
	 * for (DeviceZoneDetailsDTO deviceZoneDetailsDTO : deviceZoneDetailsDTOs) {
	 * DeviceZoneDetailsEntity deviceZoneDetails =
	 * deviceZoneDetailsRepository.getById(deviceZoneDetailsDTO.getId());
	 * 
	 * if (deviceZoneDetails == null) { DeviceZoneDetailsEntity entity = new
	 * DeviceZoneDetailsEntity(); String id = paramEntity.getDescp1() +
	 * String.valueOf(deviceZoneId); System.out.println("Generated Id-->" + id);
	 * entity.setId(id); entity.setDeviceId(deviceZoneDetailsDTO.getDeviceId());
	 * entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
	 * entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
	 * 
	 * deviceZoneDetailsRepository.save(entity);
	 * System.out.println("Saved entity-->" + entity);
	 * 
	 * responseList.add(entity);
	 * 
	 * deviceZoneId = deviceZoneId+1; System.out.println("iddd-->" +
	 * deviceZoneId);// Increment deviceZoneId after adding the entity } else {
	 * System.out.println("Device details already present in the database.");
	 * response.setMessage("Device details already present in the database.");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); return response; } }
	 * paramEntity.setDescp2(String.valueOf(deviceZoneId));
	 * 
	 * response.setPayload(responseList);
	 * response.setMessage("Device zone details saved successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); } catch (Exception e) {
	 * e.printStackTrace();
	 * System.out.println("Error adding device zone details items: " +
	 * e.getMessage());
	 * response.setMessage("Error adding device zone details items: " +
	 * e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); } return
	 * response; }
	 */

	/*
	 * public ResponseEntity addMultipleDeviceZoneDetails(List<DeviceZoneDetailsDTO>
	 * deviceZoneDetailsDTOs) { ResponseEntity response = new ResponseEntity(); try
	 * { List<DeviceZoneDetailsEntity> responseList = new ArrayList<>(); ParamEntity
	 * paramEntity = paramRepository.findByCode("DEVICE_ZONE_ID"); int deviceZoneId
	 * = Integer.parseInt(paramEntity.getDescp2());
	 * 
	 * for (DeviceZoneDetailsDTO deviceZoneDetailsDTO : deviceZoneDetailsDTOs) {
	 * Optional<DeviceZoneDetailsEntity> optionalDeviceZoneDetails =
	 * deviceZoneDetailsRepository.findById(deviceZoneDetailsDTO.getId());
	 * 
	 * if (!optionalDeviceZoneDetails.isPresent()) { DeviceZoneDetailsEntity entity
	 * = new DeviceZoneDetailsEntity(); String id = paramEntity.getDescp1() +
	 * String.valueOf(deviceZoneId); System.out.println("DeviceId-->" + id);
	 * deviceZoneId++; System.out.println("Incremented Id-->" + deviceZoneId);
	 * entity.setId(id); entity.setDeviceId(deviceZoneDetailsDTO.getDeviceId());
	 * entity.setZoneDesiredName(deviceZoneDetailsDTO.getZoneDesiredName());
	 * entity.setZoneOriginalName(deviceZoneDetailsDTO.getZoneOriginalName());
	 * 
	 * deviceZoneDetailsRepository.save(entity); System.out.println(entity);
	 * 
	 * responseList.add(entity); } else {
	 * response.setMessage("Device details already present in the database.");
	 * response.setStatusCode(HttpStatus.BAD_REQUEST.value()); return response; } }
	 * paramEntity.setDescp2(String.valueOf(deviceZoneId));
	 * 
	 * response.setPayload(responseList);
	 * response.setMessage("Device zone details saved successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); } catch (Exception e) {
	 * e.printStackTrace();
	 * response.setMessage("Error adding device zone details items: " +
	 * e.getMessage());
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); } return
	 * response; }
	 */

	public ResponseEntity getAllDistinctAlertTypes() {
		ResponseEntity response = new ResponseEntity();

		List<String> alertList = deviceZoneDetailsRepository.getAllAlertTypes();

		if (!alertList.isEmpty()) {
			response.setPayload(alertList);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		}
		return response;

	}

	/*
	 * public ResponseEntity deleteZoneByDeviceId(String deviceId, String id) {
	 * ResponseEntity response = new ResponseEntity();
	 * 
	 * DeviceZoneDetailsEntity deviceZoneDetailsEntity =
	 * deviceZoneDetailsRepository.findByDeviceIdAndId(deviceId, id);
	 * 
	 * if (deviceZoneDetailsEntity != null) {
	 * deviceZoneDetailsRepository.delete(deviceZoneDetailsEntity); }
	 * 
	 * response.setMessage("Device zone deleted successfully.");
	 * response.setStatusCode(HttpStatus.OK.value()); return response;
	 * 
	 * }
	 */
	@Transactional
	public ResponseEntity deleteZoneByDeviceId(String deviceId, String id) {

		DeviceZoneDetailsEntity zone = deviceZoneDetailsRepository.findByDeviceIdAndId(deviceId, id);

		if (zone == null) {
			ResponseEntity response = new ResponseEntity();
			response.setMessage("Zone not found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}
		nonSiaZoneCameraMappingRepository.deleteByZoneId(zone.getId());
		deviceZoneDetailsRepository.delete(zone);

		ResponseEntity response = new ResponseEntity();
		response.setMessage("Device zone and mappings deleted successfully.");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	public ResponseEntity getAllNoneSiaCameraMappingDetailsByZoneId(String zoneId) {

		ResponseEntity response = new ResponseEntity();

		try {

			List<NonSiaZoneCameraMapping> list = nonSiaZoneCameraMappingRepository.findByZoneId(zoneId);

			response.setMessage("Zone-Camera mapping fetched successfully by zoneId");
			response.setStatusCode(200);
			response.setPayload(list);

		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setStatusCode(500);
			response.setPayload(null);
		}

		return response;
	}
	
	 public ResponseEntity getAlertDetailsByDeviceSerialNo(String deviceSerialNo) {

	        ResponseEntity response = new ResponseEntity();

	        try {

	            if (deviceSerialNo == null || deviceSerialNo.trim().isEmpty()) {
	                response.setMessage("Device serial number is required");
	                response.setStatusCode(400);
	                response.setPayload(null);
	                return response;
	            }

	            DeviceMasterEntity device =
	                    deviceMasterRepository.findByDeviceSerialNo(deviceSerialNo.trim());

	            if (device == null) {
	                response.setMessage(
	                        "Device not found for serial number: " + deviceSerialNo);
	                response.setStatusCode(404);
	                response.setPayload(null);
	                return response;
	            }

	            String deviceId = device.getDeviceId();

	            List<DeviceZoneDetailsEntity> zoneDetailsList =
	                    deviceZoneDetailsRepository.findByDeviceId(deviceId);

	            if (zoneDetailsList == null || zoneDetailsList.isEmpty()) {
	                response.setMessage(
	                        "No zone details found for device: " + deviceSerialNo);
	                response.setStatusCode(404);
	                response.setPayload(null);
	                return response;
	            }

	            List<Map<String, Object>> alertDetails = new ArrayList<>();

	            for (DeviceZoneDetailsEntity zone : zoneDetailsList) {

	                Map<String, Object> alertMap = new HashMap<>();

	                alertMap.put("alertCode", zone.getAlertCode());
	                alertMap.put("alertName", zone.getAlertType());

	                alertDetails.add(alertMap);
	            }

	            Map<String, Object> payload = new HashMap<>();

				/*
				 * payload.put("deviceSerialNo", deviceSerialNo); payload.put("deviceId",
				 * deviceId);
				 */
	            payload.put("alertDetails", alertDetails);

	            response.setMessage("Alert details fetched successfully");
	            response.setStatusCode(200);
	            response.setPayload(payload);

	            return response;

	        } catch (Exception e) {

	            e.printStackTrace();

	            response.setMessage(
	                    "Error while fetching alert details: " + e.getMessage());
	            response.setStatusCode(500);
	            response.setPayload(null);

	            return response;
	        }
	    }
}
