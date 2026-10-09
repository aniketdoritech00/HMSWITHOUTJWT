package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.DeviceDropdownDTO;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.ParamRepository;

// DeviceBranchService.java
@Service
public class DeviceBranchService {
    
    @Autowired
    private BranchMasterService branchService;
    
    @Autowired
    private DeviceMasterRepository deviceMasterRepository;
    
    @Autowired
    ParamRepository paramRepository;
    
    /**
     * Get device serial numbers with their branch descriptions for dropdown
     */
    public List<DeviceDropdownDTO> getDeviceListForDropdown(String branchCode) {
        List<DeviceDropdownDTO> dropdownList = new ArrayList<>();
        
        try {
            // Step 1: Get all child branches
            ResponseEntity childBranchResponse = branchService.getAllChildren(branchCode);
            List<BranchMasterEntity> branches = (List<BranchMasterEntity>) childBranchResponse.getPayload();
            
            // Step 2: Extract branch codes
            List<String> branchCodes = branches.stream()
                    .map(BranchMasterEntity::getBranchCode)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            // Step 3: Create branch code to description map
            Map<String, String> branchDescMap = branches.stream()
                    .filter(b -> b.getBranchCode() != null)
                    .collect(Collectors.toMap(
                        BranchMasterEntity::getBranchCode,
                        b -> b.getBranchDesc() != null ? b.getBranchDesc() : "",
                        (existing, replacement) -> existing
                    ));
            
            // Step 4: Get all devices for these branches
            List<DeviceMasterEntity> allDevices = deviceMasterRepository.findAll();
            List<DeviceMasterEntity> filteredDevices = allDevices.stream()
                    .filter(device -> branchCodes.contains(device.getBranchCode()))
                    .collect(Collectors.toList());
            
            // Step 5: Create dropdown list
            for (DeviceMasterEntity device : filteredDevices) {
                String branchDesc = branchDescMap.get(device.getBranchCode());
                dropdownList.add(new DeviceDropdownDTO(device.getDeviceSerialNo(), branchDesc));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dropdownList;
    }
    
    public List<ParamEntity> getParamByCode(String code){
		List<ParamEntity> params=paramRepository.findAllByCode("STATUS");
		return params;
	}
}