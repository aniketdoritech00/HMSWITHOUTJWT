package com.doritech.api.Service;

import com.doritech.api.DTO.MenuAccessDTO;
import com.doritech.api.Entity.MenuAccessEntity;
import com.doritech.api.Entity.MenuMasterEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.MenuMasterRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.MenuAccessRepository;
import com.microsoft.schemas.office.visio.x2012.main.ShapeSheetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuAccessService {
    private static final Logger logger = LoggerFactory.getLogger(MenuAccessService.class);
    @Autowired
    MenuAccessRepository menuAccessRepository;

    @Autowired
    ParamRepository paramRepo;
    @Autowired
    MenuMasterRepository menuMasterRepository;
    @Autowired
    MenuMasterService menuMasterService;

    /**
     *  @Author Swati
     * */

    /**
     *  SAVE OR EDIT ROLE MASTER
     * */
    public ResponseEntity saveOrEditRoleMaster(MenuAccessDTO menuAccessDTO) {
        ResponseEntity response = new ResponseEntity();
        try {
            Optional<MenuAccessEntity> existingRoleMaster = menuAccessRepository.findById(menuAccessDTO.getId());
            if (!existingRoleMaster.isPresent()) {
                MenuAccessEntity menuAccessEntity = new MenuAccessEntity();
                menuAccessEntity.setRoleName(menuAccessDTO.getRoleName());
                menuAccessEntity.setRoleId(getRoleIdByEmpRole(menuAccessDTO.getRoleName()));
                menuAccessEntity.setMenuId(menuAccessDTO.getMenuId());

                menuAccessRepository.save(menuAccessEntity);

                response.setPayload(menuAccessEntity);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Role Master added successfully");
            } else {
                MenuAccessEntity menuAccessEntity = existingRoleMaster.get();
                menuAccessEntity.setRoleName(menuAccessDTO.getRoleName());
                menuAccessEntity.setRoleId(getRoleIdByEmpRole(menuAccessDTO.getRoleName()));
                menuAccessEntity.setMenuId(menuAccessDTO.getMenuId());

                menuAccessRepository.save(menuAccessEntity);

                response.setPayload(menuAccessEntity);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Role Master updated successfully");
            }

        } catch (Exception e) {
            logger.error("Error processing role master", e);
            response.setMessage("Role Master can't be added in the database");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }


    /**
     *  ASSIGN MENU DETAILS
     * */

    public ResponseEntity assignMenuDetailsToRole(String empRole, int id) {
        ResponseEntity response = new ResponseEntity();
        try {
            Optional<MenuMasterEntity> menuMasterOptional = menuMasterRepository.findById(id);
            if (menuMasterOptional.isPresent()) {
                MenuMasterEntity menu = menuMasterOptional.get();
                menuMasterRepository.save(menu);

                MenuAccessEntity roleMaster = new MenuAccessEntity();
                roleMaster.setRoleName(empRole);
                roleMaster.setRoleId(getRoleIdByEmpRole(empRole));
                roleMaster.setMenuId(id);
                menuAccessRepository.save(roleMaster);

                response.setPayload(roleMaster);
                response.setStatusCode(HttpStatus.OK.value());
                response.setMessage("Menu Details assigned to the given Role successfully.");
            } else {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Menu Details are already assigned to a Role.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Error occurred in assigning Menu Details: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     *  REVOKE MENU DETAILS
     * */

    public ResponseEntity revokeMenuDetailsFromTheRole(String empRole, int id) {
        ResponseEntity response = new ResponseEntity();
        try {
            Optional<MenuMasterEntity> menuMasterEntity = menuMasterRepository.findById(id);
            if (menuMasterEntity.isPresent()) {
                MenuAccessEntity role = menuAccessRepository.findByRoleNameAndMenuId(empRole, id);
                if (role!=null) {
                    response.setPayload(role);
                    menuAccessRepository.delete(role);

                    MenuMasterEntity menu = menuMasterEntity.get();

                    menuMasterRepository.save(menu);

                    response.setMessage("Menu Details revoked from the Role successfully");
                    response.setStatusCode(HttpStatus.OK.value());
                } else {
                    response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                    response.setMessage("Menu Details not found for the specified Role and Menu ID");
                }
            } else {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Provided data is not correct");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Error occurred in revoking Menu Details: " + e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public ResponseEntity getMenuDetailsByRole(String empRole) throws Exception {
        ResponseEntity response = new ResponseEntity();
        try {
            List<MenuAccessEntity> menuDetailsByRole = menuAccessRepository.findByRoleName(empRole);
            List<MenuMasterEntity> allMenuDetails = menuMasterRepository.findAll();

            List<MenuMasterEntity> responseList = new ArrayList<>();

            for(MenuMasterEntity menuMaster : allMenuDetails) {
                for(MenuAccessEntity menuAccess : menuDetailsByRole) {
                    if(menuAccess.getMenuId() == menuMaster.getId()) {
                        responseList.add(menuMaster);
                    }
                }
            }
            if(responseList != null) {
                response.setPayload(responseList);
                response.setMessage("Menu Details Based on Role fetched successfully.");
                response.setStatusCode(HttpStatus.OK.value());
            } else {
                response.setMessage("No Menu Details are present with the given role.");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Unable to fetch Menu Details");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }


    /**
     *  GET ROLE ID BY ROLE TYPE
     * */
    private int getRoleIdByEmpRole(String empRole) {
        if(empRole.equals("admin")) {
            return 101;
        } else if(empRole.equals("trainer")) {
            return 102;
        } else if(empRole.equals("trainee")) {
            return 103;
        } else
            return 0;
    }

    public ResponseEntity getAllRoles() {
        ResponseEntity response = new ResponseEntity();
        try {
            List<ParamEntity> roles = new ArrayList<ParamEntity>();
            List<ParamEntity> allParams = paramRepo.findAll();
            for(ParamEntity param : allParams) {
                if(param.getCode().equals("ROLE")) {
                    roles.add(param);
                }
            }

            response.setPayload(roles);
            response.setMessage("All Roles fetched successfully.");
            response.setStatusCode(HttpStatus.OK.value());
        }catch(Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Roles not found");
        }
        return response;
    }

}
