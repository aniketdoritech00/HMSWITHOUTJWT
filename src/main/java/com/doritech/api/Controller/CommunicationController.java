package com.doritech.api.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.doritech.api.DTO.CommunicationMappingDTO;
import com.doritech.api.Service.CommunicationMappingService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {
    
    @Autowired
    private CommunicationMappingService communicationService;
    
    @PostMapping
    public ResponseEntity<CommunicationMappingDTO> createCommunication(@RequestBody CommunicationMappingDTO dto) {
        try {
        	CommunicationMappingDTO createdDto = communicationService.createCommunication(dto);
            return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
        } catch (InvocationTargetException | IllegalAccessException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<CommunicationMappingDTO>> getAllCommunications() {
        try {
            List<CommunicationMappingDTO> dtoList = communicationService.getAllCommunications();
            return new ResponseEntity<>(dtoList, HttpStatus.OK);
        } catch (InvocationTargetException | IllegalAccessException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommunicationMappingDTO> getCommunicationById(@PathVariable Long id) {
        try {
        	CommunicationMappingDTO dto = communicationService.getCommunicationById(id);
            if (dto != null) {
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommunicationMappingDTO> updateCommunication(@PathVariable Long id, @RequestBody CommunicationMappingDTO dto) {
        try {
        	CommunicationMappingDTO updatedDto = communicationService.updateCommunication(id, dto);
            if (updatedDto != null) {
                return new ResponseEntity<>(updatedDto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunication(@PathVariable Long id) {
        boolean deleted = communicationService.deleteCommunication(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}