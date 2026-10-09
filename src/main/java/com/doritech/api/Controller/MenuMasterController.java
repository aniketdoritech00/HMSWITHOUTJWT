package com.doritech.api.Controller;

import com.doritech.api.DTO.MenuMasterDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.MenuMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MenuMasterController {
    @Autowired
    MenuMasterService menuMasterService;
    /**
     *  @Author Swati
     * */


    /**
     *  API FOR SAVE OR EDIT THE MENU DETAILS
     * */
    @PostMapping("/saveOrEditMenuDetails")
    @ResponseBody
    public ResponseEntity saveOrEditMenuDetails(@RequestBody MenuMasterDTO menuMasterDTO) {
        return menuMasterService.saveOrEditMenuDetails(menuMasterDTO);
    }

    /**
     *  API FOR GET ALL MENU DETAILS
     * */
    @GetMapping("/getAllMenuDetails")
    @ResponseBody
    public ResponseEntity getAllMenuDetails() throws Exception {
        return menuMasterService.getAllMenuDetails();
    }

}
