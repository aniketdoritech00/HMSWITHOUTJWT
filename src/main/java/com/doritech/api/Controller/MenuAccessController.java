package com.doritech.api.Controller;

import com.doritech.api.DTO.MenuAccessDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.MenuAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MenuAccessController {
    @Autowired
    private MenuAccessService menuAccessService;

    /**
     *  @Author Swati
     * */

    /**
     *  API FOR SAVE OR EDIT THE ROLE MASTER
     * */
    @PostMapping("/saveOrEditRoleMaster")
    @ResponseBody
    public ResponseEntity saveOrEditRoleMaster(@RequestBody MenuAccessDTO menuAccessDTO) {
        return menuAccessService.saveOrEditRoleMaster(menuAccessDTO);
    }

    @GetMapping("/getAllRoles")
    @ResponseBody
    public ResponseEntity getAllRoles() {
        return menuAccessService.getAllRoles();
    }

    @PutMapping("/assignMenuDetailsToRole")
    @ResponseBody
    public ResponseEntity assignMenuDetailsToRole(@RequestParam("empRole") String empRole,
                                                  @RequestParam("menuId") int id) {
        return menuAccessService.assignMenuDetailsToRole(empRole, id);
    }

    @PutMapping("/revokeMenuDetailsFromTheRole")
    @ResponseBody
    public ResponseEntity revokeMenuDetailsFromTheRole(@RequestParam("empRole") String empRole,
                                                       @RequestParam("menuId") int id) {
        return menuAccessService.revokeMenuDetailsFromTheRole(empRole, id);
    }

    @GetMapping("/getMenuDetailsByRole")
    @ResponseBody
    public ResponseEntity getMenuDetailsByRole(@RequestParam String empRole) throws Exception {
        return menuAccessService.getMenuDetailsByRole(empRole);
    }
}
