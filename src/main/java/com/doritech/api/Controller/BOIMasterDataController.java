package com.doritech.api.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.BOIMasterDataDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.BoiMasterDataService;

@RestController
public class BOIMasterDataController {

    @Autowired
    BoiMasterDataService boiMasterDataService;

    private static final Logger logger = LoggerFactory.getLogger(BOIMasterDataController.class);


    @PostMapping("/saveBOIMasterData")
    public ResponseEntity saveBOIMasterData(@RequestBody BOIMasterDataDTO dto) {
        ResponseEntity response = new ResponseEntity();
        logger.info("POST /saveBOIMasterData - Request received");

        BOIMasterDataDTO savedDTO = boiMasterDataService.saveBOIMasterData(dto);
        logger.info("POST /saveBOIMasterData - Data saved successfully with ID: {}", savedDTO.getBranchName());
        response.setPayload(savedDTO);
        response.setMessage("Branch Data saved Successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/editBOIMasterData")
    public ResponseEntity editBOIMasterData(@RequestParam Long id,
            @RequestBody BOIMasterDataDTO dto) {
        ResponseEntity response = new ResponseEntity();
        logger.info("PUT /editBOIMasterData - Request received");

        BOIMasterDataDTO editdDTO = boiMasterDataService.updateBOIMasterData(id, dto);
        logger.info("PUT /editBOIMasterData - Data edited successfully with ID: {}", editdDTO.getBranchName());
        response.setPayload(editdDTO);
        response.setMessage("Branch Data edited Successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    @GetMapping("/findAllBranchCodes")
    public ResponseEntity findAllBranchCodes() {
        return boiMasterDataService.findAllBranchCodes();
    }

    @GetMapping("/findBranchDataByMasterBranchCode")
    public ResponseEntity findBranchDataByMasterBranchCode(String branchCode) {
        return boiMasterDataService.findBranchDataByMasterBranchCode(branchCode);
    }

    @GetMapping("/findAllFgmo")
    public ResponseEntity findAllFgmo(@RequestParam String bankName) {
        return boiMasterDataService.findAllFgmo(bankName);
    }

    @GetMapping("/findAllZoneNameByFgmo")
    public ResponseEntity findAllZoneNameByFgmo(@RequestParam String fgmo, @RequestParam String bankName) {
        return boiMasterDataService.findAllZoneNameByFgmo(fgmo, bankName);
    }

    @GetMapping("/findAllBranchByZoneName")
    public ResponseEntity findAllBranchByZoneName(@RequestParam String branchCode, @RequestParam String bankName) {
        return boiMasterDataService.findAllBranchByZoneName(branchCode, bankName);
    }

    // ======= 1. All Distinct Banks =======
    @GetMapping("/findAllDistinctBanks")
    public ResponseEntity findAllDistinctBanks() {
        ResponseEntity response = new ResponseEntity();
        logger.info("GET /findAllDistinctBanks - Request received");
        try {
            response = boiMasterDataService.getAllDistinctBanks();
            logger.info("GET /findAllDistinctBanks - Banks fetched successfully");
        } catch (Exception e) {
            logger.error("GET /findAllDistinctBanks - Error: {}", e.getMessage(), e);
            response.setMessage("Internal server error. Please try again later.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    // ======= 2. Distinct FGMO by Bank Name =======
    @GetMapping("/findDistinctFgmoByBankName")
    public ResponseEntity findDistinctFgmoByBankName(@RequestParam String bankName) {
        ResponseEntity response = new ResponseEntity();
        logger.info("GET /findDistinctFgmoByBankName - Request received with bankName: {}", bankName);
        try {
            response = boiMasterDataService.getDistinctFgmoByBankName(bankName);
            logger.info("GET /findDistinctFgmoByBankName - FGMOs fetched successfully for bankName: {}", bankName);
        } catch (IllegalArgumentException e) {
            logger.warn("GET /findDistinctFgmoByBankName - Validation error: {}", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error("GET /findDistinctFgmoByBankName - Error: {}", e.getMessage(), e);
            response.setMessage("Internal server error. Please try again later.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    // ======= 3. Distinct Zones by bankName / fgmo / both =======
    @GetMapping("/findDistinctZones")
    public ResponseEntity findDistinctZones(
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String fgmo) {
        ResponseEntity response = new ResponseEntity();
        logger.info("GET /findDistinctZones - Request received with bankName: {}, fgmo: {}", bankName, fgmo);
        try {
            response = boiMasterDataService.getDistinctZones(bankName, fgmo);
            logger.info("GET /findDistinctZones - Zones fetched successfully");
        } catch (IllegalArgumentException e) {
            logger.warn("GET /findDistinctZones - Validation error: {}", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        } catch (Exception e) {
            logger.error("GET /findDistinctZones - Error: {}", e.getMessage(), e);
            response.setMessage("Internal server error. Please try again later.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }
}