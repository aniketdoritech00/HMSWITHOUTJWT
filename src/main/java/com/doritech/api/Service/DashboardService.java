package com.doritech.api.Service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Common.General;
import com.doritech.api.DTO.DashboardDTO;
import com.doritech.api.Entity.DashboardEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.DashboardRepository;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class DashboardService {

    @Autowired
    DashboardRepository dashboardRepository;
    @Autowired
    General general;
    

    public ResponseEntity saveOrUpdateDashboardEntity(DashboardDTO dashboardDTO) throws IllegalAccessException, InvocationTargetException {
        ResponseEntity response = new ResponseEntity();
    	if (dashboardDTO == null) {
        	response.setMessage("Bad request");
        	response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        	
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            DashboardEntity dashboardEntity;
            
            DashboardEntity dashboardEntity2 = dashboardRepository.findByEmpId(dashboardDTO.getEmpId());
            
            
            if (dashboardEntity2!=null) {
                dashboardEntity = dashboardRepository.findByEmpId(dashboardDTO.getEmpId());
                dashboardEntity.setDashboardNo(dashboardDTO.getDashboardNo());
                dashboardEntity.setTimeStamp(String.valueOf(general.getCurrentTimeStamp()));
            } else {
                dashboardEntity = new DashboardEntity();
                BeanUtils.copyProperties(dashboardEntity, dashboardDTO);
            }

            
            dashboardRepository.save(dashboardEntity);
            

            response.setMessage("Data saved Succesfully");
        	response.setStatusCode(HttpStatus.OK.value());
        	
        }
    	return response;
    }
    
    public ResponseEntity getByEmpId(String empId) throws IllegalAccessException, InvocationTargetException {
    	ResponseEntity response = new ResponseEntity();
        DashboardEntity dashboardEntity = dashboardRepository.findByEmpId(empId);
        if (dashboardEntity == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        	
        	response.setStatusCode(HttpStatus.NOT_FOUND.value());
        } else {
            DashboardDTO dashboardDTO = new DashboardDTO();
            BeanUtils.copyProperties(dashboardDTO, dashboardEntity);
            response.setPayload(dashboardDTO);
            response.setStatusCode(HttpStatus.OK.value());
            
        }
        return response;
    }
}
