package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.doritech.api.Common.General;
import com.doritech.api.DTO.AlertTypeDTO;
import com.doritech.api.DTO.SocialMediaDTO;
import com.doritech.api.Entity.SocialMediaEntity;
import com.doritech.api.Repository.SocialMediaRepository;

@Service
public class SocialMediaService {

	private static final Logger logger = LoggerFactory.getLogger(SocialMediaService.class);

	@Autowired
	private SocialMediaRepository socialMediaRepository;

	@Autowired
	private General general;

	public ResponseEntity<Object> saveSocialMediaDetails(List<SocialMediaDTO> socialMediaRequestList,
			HttpServletRequest httpServletRequest) {
		if (socialMediaRequestList.isEmpty()) {
			logger.warn("Received Empty SocialMediaDTO List");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not present in the Social Media request");
		} else {
			try {
				for (SocialMediaDTO socialMediaDTO : socialMediaRequestList) {
					SocialMediaEntity existingEntity = socialMediaRepository.findByBranchCodeAndAlertType(
							socialMediaDTO.getBranchCode(), socialMediaDTO.getAlertType());

					if (existingEntity != null) {

						existingEntity.setWhatsapp(socialMediaDTO.getWhatsapp());
						existingEntity.setSms(socialMediaDTO.getSms());
						existingEntity.setCalling(socialMediaDTO.getCalling());
						existingEntity.setMail(socialMediaDTO.getMail());
						existingEntity.setTimestamp(general.getCurrentTimeStamp().toString());
						existingEntity.setTerminal(httpServletRequest.getLocalAddr());
						socialMediaRepository.save(existingEntity);
						logger.info("Updated social media details for ID: {}", existingEntity.getId());
					} else {
						// Create a new entity
						SocialMediaEntity newEntity = new SocialMediaEntity(); 
						BeanUtils.copyProperties(socialMediaDTO, newEntity);
						newEntity.setTimestamp(general.getCurrentTimeStamp().toString());
						newEntity.setTerminal(httpServletRequest.getLocalAddr());
						socialMediaRepository.save(newEntity);
						logger.info("Saved new social media details for ID: {}", newEntity.getId());
					}
				}
				return ResponseEntity.status(HttpStatus.CREATED).body("Social media details saved successfully");
			} catch (Exception e) {
				logger.error("Error saving social media details", e);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("An error occurred while saving social media details");
			}
		}
	}

//	public ResponseEntity<Object> saveSocialMediaDetails(List<SocialMediaDTO> socialMediaRequestList, HttpServletRequest httpServletRequest) {
//	    if (socialMediaRequestList.isEmpty()) {
//	        logger.warn("Received Empty SocialMediaDTO List");
//	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Data not present in the Social Media request");
//	    } else {
//	        try {
//	            for (SocialMediaDTO socialMediaDTO : socialMediaRequestList) {
//	                SocialMediaEntity socialMediaEntity = new SocialMediaEntity();
//	                BeanUtils.copyProperties(socialMediaDTO, socialMediaEntity);
//	                Timestamp currentTimestamp = general.getCurrentTimeStamp();
//	                socialMediaEntity.setTimestamp(currentTimestamp.toString());
//	                socialMediaEntity.setTerminal(httpServletRequest.getLocalAddr());
//	                socialMediaRepository.save(socialMediaEntity);
//	                logger.info("Saved social media details for ID: {}", socialMediaEntity.getId());
//	            }
//	            return ResponseEntity.status(HttpStatus.CREATED).body("Social media details saved successfully");
//	        } catch (Exception e) {
//	            logger.error("Error saving social media details", e);
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                    .body("An error occurred while saving social media details");
//	        }
//	    }
//	}

	public ResponseEntity<List<SocialMediaDTO>> getSocialMediaDetailsByBranchCode(String branchCode) {

	    if (branchCode == null || branchCode.isEmpty()) {
	        logger.warn("Branch Code not present in the request");
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
	    }

	    try {
	        List<SocialMediaEntity> socialMediaEntityList = socialMediaRepository.findByBranchCode(branchCode);

	        if (socialMediaEntityList == null || socialMediaEntityList.isEmpty()) {
	            logger.info("Branch Code: " + branchCode + " not found in the database");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
	        }

	        List<SocialMediaDTO> socialMediaDTOList = new ArrayList<>();
	        for (SocialMediaEntity socialMediaEntity : socialMediaEntityList) {
	            SocialMediaDTO socialMediaDTO = new SocialMediaDTO();
	            BeanUtils.copyProperties(socialMediaEntity, socialMediaDTO);
	            socialMediaDTOList.add(socialMediaDTO);
	        }

	        logger.info("Successfully retrieved social media details for Branch Code: " + branchCode);
	        return ResponseEntity.status(HttpStatus.OK).body(socialMediaDTOList);

	    } catch (Exception e) {
	        logger.error("An error occurred while fetching details for Branch Code: " + branchCode, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	    }
	}


	///// -----------------------Get Alert Type for CheckBox-----------------////

	public com.doritech.api.Entity.ResponseEntity getSavedAlertTypes(String branchCode) {
		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();
		if (branchCode == null || branchCode.isEmpty()) {
			logger.warn("Branch Code not present in the request");
			response.setMessage("Branch Code not present in the request");
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
			return response;
		}

		try {

			List<SocialMediaEntity> socialMediaEntityList = socialMediaRepository.findByBranchCode(branchCode);

			if (socialMediaEntityList == null || socialMediaEntityList.isEmpty()) {
				logger.info("Branch Code: " + branchCode + " not found in the database");
				response.setMessage("Branch Code: " + branchCode + " not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			List<AlertTypeDTO> alertTypeDTOList = new ArrayList<>();
			for (SocialMediaEntity socialMediaEntity : socialMediaEntityList) {
				AlertTypeDTO alertTypeDTO = new AlertTypeDTO();
				alertTypeDTO.setAlertType(socialMediaEntity.getAlertType());
				alertTypeDTO.setWhatsapp(socialMediaEntity.getWhatsapp());
				alertTypeDTO.setSms(socialMediaEntity.getSms());
				alertTypeDTO.setCalling(socialMediaEntity.getCalling());
				alertTypeDTO.setMail(socialMediaEntity.getMail());
				alertTypeDTOList.add(alertTypeDTO);
			}

			logger.info("Successfully retrieved saved alert types for Branch Code: " + branchCode);
			response.setMessage("Successfully retrieved saved alert types for Branch Code: " + branchCode);
			response.setPayload(alertTypeDTOList);
			response.setStatusCode(HttpStatus.OK.value());
			return response;
		} catch (Exception e) {
			logger.error("An error occurred while fetching saved alert types for Branch Code: " + branchCode, e);
			response.setMessage("An error occurred while fetching saved alert types for Branch Code: " + branchCode
					+ ". Please try again later.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return response;
		}
	}
}
