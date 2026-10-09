package com.doritech.api.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.doritech.api.DTO.VendorBranchMappedDTO;
import com.doritech.api.Entity.VendorBranchMappedEntity;
import com.doritech.api.Service.VendorBranchService;

@RestController
@RequestMapping("/api/vendor-branch")
public class VendorBranchController {

    @Autowired
    private VendorBranchService vendorBranchService;

    @PostMapping("/save")
    public VendorBranchMappedEntity saveVendorBranch(@RequestBody VendorBranchMappedDTO dto) throws IOException {
        return vendorBranchService.saveVendorBranch(dto);
    }
    @GetMapping("/getAllVendorBranches")
    public List<VendorBranchMappedDTO> getAllVendorBranches() {
        return vendorBranchService.getAllVendorBranches();
    }

    @GetMapping("/getVendorBranchById")
    public VendorBranchMappedDTO getVendorBranchById(@RequestParam int id) {
        return vendorBranchService.getVendorBranchById(id);
    }
}
