package com.doritech.api.Service;

import java.sql.RowId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.doritech.api.DTO.CameraDto;
import com.doritech.api.DTO.HardDiskDto;
import com.doritech.api.DTO.NvrRequest;
import com.doritech.api.Entity.CameraMaster;
import com.doritech.api.Entity.CameraType;
import com.doritech.api.Entity.DiskType;
import com.doritech.api.Entity.NvrConnectionDetails;
import com.doritech.api.Entity.NvrHardDisk;
import com.doritech.api.Entity.NvrMaster;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Repository.CameraTypeRepository;
import com.doritech.api.Repository.DiskTypeRepository;
import com.doritech.api.Repository.NvrConnectionDetailsRepository;
import com.doritech.api.Repository.NvrMasterRepository;
import com.doritech.api.Repository.ParamRepository;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class NvrMasterService {

	@Autowired
	private NvrMasterRepository repository;
	@Autowired
	private CameraTypeRepository cameraTypeRepository;
	@Autowired
	private DiskTypeRepository diskTypeRepository;
	@Autowired
	private ParamRepository paramRepository;

	@Autowired
	private NvrConnectionDetailsRepository nvrConnectionDetailsRepository;

	// CREATE
	public ResponseEntity<?> createNvr(NvrRequest req) {

		try {

			NvrMaster nvr = new NvrMaster();

			nvr.setNvrName(req.getNvrName());
			nvr.setVendorId(req.getVendorId());
			nvr.setEmail(req.getEmail());
			nvr.setNvrModel(req.getNvrModel());
			nvr.setIpAddress(req.getIpAddress());
			nvr.setPortNo(req.getPortNo());
			nvr.setFirmwareVersion(req.getFirmwareVersion());
			nvr.setInstallationDate(req.getInstallationDate());
			nvr.setMacAddress(req.getMacAddress());
			nvr.setNvrSerialNo(req.getNvrSerialNo());
			nvr.setPanelSerialNo(req.getPanelSerialNo());
			nvr.setTotalChannels(req.getTotalChannels());
			nvr.setTotalHardDisks(req.getTotalHardDisks());
			nvr.setLocation(req.getLocation());
			nvr.setStatus(req.getStatus());

			List<CameraMaster> cameras = new ArrayList<>();

			if (req.getCameras() != null) {

				for (CameraDto c : req.getCameras()) {

					CameraMaster cam = new CameraMaster();

					cam.setCameraName(c.getCameraName());
					cam.setCameraType(c.getCameraType());
					cam.setCameraPort(c.getCameraPort());
					cam.setIpAddress(c.getIpAddress());
					cam.setPixelResolution(c.getPixelResolution());
					cam.setChannelNo(c.getChannelNo());
					cam.setModel(c.getModel());
					cam.setCameraSerialNo(c.getCameraSerialNo());
					cam.setStatus(c.getStatus());
					cam.setNvr(nvr);

					cameras.add(cam);
				}
			}

			List<NvrHardDisk> disks = new ArrayList<>();

			if (req.getHardDisks() != null) {

				for (HardDiskDto h : req.getHardDisks()) {

					NvrHardDisk disk = new NvrHardDisk();
					disk.setHardDiskName(h.getHardDiskName());
					disk.setCapacityTb(h.getCapacityTb());
					disk.setFreeSpace(h.getFreeSpace());
					disk.setDiskType(h.getDiskType());
					disk.setHardDiskSerialNo(h.getHardDiskSerialNo());
					disk.setHealthStatus(h.getHealthStatus());
					disk.setStatus(h.getStatus());
					disk.setNvr(nvr);

					disks.add(disk);
				}
			}

			nvr.setCameras(cameras);
			nvr.setHardDisks(disks);

			return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(nvr));

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Create Failed : " + e.getMessage());
		}
	}

	// GET ALL
	public ResponseEntity<?> getAll() {

		try {

			return ResponseEntity.ok(repository.findAll());

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fetch Failed : " + e.getMessage());
		}
	}

	// GET BY ID
	public ResponseEntity<?> getById(Long id) {

		try {

			NvrMaster nvr = repository.findById(id)
					.orElseThrow(() -> new RuntimeException("NVR Not Found With Id : " + id));

			return ResponseEntity.ok(nvr);

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// UPDATE NVR + CAMERAS + HARD DISKS
	public ResponseEntity<?> updateNvr(Long id, NvrRequest req) {

		try {

			NvrMaster nvr = repository.findById(id)
					.orElseThrow(() -> new RuntimeException("NVR Not Found With Id : " + id));

			nvr.setNvrName(req.getNvrName());
			nvr.setVendorId(req.getVendorId());
			nvr.setEmail(req.getEmail());
			nvr.setNvrModel(req.getNvrModel());
			nvr.setIpAddress(req.getIpAddress());
			nvr.setPortNo(req.getPortNo());
			nvr.setFirmwareVersion(req.getFirmwareVersion());
			nvr.setInstallationDate(req.getInstallationDate());
			nvr.setMacAddress(req.getMacAddress());
			nvr.setNvrSerialNo(req.getNvrSerialNo());
			nvr.setPanelSerialNo(req.getPanelSerialNo());
			nvr.setTotalChannels(req.getTotalChannels());
			nvr.setTotalHardDisks(req.getTotalHardDisks());
			nvr.setStatus(req.getStatus());
			nvr.setLocation(req.getLocation());
			// Update Cameras
			nvr.getCameras().clear();

			if (req.getCameras() != null) {

				for (CameraDto c : req.getCameras()) {

					CameraMaster cam = new CameraMaster();

					cam.setCameraName(c.getCameraName());
					cam.setCameraType(c.getCameraType());
					cam.setCameraPort(c.getCameraPort());
					cam.setModel(c.getModel());
					cam.setPixelResolution(c.getPixelResolution());
					cam.setIpAddress(c.getIpAddress());
					cam.setChannelNo(c.getChannelNo());
					cam.setStatus(c.getStatus());
					cam.setCameraSerialNo(c.getCameraSerialNo());
					cam.setNvr(nvr);

					nvr.getCameras().add(cam);
				}
			}

			// Update Hard Disks
			nvr.getHardDisks().clear();

			if (req.getHardDisks() != null) {

				for (HardDiskDto h : req.getHardDisks()) {

					NvrHardDisk disk = new NvrHardDisk();

					disk.setHardDiskName(h.getHardDiskName());
					disk.setCapacityTb(h.getCapacityTb());
					disk.setDiskType(h.getDiskType());
					disk.setFreeSpace(h.getFreeSpace());
					disk.setHardDiskSerialNo(h.getHardDiskSerialNo());
					disk.setHealthStatus(h.getHealthStatus());
					disk.setStatus(h.getStatus());
					disk.setNvr(nvr);

					nvr.getHardDisks().add(disk);
				}
			}

			NvrMaster updated = repository.save(nvr);

			return ResponseEntity.ok(updated);

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update Failed : " + e.getMessage());
		}
	}

	// DELETE
	public ResponseEntity<?> delete(Long id) {

		try {

			NvrMaster nvr = repository.findById(id)
					.orElseThrow(() -> new RuntimeException("NVR Not Found With Id : " + id));

			repository.delete(nvr);

			return ResponseEntity.ok("Deleted Successfully");

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete Failed : " + e.getMessage());
		}
	}

	public com.doritech.api.Entity.ResponseEntity getAllActiveCameraTypes() {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			List<CameraType> cameraTypes = cameraTypeRepository.findByStatus("Active");

			response.setMessage("Active Camera Types fetched successfully");
			response.setStatusCode(200);
			response.setPayload(cameraTypes);

		} catch (Exception e) {

			response.setMessage("Failed to fetch Active Camera Types");
			response.setStatusCode(500);
			response.setPayload(null);
		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getAllActiveDiskTypes() {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			List<DiskType> list = diskTypeRepository.findByStatus("Active");

			response.setMessage("Active Disk Types fetched successfully");
			response.setStatusCode(200);
			response.setPayload(list);

		} catch (Exception e) {

			response.setMessage("Failed to fetch Disk Types");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getDiskHealthStatus(String code, String serial) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			List<ParamEntity> list = paramRepository.getParamDataByCodeAndSerial(code, serial);

			List<String> healthStatus = list.stream().map(ParamEntity::getDescp1).toList();

			response.setMessage("Disk health status fetched successfully");
			response.setStatusCode(200);
			response.setPayload(healthStatus);

		} catch (Exception e) {

			response.setMessage("Failed to fetch disk health status");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity save(NvrConnectionDetails details) {

		NvrConnectionDetails save = nvrConnectionDetailsRepository.save(details);

		return new com.doritech.api.Entity.ResponseEntity("NVR Connection Create successfully: ", 200, save);

	}

	public com.doritech.api.Entity.ResponseEntity getAllNvrConnectionDetails() {

		return new com.doritech.api.Entity.ResponseEntity("NVR Connection fetch successfully: ", 200,
				nvrConnectionDetailsRepository.findAll());
	}

	public com.doritech.api.Entity.ResponseEntity getByIdNvrConnectionDetails(Long id) {
		Optional<NvrConnectionDetails> byId = nvrConnectionDetailsRepository.findById(id);

		return new com.doritech.api.Entity.ResponseEntity(
				byId.isPresent() ? "NVR Connection Fetch successfully: " : "NVR not Found ", 200,
				byId.isPresent() ? byId : null);
	}

	public com.doritech.api.Entity.ResponseEntity updateNvrConnectionDetails(Long id, NvrConnectionDetails details) {

		Optional<NvrConnectionDetails> existingRecord = nvrConnectionDetailsRepository.findById(id);

		if (existingRecord.isPresent()) {

			NvrConnectionDetails existing = existingRecord.get();

			existing.setNvrIp(details.getNvrIp());
			existing.setNvrPort(details.getNvrPort());
			existing.setNvrUrl(details.getNvrUrl());
			existing.setUsername(details.getUsername());
			existing.setPassword(details.getPassword());
			existing.setPanelSerial(details.getPanelSerial());
			existing.setRtspPort(details.getRtspPort());
			existing.setRtspUrl(details.getRtspUrl());
			existing.setNvr(details.getNvr());

			nvrConnectionDetailsRepository.save(existing);

			return new com.doritech.api.Entity.ResponseEntity("NVR Connection Update  successfully: ", 200, null);
		}
		return new com.doritech.api.Entity.ResponseEntity("NVR Connection Details not found with id : " + id, 200,
				null);
	}

	public com.doritech.api.Entity.ResponseEntity deleteNvrConnectionDetails(Long id) {

		if (!nvrConnectionDetailsRepository.existsById(id)) {

			return new com.doritech.api.Entity.ResponseEntity("NVR Connection Details not found with id : " + id, 200,
					null);
		}

		repository.deleteById(id);

		return new com.doritech.api.Entity.ResponseEntity("NVR Connection Delete successfully: ", 200, null);

	}

	public com.doritech.api.Entity.ResponseEntity getNvrConnectionDetailsByNvrId(String nvrId) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			NvrConnectionDetails details = nvrConnectionDetailsRepository.findByNvr(nvrId);

			if (details != null) {

				response.setMessage("NVR Details Found");
				response.setStatusCode(200);
				response.setPayload(details);

			} else {

				response.setMessage("NVR Details Not Found");
				response.setStatusCode(404);
				response.setPayload(null);
			}

		} catch (Exception e) {

			response.setMessage("Failed to fetch NVR Details");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());

			e.printStackTrace();
		}

		return response;
	}

	@Transactional
	public com.doritech.api.Entity.ResponseEntity deleteNvrConnectionDetailsByNvrId(String nvrId) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			if (!nvrConnectionDetailsRepository.existsByNvr(nvrId)) {

				response.setMessage("NvrConnectionDetails  Not Found");
				response.setStatusCode(404);
				return response;
			}

			nvrConnectionDetailsRepository.deleteByNvr(nvrId);

			response.setMessage("NvrConnectionDetails Deleted Successfully");
			response.setStatusCode(200);

		} catch (Exception e) {

			response.setMessage("Failed to delete NvrConnectionDetails ");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getActiveNvrDetailsByVendorId(String venderId) {
		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		List<NvrMaster> list = repository.findByStatusAndVendorId("A", venderId);
		if (list == null || list.isEmpty()) {
			response.setMessage("Nvr Details Not Found By Given VendorId " + venderId);
			response.setStatusCode(404);
			return response;
		}

		response.setMessage("Nvr Details Fetched Successfully ");
		response.setStatusCode(200);
		response.setPayload(list);
		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getAllNvrNamesAndId() {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			List<Object[]> result = repository.findNvrIdAndName();

			List<Map<String, Object>> data = new ArrayList<>();

			for (Object[] row : result) {
				Map<String, Object> map = new HashMap<>();
				map.put("nvrId", row[0]);
				map.put("nvrName", row[1]);
				data.add(map);
			}

			response.setMessage("NVR list fetched successfully");
			response.setStatusCode(200);
			response.setPayload(data);

		} catch (Exception e) {

			response.setMessage("Failed to fetch NVR list");
			response.setStatusCode(500);
			response.setPayload(null);
		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity checkNvrSerialNo(Long nvrId, String nvrSerialNo) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			boolean exists;

			if (nvrId == null) {
				exists = repository.existsByNvrSerialNo(nvrSerialNo);
			} else {
				if (!repository.existsById(nvrId)) {
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
					response.setMessage("NVR not found.");
					return response;
				}

				exists = repository.existsByNvrSerialNoAndNvrIdNot(nvrSerialNo, nvrId);
			}

			if (exists) {
				response.setStatusCode(HttpStatus.CONFLICT.value());
				response.setMessage("NVR Serial Number already exists.");
				response.setPayload(true);
			} else {
				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("NVR Serial Number is available.");
				response.setPayload(false);
			}

		} catch (Exception e) {

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error while checking NVR Serial Number.");
			response.setPayload(e.getMessage());
		}

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getNvrAllDetailsByPanelSerialNo(String panelSerialNo) {
		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		if (panelSerialNo.trim().isEmpty() || panelSerialNo == null) {
			response.setMessage("Panel serial number cannot be null ");
			response.setStatusCode(400);
			return response;
		}

		List<NvrMaster> list = repository.findByPanelSerialNo(panelSerialNo);

		if (list == null || list.isEmpty()) {
			response.setMessage("Nvr Details Not Found By Given panelSerialNo " + panelSerialNo);
			response.setStatusCode(404);
			return response;
		}

		response.setMessage("Nvr Details Fetched Successfully ");
		response.setStatusCode(200);
		response.setPayload(list);
		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getCameraDetailsByPanelSerialNo(String panelSerialNo) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();
		List<CameraMaster> cameraList = repository.findCameraByPanelSerialNo(panelSerialNo);

		if (cameraList == null || cameraList.isEmpty()) {
			response.setMessage("Camera Details Not Found For Panel Serial No : " + panelSerialNo);
			response.setStatusCode(404);
			return response;
		}

		response.setMessage("Camera Details Fetched Successfully");
		response.setStatusCode(200);
		response.setPayload(cameraList);

		return response;

	}

	public com.doritech.api.Entity.ResponseEntity getNvrHardDiskDetailsByPanelSerialNo(String panelSerialNo) {
		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		if (panelSerialNo.trim().isEmpty() || panelSerialNo == null) {
			response.setMessage("Panel serial number cannot be null ");
			response.setStatusCode(400);
			return response;
		}

		List<NvrHardDisk> hardDiskList = repository.getHardDisksByPanelSerialNo(panelSerialNo);

		if (hardDiskList == null || hardDiskList.isEmpty()) {
			response.setMessage("HardDisk Details Not Found For Panel Serial No" + panelSerialNo);
			response.setStatusCode(404);
			return response;
		}

		response.setMessage("Hard Disk Details Fetched SuccessFully ");
		response.setPayload(hardDiskList);
		response.setStatusCode(200);
		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getCameraByPanelSerialNoAndChannelNo(String panelSerialNo,
			Integer channelNo) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		Optional<NvrMaster> nvr = repository.findNvrByPanelSerialNo(panelSerialNo);

		if (nvr.isEmpty()) {
			response.setStatusCode(404);
			response.setMessage("Panel Serial No Not Found ");
			return response;
		}

		Optional<CameraMaster> camera = repository.findCameraByPanelSerialNoAndChannelNo(panelSerialNo, channelNo);

		if (camera.isEmpty()) {
			response.setStatusCode(404);
			response.setMessage("Channel No Not Found for this : " + channelNo);
			return response;
		}

		response.setStatusCode(200);
		response.setMessage("Camera Details Fetched Successfully");
		response.setPayload(camera.get());

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getNvrDetailsByPanelSerialNo(String panelSerialNo) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		if (panelSerialNo.trim().isEmpty() || panelSerialNo == null) {
			response.setMessage("Panel serial number cannot be null or empty");
			response.setStatusCode(400);
			return response;
		}
		List<Object[]> result = repository.findNvrDetailsByPanelSerialNo(panelSerialNo);
		if (result.isEmpty() || result == null) {
			response.setMessage("Nvr Details Not Found ");
			response.setStatusCode(400);
			return response;
		}

		List<Map<String, Object>> list = new ArrayList<>();
		for (Object[] row : result) {
			Map<String, Object> map = new HashMap<>();
			map.put("ipAddress", row[0]);
			map.put("portNo", row[1]);
			map.put("username", row[2]);
			map.put("password", row[3]);
			map.put("nvrId", row[4]);
			map.put("panelSerialNo", row[5]);
			map.put("vendorId", row[6]);
			map.put("vendorName", row[7]);
			map.put("rtspPort", row[8]);

			list.add(map);

		}
		response.setMessage("NVR details fetched successfully");
		response.setStatusCode(200);
		response.setPayload(list);

		return response;
	}

	public com.doritech.api.Entity.ResponseEntity getAllHikvisionNvrs() {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		List<Object[]> result = repository.findAllHikvisionNvrs();

		if (result.isEmpty() || result == null) {
			response.setMessage("Hikvision Nvr Not Found ");
			response.setStatusCode(400);
			return response;
		}

		List<Map<String, Object>> payload = new ArrayList<>();

		for (Object[] row : result) {

			Map<String, Object> data = new HashMap<>();
			data.put("vendorId", row[0]);
			data.put("vendorName", row[1]);
			data.put("nvrId", row[2]);
			data.put("ipAddress", row[3]);
			data.put("portNo", row[4]);
			data.put("panelSerialNo", row[5]);
			data.put("username", row[6]);
			data.put("password", row[7]);

			payload.add(data);
		}

		response.setMessage("Hikvision NVR details fetched successfully");
		response.setStatusCode(200);
		response.setPayload(payload);

		return response;
	}
	
	public com.doritech.api.Entity.ResponseEntity getAllNvrAndVendorDetailsByPanelSerialNo(String panelSerialNo){
		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();
		
		if(panelSerialNo == null || panelSerialNo.trim().isBlank()) {
			response.setMessage("Panel serial number cannot be null or empty");
			response.setStatusCode(400);
			return response;
		}
		
		List<Object[]> result = repository.findHikvisionNvrDetailsByPanelSerialNo(panelSerialNo);
		
		if(result == null || result.isEmpty() ) {
			response.setMessage("Hikvision Nvr Details Not Found ");
			response.setStatusCode(404);
			return response;
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		for (Object[] row : result) {
			
			Map<String, Object> map = new HashMap<>();
			map.put("ipAddress", row[0]);
			map.put("portNo", row[1]);
			map.put("username", row[2]);
			map.put("password", row[3]);
			map.put("nvrId", row[4]);
			map.put("panelSerialNo", row[5]);
			map.put("vendorId", row[6]);
			map.put("vendorName", row[7]);
			
			list.add(map);
		}
		response.setMessage("Hikvision NVR details fetched successfully");
		response.setStatusCode(200);
		response.setPayload(list);

		return response;
	}

}







