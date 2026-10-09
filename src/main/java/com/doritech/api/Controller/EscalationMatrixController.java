package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.DTO.EscalationMatrixDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.EscalationMatrixService;

@RestController
@RequestMapping("/escalation-matrix")
public class EscalationMatrixController {

    @Autowired
    private EscalationMatrixService escalationMatrixService;

    // SAVE
    @PostMapping("/save")
    public ResponseEntity save(@RequestBody EscalationMatrixDTO dto) {
        return escalationMatrixService.save(dto);
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity update(@PathVariable Long id,
                                 @RequestBody EscalationMatrixDTO dto) {
        return escalationMatrixService.update(id, dto);
    }

    // GET BY ID
    @GetMapping("/getById/{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        return escalationMatrixService.getById(id);
    }

    // GET ALL WITH PAGINATION
    @GetMapping("/getAll")
    public ResponseEntity getAll(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return escalationMatrixService.getAll(page, size);
    }
}