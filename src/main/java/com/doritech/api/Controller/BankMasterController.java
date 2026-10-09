package com.doritech.api.Controller;

import com.doritech.api.DTO.BankMasterDTO;
import com.doritech.api.DTO.BankMasterRequestDTO;
import com.doritech.api.DTO.BankMasterResponseDTO;
import com.doritech.api.Service.BankMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class BankMasterController {
    
    @Autowired
    private BankMasterService bankMasterService;
    
    @PostMapping("/save")
    public ResponseEntity<BankMasterResponseDTO> saveBank(@Valid @RequestBody BankMasterRequestDTO requestDTO) {
        try {
            BankMasterDTO savedBank = bankMasterService.saveBank(requestDTO);
            BankMasterResponseDTO response = new BankMasterResponseDTO(
                savedBank.getBankCode(),
                savedBank.getBankName(),
                "Bank saved successfully with auto-generated bank code"
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            BankMasterResponseDTO response = new BankMasterResponseDTO();
            response.setMessage("Error saving bank: " + e.getMessage());
            response.setTimestamp(java.time.LocalDateTime.now());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/update/{bankCode}")
    public ResponseEntity<BankMasterResponseDTO> updateBank(
            @PathVariable String bankCode,
            @Valid @RequestBody BankMasterRequestDTO requestDTO) {
        try {
            BankMasterDTO updatedBank = bankMasterService.updateBank(bankCode, requestDTO);
            BankMasterResponseDTO response = new BankMasterResponseDTO(
                updatedBank.getBankCode(),
                updatedBank.getBankName(),
                "Bank updated successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            BankMasterResponseDTO response = new BankMasterResponseDTO();
            response.setMessage("Error updating bank: " + e.getMessage());
            response.setTimestamp(java.time.LocalDateTime.now());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/{bankCode}")
    public ResponseEntity<BankMasterResponseDTO> getBankByCode(@PathVariable String bankCode) {
        try {
            BankMasterDTO bank = bankMasterService.getBankByCode(bankCode);
            BankMasterResponseDTO response = new BankMasterResponseDTO(
                bank.getBankCode(),
                bank.getBankName(),
                "Bank retrieved successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            BankMasterResponseDTO response = new BankMasterResponseDTO();
            response.setMessage("Error retrieving bank: " + e.getMessage());
            response.setTimestamp(java.time.LocalDateTime.now());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<BankMasterDTO>> getAllBanks() {
        List<BankMasterDTO> banks = bankMasterService.getAllBanks();
        return new ResponseEntity<>(banks, HttpStatus.OK);
    }
    
    @DeleteMapping("/delete/{bankCode}")
    public ResponseEntity<BankMasterResponseDTO> deleteBank(@PathVariable String bankCode) {
        try {
            bankMasterService.deleteBank(bankCode);
            BankMasterResponseDTO response = new BankMasterResponseDTO();
            response.setMessage("Bank deleted successfully");
            response.setTimestamp(java.time.LocalDateTime.now());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            BankMasterResponseDTO response = new BankMasterResponseDTO();
            response.setMessage("Error deleting bank: " + e.getMessage());
            response.setTimestamp(java.time.LocalDateTime.now());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}