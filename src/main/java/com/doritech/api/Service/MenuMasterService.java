package com.doritech.api.Service;

import com.doritech.api.Common.Message;
import com.doritech.api.DTO.MenuMasterDTO;
import com.doritech.api.Entity.MenuMasterEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.MenuMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuMasterService {
    private static final Logger logger = LoggerFactory.getLogger(MenuMasterService.class);
    @Autowired
    private MenuMasterRepository menuMasterRepository;

    
    /**
     *  SAVE OR UPDATE MENU DETAILS
     * */
    public ResponseEntity saveOrEditMenuDetails(MenuMasterDTO menuMasterDTO) {
        ResponseEntity response = new ResponseEntity();
        try {
            List<Message> messageList = validateMenuDetails(menuMasterDTO);
            MenuMasterEntity menuMaster = null;
            if(messageList.isEmpty()) {
                Optional<MenuMasterEntity> existingMenuMasterEntity = menuMasterRepository.findById(menuMasterDTO.getId());

                if(!existingMenuMasterEntity.isPresent()) {
                    menuMaster = new MenuMasterEntity();
                    menuMaster.setMenuName(menuMasterDTO.getMenuName());
                    menuMaster.setMenuHandlerName(menuMasterDTO.getMenuHandlerName());
                    menuMaster.setMenuIcon(menuMasterDTO.getMenuIcon());
                    menuMaster.setChildId(menuMasterDTO.getChildId());
                    menuMaster.setParentId(menuMasterDTO.getParentId());

                    menuMasterRepository.save(menuMaster);

                    response.setPayload(menuMaster);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("New Menu Details saved successfully.");
                } else {
                     menuMaster = existingMenuMasterEntity.get();

                    menuMaster.setMenuName(menuMasterDTO.getMenuName());
                    menuMaster.setMenuHandlerName(menuMasterDTO.getMenuHandlerName());
                    menuMaster.setMenuIcon(menuMasterDTO.getMenuIcon());
                    menuMaster.setChildId(menuMasterDTO.getChildId());
                    menuMaster.setParentId(menuMasterDTO.getParentId());

                    menuMasterRepository.save(menuMaster);
                    response.setPayload(menuMaster);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Menu Details updated successfully.");
                }
            } else {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Invalid data received");
                response.setPayload(messageList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     *  GET ALL MENU DETAILS
     * */
     public ResponseEntity getAllMenuDetails() {
         ResponseEntity response = new ResponseEntity();
         try{
             List<MenuMasterEntity> menuMasterEntityList = menuMasterRepository.findAll();
             response.setPayload(menuMasterEntityList);
             response.setStatusCode(HttpStatus.OK.value());
             response.setMessage("All Menu Details fetched successfully.");
         } catch (Exception e) {
             e.printStackTrace();
             response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
             response.setMessage("Unable to fetch data.");
         }
         return response;
     }
     /**
      *  ASSIGN MENU DETAILS
      * */
     public ResponseEntity assignMenuDetails(MenuMasterDTO menuMasterDTO) {

         ResponseEntity response = new ResponseEntity();

         try {
             MenuMasterEntity entity = new MenuMasterEntity();
             BeanUtils.copyProperties(menuMasterDTO, entity);
             menuMasterRepository.save(entity);
             response.setMessage("Menu Details assigned successfully");
             response.setPayload(entity);
             response.setStatusCode(HttpStatus.OK.value());

         }catch (Exception e) {
             e.printStackTrace();
             response.setMessage("Error occurred in assigning Menu Details:- " + e.getLocalizedMessage());
             response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
         }
         return response;
     }

     /**
      *  REVOKE MENU DETAILS
      * */
     public ResponseEntity revokeMenuDetails(int id) {
         ResponseEntity response=new ResponseEntity();
         try {
             Optional<MenuMasterEntity> optional= menuMasterRepository.findById(id);
             if(optional.isPresent()) {
                 MenuMasterEntity entity=optional.get();
                 menuMasterRepository.save(entity);
                 response.setMessage("training revoked");
                 response.setPayload(entity);
                 response.setStatusCode(HttpStatus.OK.value());
             }else {
                 response.setMessage("Menu Details not found with given id");
                 response.setStatusCode(HttpStatus.BAD_REQUEST.value());
             }
         }catch (Exception e) {
             e.printStackTrace();
             response.setMessage("Error occurred in revoking Menu Details:- " + e.getLocalizedMessage());
             response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

         }
         return response;
     }

    /**
     *  VALIDATIONS FOR ADDING MENU DETAILS
     * */
    public List<Message> validateMenuDetails(MenuMasterDTO menuMasterDTO) {
        List<Message> validationMessageList = new ArrayList<>();

        if(menuMasterDTO.getChildId()<0 || menuMasterDTO.getChildId()>1) {
            validationMessageList.add(new Message("Child Id", "Child Id is Invalid"));
        }
        if(menuMasterDTO.getParentId()<0 || menuMasterDTO.getParentId()>1) {
            validationMessageList.add(new Message("Parent Id", "Parent Id is Invalid"));
        }
        if(menuMasterDTO.getMenuName()==null || StringUtils.isEmpty(menuMasterDTO.getMenuName())) {
            validationMessageList.add(new Message("Menu Name", "Menu Name can't be null or empty"));
        }
        if(menuMasterDTO.getMenuHandlerName()==null || StringUtils.isEmpty(menuMasterDTO.getMenuHandlerName())) {
            validationMessageList.add(new Message("Menu Handler Name", "Menu Handler Name can't be null or empty"));
        }
        if(menuMasterDTO.getMenuIcon()==null || StringUtils.isEmpty(menuMasterDTO.getMenuIcon())) {
            validationMessageList.add(new Message("Menu Icon", "Menu Icon can't be null or empty"));
        }

        return validationMessageList;
    }

}
