package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.doritech.api.DTO.BOIMasterDataDTO;
import com.doritech.api.Entity.BOIMasterDataEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.BOIMasterDataRepository;
import com.doritech.api.Repository.BranchMasterRepository;

@Service
public class BoiMasterDataService {

	@Autowired
	BOIMasterDataRepository boiMasterDataRepository;

	@Autowired
    BranchMasterRepository branchMasterRepository;
	
	
	private static final Logger logger = LoggerFactory.getLogger(BoiMasterDataService.class);

    @Autowired
    private BOIMasterDataRepository repository;

    public BOIMasterDataDTO saveBOIMasterData(BOIMasterDataDTO dto) {
        logger.info("saveBOIMasterData() called with bankName: {}, branchCode: {}",
                dto.getBankName(), dto.getBranchCode());
        try {
            if (dto.getBranchCode() == null || dto.getBranchCode().trim().isEmpty()) {
                logger.warn("Validation failed: branchCode is null or empty");
                throw new IllegalArgumentException("Branch code must not be null or empty");
            }
            if (dto.getBankName() == null || dto.getBankName().trim().isEmpty()) {
                logger.warn("Validation failed: bankName is null or empty");
                throw new IllegalArgumentException("Bank name must not be null or empty");
            }

            BOIMasterDataEntity entity = new BOIMasterDataEntity();
            
            BeanUtils.copyProperties(dto, entity);
            logger.debug("DTO mapped to Entity successfully: {}", entity.getBranchCode());
            entity.setBankName(dto.getBankName().toUpperCase());
            BOIMasterDataEntity savedEntity = repository.save(entity);
            logger.info("Entity saved successfully with ID: {}", savedEntity.getId());

            
            BOIMasterDataDTO savedDTO = new BOIMasterDataDTO();
            BeanUtils.copyProperties(savedEntity, savedDTO);
            logger.debug("Saved Entity mapped back to DTO successfully");

            return savedDTO;

        } catch (IllegalArgumentException e) {
            logger.error("Validation error while saving BOIMasterData: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error while saving BOIMasterData: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save BOI Master Data. Please try again.", e);
        }
    }
	
	
    public BOIMasterDataDTO updateBOIMasterData(Long id, BOIMasterDataDTO dto) {

        logger.info("updateBOIMasterData() called with ID: {}", id);

        try {
            BOIMasterDataEntity existingEntity = repository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Record not found with ID: {}", id);
                        return new RuntimeException("BOI Master Data not found with ID: " + id);
                    });

            logger.debug("Record found with ID: {}, proceeding with update", id);

            BeanUtils.copyProperties(dto, existingEntity, "id");
            logger.debug("DTO fields copied to existing entity successfully");

            BOIMasterDataEntity updatedEntity = repository.save(existingEntity);
            logger.info("Entity updated successfully with ID: {}", updatedEntity.getId());

            BOIMasterDataDTO updatedDTO = new BOIMasterDataDTO();
            BeanUtils.copyProperties(updatedEntity, updatedDTO);
            logger.debug("Updated Entity mapped back to DTO successfully");

            return updatedDTO;

        } catch (RuntimeException e) {
            logger.error("Error while updating BOIMasterData with ID {}: {}", id, e.getMessage());
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error while updating BOIMasterData with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update BOI Master Data. Please try again.", e);
        }
    }
    
    
	public ResponseEntity findAllBranchCodes() {
		ResponseEntity entity = new ResponseEntity();
		List<BOIMasterDataEntity> boiMasterDataEntities = boiMasterDataRepository.findAll();
		entity.setMessage("Data Fethed Successfully..");
		entity.setPayload(boiMasterDataEntities);
		entity.setStatusCode(HttpStatus.OK.value());
		return entity;
	}

	public ResponseEntity findBranchDataByMasterBranchCode(String branchCode) {
		ResponseEntity entity = new ResponseEntity();
		BOIMasterDataEntity boiMasterDataEntity = boiMasterDataRepository.findByBranchCode(branchCode);
		if (boiMasterDataEntity == null) {
			entity.setMessage("Data not Fethed by branch Code " + branchCode);
			entity.setStatusCode(HttpStatus.NO_CONTENT.value());
		} else {
			entity.setMessage("Data Fethed Successfully..");
			entity.setPayload(boiMasterDataEntity);
			entity.setStatusCode(HttpStatus.OK.value());

		}
		return entity;
	}

	public ResponseEntity findAllFgmo(String bankName) {
	    ResponseEntity entity = new ResponseEntity();

	    List<BOIMasterDataEntity> boiMasterDataEntities = boiMasterDataRepository.findByBankName(bankName);

	    if (boiMasterDataEntities.isEmpty()) {
	        entity.setMessage("Data not Fetched ..");
	        entity.setStatusCode(HttpStatus.NO_CONTENT.value());
	        return entity;
	    }

	    List<String> distinctFgmoList = boiMasterDataEntities.stream()
	    	    .filter(e -> e.getFgmo() != null )
	    	    .map(e -> e.getFgmo())
	    	    .distinct()
	    	    .collect(Collectors.toList());


	    entity.setMessage("Distinct fgmo fetched successfully");
	    entity.setPayload(distinctFgmoList);
	    entity.setStatusCode(HttpStatus.OK.value());

	    return entity;
	}

	public ResponseEntity findAllZoneNameByFgmo(String fgmo, String bankName) {
	    ResponseEntity entity = new ResponseEntity();

	    List<BOIMasterDataEntity> boiMasterDataEntities = boiMasterDataRepository.findByFgmoAndBankName(fgmo,bankName);

	    if (boiMasterDataEntities.isEmpty()) {
	        entity.setMessage("Data not Fetched ..");
	        entity.setStatusCode(HttpStatus.NO_CONTENT.value());
	        return entity;
	    }

	    List<String> distinctZoneList = boiMasterDataEntities.stream()
	    	    .filter(e -> e.getZoneName() != null )
	    	    .map(e -> e.getZoneName() )
	    	    .distinct()
	    	    .collect(Collectors.toList());

	    entity.setMessage("Distinct Zone Name fetched successfully");
	    entity.setPayload(distinctZoneList);
	    entity.setStatusCode(HttpStatus.OK.value());

	    return entity;
	}

	
	public ResponseEntity findAllBranchByZoneName(String zoneName,String bankName) {
		    ResponseEntity entity = new ResponseEntity();

		    List<BOIMasterDataEntity> boiMasterDataEntities = boiMasterDataRepository.findByZoneNameAndBankName(zoneName,bankName);

		    if (boiMasterDataEntities.isEmpty()) {
		        entity.setMessage("Data not Fetched ..");
		        entity.setStatusCode(HttpStatus.NO_CONTENT.value());
		        return entity;
		    }

		    List<String> distinctZoneList = boiMasterDataEntities.stream()
		    	    .filter(e -> e.getBranchName() != null )
		    	    .map(e -> e.getBranchName() )
		    	    .distinct()
		    	    .collect(Collectors.toList());

		    entity.setMessage("Distinct Branch Name fetched successfully");
		    entity.setPayload(distinctZoneList);
		    entity.setStatusCode(HttpStatus.OK.value());

		    return entity;
		}
	
	
	
	// ======= 1. All Distinct Banks =======
	public ResponseEntity getAllDistinctBanks() {
	    ResponseEntity response = new ResponseEntity();
	    logger.info("getAllDistinctBanks() called");
	    try {
	        List<String> banks = repository.findAllDistinctBankNames();
	        logger.info("Total distinct banks found: {}", banks.size());
	        response.setPayload(banks);
	        response.setMessage("Distinct banks fetched successfully");
	        response.setStatusCode(HttpStatus.OK.value());
	    } catch (Exception e) {
	        logger.error("Error while fetching distinct banks: {}", e.getMessage(), e);
	        response.setMessage("Failed to fetch distinct banks. Please try again.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}

	// ======= 2. Distinct FGMO by Bank Name =======
	public ResponseEntity getDistinctFgmoByBankName(String bankName) {
	    ResponseEntity response = new ResponseEntity();
	    logger.info("getDistinctFgmoByBankName() called with bankName: {}", bankName);
	    try {
	        if (bankName == null || bankName.trim().isEmpty()) {
	            logger.warn("Validation failed: bankName is null or empty");
	            throw new IllegalArgumentException("Bank name must not be null or empty");
	        }
	        List<String> fgmoList = repository.findDistinctFgmoByBankName(bankName.trim().toUpperCase());
	        logger.info("Total distinct FGMOs found for bank '{}': {}", bankName, fgmoList.size());
	        response.setPayload(fgmoList);
	        response.setMessage("FGMO list fetched successfully");
	        response.setStatusCode(HttpStatus.OK.value());
	    } catch (IllegalArgumentException e) {
	        logger.error("Validation error in getDistinctFgmoByBankName: {}", e.getMessage());
	        throw e;
	    } catch (Exception e) {
	        logger.error("Error while fetching FGMOs for bankName '{}': {}", bankName, e.getMessage(), e);
	        response.setMessage("Failed to fetch FGMO list. Please try again.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}

	// ======= 3. Distinct Zones by bankName / fgmo / both =======
	public ResponseEntity getDistinctZones(String bankName, String fgmo) {
	    ResponseEntity response = new ResponseEntity();
	    logger.info("getDistinctZones() called with bankName: {}, fgmo: {}", bankName, fgmo);
	    try {
	        boolean hasBankName = bankName != null && !bankName.trim().isEmpty();
	        boolean hasFgmo = fgmo != null && !fgmo.trim().isEmpty();

	        if (!hasBankName && !hasFgmo) {
	            logger.warn("Validation failed: both bankName and fgmo are null or empty");
	            throw new IllegalArgumentException("At least one of bankName or fgmo must be provided");
	        }

	        List<String> zones;
	        if (hasBankName && hasFgmo) {
	            logger.debug("Fetching zones by both bankName and fgmo");
	            zones = repository.findDistinctZoneByBankNameAndFgmo(
	                    bankName.trim().toUpperCase(), fgmo.trim().toUpperCase());
	        } else if (hasBankName) {
	            logger.debug("Fetching zones by bankName only");
	            zones = repository.findDistinctZoneByBankName(bankName.trim().toUpperCase());
	        } else {
	            logger.debug("Fetching zones by fgmo only");
	            zones = repository.findDistinctZoneByFgmo(fgmo.trim().toUpperCase());
	        }

	        logger.info("Total distinct zones found: {}", zones.size());
	        response.setPayload(zones);
	        response.setMessage("Zone list fetched successfully");
	        response.setStatusCode(HttpStatus.OK.value());
	    } catch (IllegalArgumentException e) {
	        logger.error("Validation error in getDistinctZones: {}", e.getMessage());
	        throw e;
	    } catch (Exception e) {
	        logger.error("Error while fetching zones: {}", e.getMessage(), e);
	        response.setMessage("Failed to fetch zone list. Please try again.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}
}
