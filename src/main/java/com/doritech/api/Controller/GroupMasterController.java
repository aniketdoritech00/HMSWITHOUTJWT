package com.doritech.api.Controller;

import com.doritech.api.DTO.GroupMasterDTO;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Service.GroupMasterService;
import com.doritech.api.Service.ParamService;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class GroupMasterController {
    @Autowired
    public GroupMasterService groupMasterService;
    @Autowired
    private EmployeeMasterRepository employeeMasterRepository;
    @Autowired
    private ParamService paramService;
    @Autowired
    private ParamRepository paramRepository;


    /**
     *  @Author Swati
     * */


    /**
     *  API TO GET GROUP MASTER BY GROUP ID
     * */
    @GetMapping("/getGroupById")
    @ResponseBody
    public ResponseEntity getGroupByGroupId(@RequestParam String groupId) throws Exception {
        return groupMasterService.getGroupByGroupId(groupId);
    }

    /**
     *  API TO GET ALL GROUP MASTER
     * */
    @GetMapping("/getAllGroupMaster")
    @ResponseBody
    public ResponseEntity getAllGroupMaster() throws Exception {
        return groupMasterService.getAllGroupMaster();
    }

    /**
     *  API TO SAVE OR UPDATE GROUP MASTER
     * */
    @PostMapping("/saveOrUpdateGroupMaster")
    @ResponseBody
    public ResponseEntity saveOrUpdateGroupMaster(@RequestBody GroupMasterDTO groupMasterDTO,HttpSession session) throws Exception {
        ParamEntity paramEntity = paramRepository.findByCode("GROUP_ID");
        groupMasterDTO.setGroupId(paramService.getGroupDescpValue());
        paramService.updateGroupDescp2Value(paramEntity);
        return groupMasterService.saveOrUpdateGroupMaster(groupMasterDTO,session);
    }

}