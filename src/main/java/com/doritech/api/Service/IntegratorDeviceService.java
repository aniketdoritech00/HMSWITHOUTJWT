
package com.doritech.api.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.DTO.IntegratorDeviceMasterDTO;
import com.doritech.api.DTO.IntegratorPenalMasterDTO;
import com.doritech.api.DTO.IntegratorSimMasterDTO;
import com.doritech.api.Entity.IntegratorDeviceMasterEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.IntegratorDeviceMasterRepository;

@Service
public class IntegratorDeviceService {

	@Autowired
	private IntegratorDeviceMasterRepository repository;

	@Autowired
	private ServletContext servletContext;

	public ResponseEntity saveDevice(IntegratorPenalMasterDTO dto) {
		ResponseEntity response = new ResponseEntity();
		try {
			String uploadDir = servletContext.getRealPath("/uploads/");
			Files.createDirectories(Paths.get(uploadDir));
			String qcPath = saveFile(dto.getQcReportImage(), uploadDir);
			IntegratorDeviceMasterEntity entity = new IntegratorDeviceMasterEntity();
			entity.setProductType(dto.getProductType());
			entity.setPanelId(dto.getPanelId());
			entity.setPanelSerialNo(dto.getPanelSerialNo());
			entity.setQcReportImagePath(qcPath);
			repository.save(entity);
			response.setMessage("Integrator Device saved successfully..");
			response.setPayload(entity);
			response.setStatusCode(HttpStatus.OK.value());
			return response;
		} catch (IOException e) {
			return response;
		}
	}

	
	
	public ResponseEntity ContinueIntegratorDeviceForSim(IntegratorSimMasterDTO dto) throws IOException {
		ResponseEntity response = new ResponseEntity();
			String uploadDir = servletContext.getRealPath("/uploads/");
			Files.createDirectories(Paths.get(uploadDir));
			Optional<IntegratorDeviceMasterEntity> integratorDeviceMasterEntity = repository.findById(dto.getId());
			if(integratorDeviceMasterEntity.get() != null) {
				IntegratorDeviceMasterEntity device=integratorDeviceMasterEntity.get();	
				String m2mSimPath = saveFile(dto.getM2mSimImage(), uploadDir);
				IntegratorDeviceMasterEntity entity = new IntegratorDeviceMasterEntity();
				entity.setId(dto.getId());
				entity.setPanelId(device.getPanelId());
				entity.setPanelSerialNo(device.getPanelSerialNo());
				entity.setProductType(device.getProductType());
				entity.setQcReportImagePath(device.getQcReportImagePath());
				entity.setM2mSimNumber(dto.getM2mSimNumber());
				entity.setM2mSimImagePath(m2mSimPath);
				entity.setM2mSimSerialNumber(dto.getM2mSimSerialNumber());
				entity.setOpertaor(dto.getOpertaor());
				entity.setBranchName(dto.getBranchName());
				entity.setBranchCode(dto.getBranchCode());
				repository.save(entity);
				response.setMessage("Integrator Device edited successfully..");
				response.setPayload(entity);
				response.setStatusCode(HttpStatus.OK.value());
				return response;
			}else {
				response.setMessage("Integrator Device Not Found By Id :: "+dto.getId());
				response.setStatusCode(HttpStatus.NO_CONTENT.value());
			}
			return response;
	}
	
	private String saveFile(MultipartFile file, String uploadDir) throws IOException {
		if (file == null || file.isEmpty())
			return null;
		String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
		String filePath = uploadDir + fileName;
		file.transferTo(new File(filePath));
		return "uploads/" + fileName;
	}

	public ResponseEntity getAllDevicePanelSerialNo() {
		ResponseEntity response = new ResponseEntity();
		List<IntegratorDeviceMasterEntity> devices = repository.findAll();
		Set<String> panelSerialNos = new HashSet<>();
		for (IntegratorDeviceMasterEntity device : devices) {
			if (device.getPanelSerialNo() != null) {
				panelSerialNos.add(device.getPanelSerialNo());
			}
		}
		response.setMessage("All Penel Serial No Fetched Successfully..");
		response.setPayload(panelSerialNos);
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}
	public ResponseEntity getAllIntegratedDeviceDetails() {
		ResponseEntity response=new ResponseEntity();
		List<IntegratorDeviceMasterEntity> deviceMasterEntities=repository.findAll();
		if(deviceMasterEntities.isEmpty()) {
			response.setMessage("Data not Found..");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		response.setPayload(deviceMasterEntities);
		response.setMessage("All Data Found..");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}
	public ResponseEntity getDeviceByPanelSerialNo(String panelSerialNo) throws IllegalAccessException, InvocationTargetException {
		ResponseEntity response = new ResponseEntity();
		IntegratorDeviceMasterEntity device = repository.findByPanelSerialNo(panelSerialNo);
		IntegratorDeviceMasterDTO integratorDeviceMasterDTO = new IntegratorDeviceMasterDTO();
		BeanUtils.copyProperties(integratorDeviceMasterDTO, device);
		response.setMessage("Penel Serial Number Fetched Successfully..");
		response.setPayload(integratorDeviceMasterDTO);
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}
}
