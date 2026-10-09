package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.DeviceZoneDetailsDTO;
import com.doritech.api.DTO.DynamicConfigrationDTO;
import com.doritech.api.Entity.DynamicConfigurationEntity;
import com.doritech.api.Repository.DynamicConfigurationRepository;

@Service
public class DynamicConfigrationService {

	private static final Logger logger = LoggerFactory.getLogger(SocialMediaService.class);

	@Autowired
	DynamicConfigurationRepository dynamicConfigurationRepository;

	public ResponseEntity<Object> saveDyanamicConfiguration(DeviceZoneDetailsDTO deviceZoneDetailsRequest) {
		if (deviceZoneDetailsRequest == null) {
			logger.warn("Received Empty DynamicConfigurationDTO List");
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Data not present in the Dynamic Configuration request");
		} else {
			try {
					DynamicConfigurationEntity dynamicConfigurationEntity = new DynamicConfigurationEntity();
					dynamicConfigurationEntity.setAlertType(deviceZoneDetailsRequest.getAlertType());
					dynamicConfigurationEntity.setDeviceId(deviceZoneDetailsRequest.getDeviceId());
					dynamicConfigurationEntity.setMessage(deviceZoneDetailsRequest.getMessage());
					dynamicConfigurationEntity.setZone(deviceZoneDetailsRequest.getZoneOriginalName());
					dynamicConfigurationEntity.setDeviceZoneId(deviceZoneDetailsRequest.getId());
					dynamicConfigurationEntity.setAlertFlag(deviceZoneDetailsRequest.getAlertFlag());
					dynamicConfigurationRepository.save(dynamicConfigurationEntity);
					logger.info("Saved Dynamic Configuration details for ID: {}", dynamicConfigurationEntity.getId());
				return ResponseEntity.status(HttpStatus.CREATED)
						.body("Dynamic Configuration details saved successfully");
			} catch (Exception e) {
				logger.error("Error saving Dyanmic Configuration details", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("An error occurred while saving Dnamic Configuration details");
			}
		}
	}
	public ResponseEntity<Object> getDynamicConfigurationForDeviceId(String deviceId) {
		if (deviceId == null || deviceId.isEmpty()) {
            logger.warn("Device Id not present in the request");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("DeviceId not present in the request");
        }

        try {
            List<DynamicConfigurationEntity> dynamicConfigurationEntityList = dynamicConfigurationRepository.findByDeviceId(deviceId);

            if (dynamicConfigurationEntityList == null || dynamicConfigurationEntityList.isEmpty()) {
                logger.info("Device Id -: " + deviceId + " not found in the database");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Device Id : " + deviceId + " not found");
            }

            List<DynamicConfigrationDTO> dynamicConfigurationList = new ArrayList<>();
            for (DynamicConfigurationEntity dynamicConfigurationEntity : dynamicConfigurationEntityList) {
            	DynamicConfigrationDTO dynamicConfigurationDTO = new DynamicConfigrationDTO();
                BeanUtils.copyProperties(dynamicConfigurationEntity, dynamicConfigurationDTO);
                dynamicConfigurationList.add(dynamicConfigurationDTO);
            }

            logger.info("Successfully retrieved dynamic Configuration  for Device Id " + deviceId);
            return ResponseEntity.status(HttpStatus.OK).body(dynamicConfigurationList);
        } catch (Exception e) {
            logger.error("An error occurred while fetching details for DeviceId: " + deviceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching Dyanmic Configuration details for Device Id: " + deviceId + ". Please try again later.");
        }
    }
	
	public ResponseEntity<Object> editDynamicConfigurationByDeviceId( DeviceZoneDetailsDTO deviceZoneDetailsRequest) {
        if (deviceZoneDetailsRequest == null) {
            logger.warn("Received Empty DeviceZoneDetailsDTO");
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Data not present in the Dynamic Configuration request");
        } else {
            try {
                Optional<DynamicConfigurationEntity> optionalDynamicConfig = dynamicConfigurationRepository.findByDeviceIdAndAlertType(deviceZoneDetailsRequest.getDeviceId(), deviceZoneDetailsRequest.getAlertType());
                
                if (optionalDynamicConfig.isPresent()) {
                    DynamicConfigurationEntity dynamicConfigurationEntity = optionalDynamicConfig.get();
                    dynamicConfigurationEntity.setMessage(deviceZoneDetailsRequest.getMessage());
                    dynamicConfigurationEntity.setZone(deviceZoneDetailsRequest.getZoneDesiredName());
                    dynamicConfigurationEntity.setAlertFlag(deviceZoneDetailsRequest.getAlertFlag());
                    dynamicConfigurationRepository.save(dynamicConfigurationEntity);
                    logger.info("Updated Dynamic Configuration details for ID: {}", dynamicConfigurationEntity.getId());
                    return ResponseEntity.status(HttpStatus.OK)
                            .body("Dynamic Configuration details updated successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Dynamic Configuration details not found for the given deviceId and alertType");
                }
            } catch (Exception e) {
                logger.error("Error updating Dynamic Configuration details", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while updating Dynamic Configuration details");
            }
        }
    }
}
