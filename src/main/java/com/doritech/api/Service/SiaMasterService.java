package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.SiaMasterDTO;
import com.doritech.api.DTO.SiaPhysicalZoneMasterDTO;
import com.doritech.api.DTO.SiaZoneCameraMappingDto;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SiaMaster;
import com.doritech.api.Entity.SiaPhysicalZoneMaster;
import com.doritech.api.Entity.SiaZoneCameraMapping;
import com.doritech.api.Entity.VendorMasterEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.SiaMasterRepository;
import com.doritech.api.Repository.SiaPhysicalZoneMasterRepository;
import com.doritech.api.Repository.SiaZoneCameraMappingRespository;
import com.doritech.api.Repository.VendorMasterRepository;

@Service
public class SiaMasterService {

	@Autowired
	private SiaMasterRepository siaMasterRepository;

	@Autowired
	private SiaPhysicalZoneMasterRepository zoneRepository;
	@Autowired
	private DeviceMasterRepository deviceMasterRepository;

	@Autowired
	private VendorMasterRepository vendorMasterRepository;

	@Autowired
	private BranchMasterRepository branchMasterRepository;

	@Autowired
	private DeviceMasterService deviceMasterService;

	@Autowired
	private BranchMasterService branchService;

	public ResponseEntity saveOrUpdate(SiaMasterDTO dto) {

		ResponseEntity response = new ResponseEntity();

		try {

			// NULL CHECK
			if (dto == null) {

				response.setMessage("Request Body Cannot Be Null");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());

				return response;
			}

			// SERIAL NO VALIDATION
			if (dto.getSiaSerialNo() == null || dto.getSiaSerialNo().trim().isEmpty()) {

				response.setMessage("SIA Serial No Cannot Be Empty");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());

				return response;
			}

			// SAVE CASE DUPLICATE CHECK
			if (dto.getSiaDeviceId() == null) {

				boolean exists = siaMasterRepository.existsBySiaSerialNo(dto.getSiaSerialNo());

				if (exists) {

					response.setMessage("SIA Serial No Already Exists");
					response.setStatusCode(HttpStatus.CONFLICT.value());

					return response;
				}
			}

			// UPDATE CASE DUPLICATE CHECK
			else {

				boolean exists = siaMasterRepository.existsBySiaSerialNoAndIdNot(dto.getSiaSerialNo(),
						dto.getSiaDeviceId());

				if (exists) {

					response.setMessage("SIA Serial No Already Exists");
					response.setStatusCode(HttpStatus.CONFLICT.value());

					return response;
				}
			}

			SiaMaster siaMaster = new SiaMaster();

			// UPDATE CASE
			if (dto.getSiaDeviceId() != null) {

				Optional<SiaMaster> optional = siaMasterRepository.findById(dto.getSiaDeviceId());

				if (optional.isPresent()) {

					siaMaster = optional.get();

				} else {

					response.setMessage("Record Not Found");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());

					return response;
				}
			}

			// COMMON SAVE/UPDATE
			siaMaster.setSiaSerialNo(dto.getSiaSerialNo());
			siaMaster.setPanelSerialNo(dto.getPanelSerialNo());
			siaMaster.setStatus(dto.getStatus());
			siaMaster.setVendor(dto.getVendor());

			// SAVE MASTER
			SiaMaster savedMaster = siaMasterRepository.save(siaMaster);

			Long siaDeviceId = savedMaster.getId();

			// UPDATE CASE -> DELETE OLD ZONES
			if (dto.getSiaDeviceId() != null) {

				List<SiaPhysicalZoneMaster> oldZones = zoneRepository.findBySiaDeviceId(siaDeviceId);

				if (oldZones != null && !oldZones.isEmpty()) {

					zoneRepository.deleteAll(oldZones);
				}
			}

			// SAVE ZONE LIST
			if (dto.getZoneList() != null && !dto.getZoneList().isEmpty()) {

				List<SiaPhysicalZoneMaster> zoneList = new ArrayList<>();

				for (SiaPhysicalZoneMasterDTO zoneDTO : dto.getZoneList()) {

					SiaPhysicalZoneMaster zone = new SiaPhysicalZoneMaster();

					zone.setSiaDeviceId(siaDeviceId);
					zone.setZone(zoneDTO.getZone());
					zone.setZoneName(zoneDTO.getZoneName());
					zone.setStatus(zoneDTO.getStatus());

					zoneList.add(zone);
				}

				zoneRepository.saveAll(zoneList);
			}

			// SUCCESS RESPONSE
			if (dto.getSiaDeviceId() == null) {

				response.setMessage("Saved Successfully");

			} else {

				response.setMessage("Updated Successfully");
			}

			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(savedMaster);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Something Went Wrong");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response.setPayload(e.getMessage());
		}

		return response;
	}

	public ResponseEntity softDeleteById(Long id, String status) {

		ResponseEntity response = new ResponseEntity();

		try {

			if (status == null || status.trim().isEmpty()) {

				response.setMessage("Status Cannot Be Empty");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());

				return response;
			}

			Optional<SiaMaster> optional = siaMasterRepository.findById(id);

			if (!optional.isPresent()) {

				response.setMessage("Record Not Found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());

				return response;
			}

			SiaMaster siaMaster = optional.get();

			siaMaster.setStatus(status);

			siaMasterRepository.save(siaMaster);

			List<SiaPhysicalZoneMaster> zoneList = zoneRepository.findBySiaDeviceId(id);

			if (zoneList != null && !zoneList.isEmpty()) {

				for (SiaPhysicalZoneMaster zone : zoneList) {

					zone.setStatus(status);
				}

				zoneRepository.saveAll(zoneList);
			}

			response.setMessage("Sia Data deleted Successfully");

			response.setStatusCode(HttpStatus.OK.value());

			response.setPayload(siaMaster);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Something Went Wrong");

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response.setPayload(e.getMessage());
		}

		return response;
	}

	private Map<String, String> getVendorMap() {

		Map<String, String> vendorMap = new HashMap<>();

		List<VendorMasterEntity> vendorList = vendorMasterRepository.findAll();

		for (VendorMasterEntity vendor : vendorList) {

			vendorMap.put(vendor.getVendorId(), vendor.getVendorName());
		}

		return vendorMap;
	}

	public ResponseEntity getById(Long id) {

		ResponseEntity response = new ResponseEntity();

		try {

			Optional<SiaMaster> optional = siaMasterRepository.findById(id);

			if (!optional.isPresent()) {

				response.setMessage("SIA Master Record Not Found");

				response.setStatusCode(HttpStatus.NOT_FOUND.value());

				return response;
			}

			SiaMaster siaMaster = optional.get();

			// VENDOR NAME
			String vendorName = "";

			if (siaMaster.getVendor() != null) {

				VendorMasterEntity vendor = vendorMasterRepository.findByVendorId(siaMaster.getVendor());

				if (vendor != null) {

					vendorName = vendor.getVendorName();
				}
			}

			// CUSTOM SIA MASTER RESPONSE
			Map<String, Object> siaMasterMap = new HashMap<>();

			siaMasterMap.put("id", siaMaster.getId());

			siaMasterMap.put("siaSerialNo", siaMaster.getSiaSerialNo());

			siaMasterMap.put("panelSerialNo", siaMaster.getPanelSerialNo());

			// DIRECT DB STATUS
			siaMasterMap.put("status", siaMaster.getStatus());

			siaMasterMap.put("vendor", siaMaster.getVendor());

			siaMasterMap.put("vendorName", vendorName);

			// FETCH ZONE LIST
			List<SiaPhysicalZoneMaster> zoneList = zoneRepository.findBySiaDeviceId(id);

			// FINAL PAYLOAD
			Map<String, Object> payload = new HashMap<>();

			payload.put("siaMasterDetails", siaMasterMap);

			payload.put("physicalZoneDetails", zoneList);

			response.setMessage("SIA Master Details Fetched Successfully");

			response.setStatusCode(HttpStatus.OK.value());

			response.setPayload(payload);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Failed To Fetch SIA Master Details");

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response.setPayload(e.getMessage());
		}

		return response;
	}

	public ResponseEntity getAllDataWithPagination(String branchCode, int page, int size) {

		ResponseEntity response = new ResponseEntity();

		try {

			// STATUS MAP
			Map<String, String> statusMap = getStatusMap();

			// VENDOR MAP
			Map<String, String> vendorMap = getVendorMap();

			// CHILD BRANCHES
			ResponseEntity childBranchResponse = branchService.getAllChildren(branchCode);

			List<BranchMasterEntity> branches = (List<BranchMasterEntity>) childBranchResponse.getPayload();

			// BRANCH CODE LIST
			List<String> branchCodes = branches.stream().map(BranchMasterEntity::getBranchCode).filter(Objects::nonNull)
					.collect(Collectors.toList());

			// BRANCH DESC MAP
			Map<String, String> branchMap = branches.stream().collect(Collectors
					.toMap(BranchMasterEntity::getBranchCode, BranchMasterEntity::getBranchDesc, (a, b) -> a));

			// DEVICE LIST
			List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);

			// PANEL SERIAL LIST
			List<String> panelSerialNos = deviceList.stream().map(DeviceMasterEntity::getDeviceSerialNo)
					.filter(Objects::nonNull).collect(Collectors.toList());

			// DEVICE MAP
			Map<String, DeviceMasterEntity> deviceMap = deviceList.stream()
					.collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo, device -> device, (a, b) -> a));

			Pageable pageable = PageRequest.of(page, size);

			Page<SiaMaster> siaMasterPage = siaMasterRepository.findByPanelSerialNoIn(panelSerialNos, pageable);

			List<Map<String, Object>> responseList = new ArrayList<>();

			for (SiaMaster siaMaster : siaMasterPage.getContent()) {

				// STATUS CONVERT
				siaMaster.setStatus(statusMap.getOrDefault(siaMaster.getStatus(), siaMaster.getStatus()));

				// VENDOR NAME
				String vendorName = vendorMap.get(siaMaster.getVendor());

				// BRANCH DESC
				String branchDesc = "";

				DeviceMasterEntity device = deviceMap.get(siaMaster.getPanelSerialNo());

				if (device != null) {

					branchDesc = branchMap.getOrDefault(device.getBranchCode(), "");
				}

				// CUSTOM RESPONSE
				Map<String, Object> siaMasterMap = new HashMap<>();

				siaMasterMap.put("id", siaMaster.getId());

				siaMasterMap.put("siaSerialNo", siaMaster.getSiaSerialNo());

				siaMasterMap.put("panelSerialNo", siaMaster.getPanelSerialNo());

				siaMasterMap.put("status", siaMaster.getStatus());

				siaMasterMap.put("vendor", siaMaster.getVendor());

				siaMasterMap.put("vendorName", vendorName);

				siaMasterMap.put("branchDesc", branchDesc);

				// FETCH ZONES
				List<SiaPhysicalZoneMaster> zoneList = zoneRepository.findBySiaDeviceId(siaMaster.getId());

				// ZONE STATUS CONVERT
				for (SiaPhysicalZoneMaster zone : zoneList) {

					zone.setStatus(statusMap.getOrDefault(zone.getStatus(), zone.getStatus()));
				}

				Map<String, Object> map = new HashMap<>();

				map.put("siaMasterDetails", siaMasterMap);

				map.put("physicalZoneDetails", zoneList);

				responseList.add(map);
			}

			response.setMessage("SIA Master List Fetched Successfully");

			response.setStatusCode(HttpStatus.OK.value());

			response.setPayload(responseList);

			response.setCurrentPage(siaMasterPage.getNumber());

			response.setTotalPages(siaMasterPage.getTotalPages());

			response.setTotalElements(siaMasterPage.getTotalElements());

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Failed To Fetch SIA Master List");

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response.setPayload(e.getMessage());
		}

		return response;
	}

	public ResponseEntity getAllData(String branchCode) {

		ResponseEntity response = new ResponseEntity();

		try {

			// STATUS MAP
			Map<String, String> statusMap = getStatusMap();

			// VENDOR MAP
			Map<String, String> vendorMap = getVendorMap();

			// CHILD BRANCHES
			ResponseEntity childBranchResponse = branchService.getAllChildren(branchCode);

			List<BranchMasterEntity> branches = (List<BranchMasterEntity>) childBranchResponse.getPayload();

			// BRANCH CODE LIST
			List<String> branchCodes = branches.stream().map(BranchMasterEntity::getBranchCode).filter(Objects::nonNull)
					.collect(Collectors.toList());

			// BRANCH DESC MAP
			Map<String, String> branchMap = branches.stream().collect(Collectors
					.toMap(BranchMasterEntity::getBranchCode, BranchMasterEntity::getBranchDesc, (a, b) -> a));

			// DEVICE LIST
			List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);

			// PANEL SERIAL LIST
			List<String> panelSerialNos = deviceList.stream().map(DeviceMasterEntity::getDeviceSerialNo)
					.filter(Objects::nonNull).collect(Collectors.toList());

			// DEVICE MAP
			Map<String, DeviceMasterEntity> deviceMap = deviceList.stream()
					.collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo, device -> device, (a, b) -> a));

			List<SiaMaster> siaMasterList = siaMasterRepository.findByPanelSerialNoIn(panelSerialNos);

			List<Map<String, Object>> responseList = new ArrayList<>();

			for (SiaMaster siaMaster : siaMasterList) {

				// STATUS CONVERT
				siaMaster.setStatus(statusMap.getOrDefault(siaMaster.getStatus(), siaMaster.getStatus()));

				// VENDOR NAME
				String vendorName = vendorMap.get(siaMaster.getVendor());

				// BRANCH DESC
				String branchDesc = "";

				DeviceMasterEntity device = deviceMap.get(siaMaster.getPanelSerialNo());

				if (device != null) {

					branchDesc = branchMap.getOrDefault(device.getBranchCode(), "");
				}

				// CUSTOM RESPONSE
				Map<String, Object> siaMasterMap = new HashMap<>();

				siaMasterMap.put("id", siaMaster.getId());

				siaMasterMap.put("siaSerialNo", siaMaster.getSiaSerialNo());

				siaMasterMap.put("panelSerialNo", siaMaster.getPanelSerialNo());

				siaMasterMap.put("status", siaMaster.getStatus());

				siaMasterMap.put("vendor", siaMaster.getVendor());

				siaMasterMap.put("vendorName", vendorName);

				siaMasterMap.put("branchDesc", branchDesc);

				// FETCH ZONES
				List<SiaPhysicalZoneMaster> zoneList = zoneRepository.findBySiaDeviceId(siaMaster.getId());

				// ZONE STATUS CONVERT
				for (SiaPhysicalZoneMaster zone : zoneList) {

					zone.setStatus(statusMap.getOrDefault(zone.getStatus(), zone.getStatus()));
				}

				Map<String, Object> map = new HashMap<>();

				map.put("siaMasterDetails", siaMasterMap);

				map.put("physicalZoneDetails", zoneList);

				responseList.add(map);
			}

			response.setMessage("All SIA Master Data Fetched Successfully");

			response.setStatusCode(HttpStatus.OK.value());

			response.setPayload(responseList);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Failed To Fetch SIA Master Data");

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response.setPayload(e.getMessage());
		}

		return response;
	}

	// STATUS MAP
	private Map<String, String> getStatusMap() {

		Map<String, String> statusMap = new HashMap<>();

		List<ParamEntity> params = deviceMasterService.getParamByCode("STATUS");

		if (params != null && !params.isEmpty()) {

			for (ParamEntity param : params) {

				statusMap.put(param.getSerial(), param.getDescp1());
			}
		}

		return statusMap;
	}

	public ResponseEntity checkSiaSerialNo(String siaSerialNo) {

		ResponseEntity response = new ResponseEntity();

		try {

			if (siaSerialNo == null || siaSerialNo.trim().isEmpty()) {

				response.setMessage("SIA Serial No Cannot Be Empty");

				response.setStatusCode(HttpStatus.BAD_REQUEST.value());

				return response;
			}

			boolean exists = siaMasterRepository.existsBySiaSerialNo(siaSerialNo);

			if (exists) {

				response.setMessage("SIA Serial No Already Exists");

				response.setStatusCode(HttpStatus.CONFLICT.value());

				response.setPayload(true);
			}

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Failed To Check SIA Serial No");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response.setPayload(e.getMessage());
		}
		return response;
	}

//	New Code 
	
//	@Autowired
//	private SiaZoneCameraMappingRespository siaZoneCameraMappingRespository;
//
//	public ResponseEntity saveOrUpdate(SiaMasterDTO dto) {
//
//		ResponseEntity response = new ResponseEntity();
//
//		try {
//
//			// NULL CHECK
//			if (dto == null) {
//
//				response.setMessage("Request Body Cannot Be Null");
//				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//				return response;
//			}
//
//			// SERIAL NO VALIDATION
//			if (dto.getSiaSerialNo() == null || dto.getSiaSerialNo().trim().isEmpty()) {
//
//				response.setMessage("SIA Serial No Cannot Be Empty");
//				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//				return response;
//			}
//
//			// ZONE + CAMERA VALIDATION (BEFORE ANY DB OPERATION)
//			if (dto.getZoneList() != null && !dto.getZoneList().isEmpty()) {
//
//				for (SiaPhysicalZoneMasterDTO zoneDTO : dto.getZoneList()) {
//
//					if (zoneDTO.getCameraList() != null && !zoneDTO.getCameraList().isEmpty()) {
//
//						for (SiaZoneCameraMappingDto cameraDTO : zoneDTO.getCameraList()) {
//
//							// CAMERA CHANNEL REQUIRED CHECK
//							if (cameraDTO.getCameraChannel() == null || cameraDTO.getCameraChannel().trim().isEmpty()) {
//
//								response.setMessage("Camera Channel Cannot Be Empty");
//								response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//								return response;
//							}
//
//							// CAMERA CHANNEL LENGTH CHECK (DB COLUMN LENGTH = 20)
//							if (cameraDTO.getCameraChannel().trim().length() > 20) {
//
//								response.setMessage("Camera Channel Cannot Exceed 20 Characters");
//								response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//								return response;
//							}
//
//							// NVR ID REQUIRED CHECK
//							if (cameraDTO.getNvrId() == null) {
//
//								response.setMessage("NVR Id Cannot Be Empty");
//								response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//								return response;
//							}
//						}
//					}
//				}
//			}
//
//			// SAVE CASE DUPLICATE CHECK
//			if (dto.getSiaDeviceId() == null) {
//
//				boolean exists = siaMasterRepository.existsBySiaSerialNo(dto.getSiaSerialNo());
//
//				if (exists) {
//
//					response.setMessage("SIA Serial No Already Exists");
//					response.setStatusCode(HttpStatus.CONFLICT.value());
//
//					return response;
//				}
//			}
//
//			// UPDATE CASE DUPLICATE CHECK
//			else {
//
//				boolean exists = siaMasterRepository.existsBySiaSerialNoAndIdNot(dto.getSiaSerialNo(),
//						dto.getSiaDeviceId());
//
//				if (exists) {
//
//					response.setMessage("SIA Serial No Already Exists");
//					response.setStatusCode(HttpStatus.CONFLICT.value());
//
//					return response;
//				}
//			}
//
//			SiaMaster siaMaster = new SiaMaster();
//
//			// UPDATE CASE
//			if (dto.getSiaDeviceId() != null) {
//
//				Optional<SiaMaster> optional = siaMasterRepository.findById(dto.getSiaDeviceId());
//
//				if (optional.isPresent()) {
//
//					siaMaster = optional.get();
//
//				} else {
//
//					response.setMessage("Record Not Found");
//					response.setStatusCode(HttpStatus.NOT_FOUND.value());
//
//					return response;
//				}
//			}
//
//			// COMMON SAVE/UPDATE
//			siaMaster.setSiaSerialNo(dto.getSiaSerialNo());
//			siaMaster.setPanelSerialNo(dto.getPanelSerialNo());
//			siaMaster.setStatus(dto.getStatus());
//			siaMaster.setVendor(dto.getVendor());
//
//			// SAVE MASTER
//			SiaMaster savedMaster = siaMasterRepository.save(siaMaster);
//
//			Long siaDeviceId = savedMaster.getId();
//
//			// UPDATE CASE -> DELETE OLD ZONES (and their camera mappings)
//			if (dto.getSiaDeviceId() != null) {
//
//				List<SiaPhysicalZoneMaster> oldZones = zoneRepository.findBySiaDeviceId(siaDeviceId);
//
//				if (oldZones != null && !oldZones.isEmpty()) {
//
//					List<Long> oldZoneIds = oldZones.stream().map(SiaPhysicalZoneMaster::getId)
//							.collect(Collectors.toList());
//
//					// DELETE OLD CAMERA MAPPINGS FOR THESE ZONES
//					List<SiaZoneCameraMapping> oldCameraMappings = siaZoneCameraMappingRespository
//							.findBySiaZoneIdIn(oldZoneIds);
//
//					if (oldCameraMappings != null && !oldCameraMappings.isEmpty()) {
//						siaZoneCameraMappingRespository.deleteAll(oldCameraMappings);
//					}
//
//					zoneRepository.deleteAll(oldZones);
//				}
//			}
//
//			// SAVE ZONE LIST
//			if (dto.getZoneList() != null && !dto.getZoneList().isEmpty()) {
//
//				for (SiaPhysicalZoneMasterDTO zoneDTO : dto.getZoneList()) {
//
//					SiaPhysicalZoneMaster zone = new SiaPhysicalZoneMaster();
//
//					zone.setSiaDeviceId(siaDeviceId);
//					zone.setZone(zoneDTO.getZone());
//					zone.setZoneName(zoneDTO.getZoneName());
//					zone.setStatus(zoneDTO.getStatus());
//
//					// SAVE ZONE FIRST TO GET GENERATED ID
//					SiaPhysicalZoneMaster savedZone = zoneRepository.save(zone);
//
//					// SAVE CAMERA LIST FOR THIS ZONE
//					if (zoneDTO.getCameraList() != null && !zoneDTO.getCameraList().isEmpty()) {
//
//						List<SiaZoneCameraMapping> cameraMappingList = new ArrayList<>();
//
//						for (SiaZoneCameraMappingDto cameraDTO : zoneDTO.getCameraList()) {
//
//							SiaZoneCameraMapping cameraMapping = new SiaZoneCameraMapping();
//
//							// siaZoneId IS SET HERE -> NEVER NULL, SO NO SEPARATE NULL CHECK NEEDED
//							cameraMapping.setSiaZoneId(savedZone.getId());
//							cameraMapping.setCameraChannel(cameraDTO.getCameraChannel().trim());
//							cameraMapping.setNvrId(cameraDTO.getNvrId());
//
//							cameraMappingList.add(cameraMapping);
//						}
//
//						siaZoneCameraMappingRespository.saveAll(cameraMappingList);
//					}
//				}
//			}
//
//			// SUCCESS RESPONSE
//			if (dto.getSiaDeviceId() == null) {
//
//				response.setMessage("Saved Successfully");
//
//			} else {
//
//				response.setMessage("Updated Successfully");
//			}
//
//			response.setStatusCode(HttpStatus.OK.value());
//			response.setPayload(savedMaster);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			response.setMessage("Something Went Wrong");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//			response.setPayload(e.getMessage());
//		}
//
//		return response;
//	}
//
//	public ResponseEntity softDeleteById(Long id, String status) {
//
//		ResponseEntity response = new ResponseEntity();
//
//		try {
//
//			if (status == null || status.trim().isEmpty()) {
//
//				response.setMessage("Status Cannot Be Empty");
//				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//				return response;
//			}
//
//			Optional<SiaMaster> optional = siaMasterRepository.findById(id);
//
//			if (!optional.isPresent()) {
//
//				response.setMessage("Record Not Found");
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//
//				return response;
//			}
//
//			SiaMaster siaMaster = optional.get();
//
//			siaMaster.setStatus(status);
//
//			siaMasterRepository.save(siaMaster);
//
//			List<SiaPhysicalZoneMaster> zoneList = zoneRepository.findBySiaDeviceId(id);
//
//			if (zoneList != null && !zoneList.isEmpty()) {
//
//				for (SiaPhysicalZoneMaster zone : zoneList) {
//
//					zone.setStatus(status);
//				}
//
//				zoneRepository.saveAll(zoneList);
//			}
//
//			response.setMessage("Sia Data deleted Successfully");
//
//			response.setStatusCode(HttpStatus.OK.value());
//
//			response.setPayload(siaMaster);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			response.setMessage("Something Went Wrong");
//
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//			response.setPayload(e.getMessage());
//		}
//
//		return response;
//	}
//
//	private Map<String, String> getVendorMap() {
//
//		Map<String, String> vendorMap = new HashMap<>();
//
//		List<VendorMasterEntity> vendorList = vendorMasterRepository.findAll();
//
//		for (VendorMasterEntity vendor : vendorList) {
//
//			vendorMap.put(vendor.getVendorId(), vendor.getVendorName());
//		}
//
//		return vendorMap;
//	}
//
//	public ResponseEntity getById(Long id) {
//
//		ResponseEntity response = new ResponseEntity();
//
//		try {
//
//			Optional<SiaMaster> optional = siaMasterRepository.findById(id);
//
//			if (!optional.isPresent()) {
//
//				response.setMessage("SIA Master Record Not Found");
//
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//
//				return response;
//			}
//
//			SiaMaster siaMaster = optional.get();
//
//			// VENDOR NAME
//			String vendorName = "";
//
//			if (siaMaster.getVendor() != null) {
//
//				VendorMasterEntity vendor = vendorMasterRepository.findByVendorId(siaMaster.getVendor());
//
//				if (vendor != null) {
//
//					vendorName = vendor.getVendorName();
//				}
//			}
//
//			// CUSTOM SIA MASTER RESPONSE
//			Map<String, Object> siaMasterMap = new HashMap<>();
//
//			siaMasterMap.put("id", siaMaster.getId());
//
//			siaMasterMap.put("siaSerialNo", siaMaster.getSiaSerialNo());
//
//			siaMasterMap.put("panelSerialNo", siaMaster.getPanelSerialNo());
//
//			// DIRECT DB STATUS
//			siaMasterMap.put("status", siaMaster.getStatus());
//
//			siaMasterMap.put("vendor", siaMaster.getVendor());
//
//			siaMasterMap.put("vendorName", vendorName);
//
//			// FETCH ZONE LIST
//			List<SiaPhysicalZoneMaster> zoneList = zoneRepository.findBySiaDeviceId(id);
//
//			// BUILD ZONE LIST WITH CAMERA MAPPINGS
//			List<Map<String, Object>> zoneDetailsList = new ArrayList<>();
//
//			if (zoneList != null && !zoneList.isEmpty()) {
//
//				// COLLECT ZONE IDS
//				List<Long> zoneIds = zoneList.stream().map(SiaPhysicalZoneMaster::getId).collect(Collectors.toList());
//
//				// FETCH ALL CAMERA MAPPINGS FOR THESE ZONES IN ONE QUERY
//				List<SiaZoneCameraMapping> allCameraMappings = siaZoneCameraMappingRespository
//						.findBySiaZoneIdIn(zoneIds);
//
//				// GROUP CAMERA MAPPINGS BY ZONE ID
//				Map<Long, List<SiaZoneCameraMapping>> cameraMapByZoneId = allCameraMappings.stream()
//						.collect(Collectors.groupingBy(SiaZoneCameraMapping::getSiaZoneId));
//
//				for (SiaPhysicalZoneMaster zone : zoneList) {
//
//					Map<String, Object> zoneMap = new HashMap<>();
//
//					zoneMap.put("id", zone.getId());
//					zoneMap.put("zone", zone.getZone());
//					zoneMap.put("zoneName", zone.getZoneName());
//					zoneMap.put("status", zone.getStatus());
//
//					List<SiaZoneCameraMapping> cameraList = cameraMapByZoneId.getOrDefault(zone.getId(),
//							new ArrayList<>());
//
//					zoneMap.put("cameraList", cameraList);
//
//					zoneDetailsList.add(zoneMap);
//				}
//			}
//
//			// FINAL PAYLOAD
//			Map<String, Object> payload = new HashMap<>();
//
//			payload.put("siaMasterDetails", siaMasterMap);
//
//			payload.put("physicalZoneDetails", zoneDetailsList);
//
//			response.setMessage("SIA Master Details Fetched Successfully");
//
//			response.setStatusCode(HttpStatus.OK.value());
//
//			response.setPayload(payload);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			response.setMessage("Failed To Fetch SIA Master Details");
//
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//			response.setPayload(e.getMessage());
//		}
//
//		return response;
//	}
//
//	public ResponseEntity getAllDataWithPagination(String branchCode, int page, int size) {
//
//		ResponseEntity response = new ResponseEntity();
//
//		try {
//
//			// STATUS MAP
//			Map<String, String> statusMap = getStatusMap();
//
//			// VENDOR MAP
//			Map<String, String> vendorMap = getVendorMap();
//
//			// CHILD BRANCHES
//			ResponseEntity childBranchResponse = branchService.getAllChildren(branchCode);
//
//			List<BranchMasterEntity> branches = (List<BranchMasterEntity>) childBranchResponse.getPayload();
//
//			// BRANCH CODE LIST
//			List<String> branchCodes = branches.stream().map(BranchMasterEntity::getBranchCode).filter(Objects::nonNull)
//					.collect(Collectors.toList());
//
//			// BRANCH DESC MAP
//			Map<String, String> branchMap = branches.stream().collect(Collectors
//					.toMap(BranchMasterEntity::getBranchCode, BranchMasterEntity::getBranchDesc, (a, b) -> a));
//
//			// DEVICE LIST
//			List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
//
//			// PANEL SERIAL LIST
//			List<String> panelSerialNos = deviceList.stream().map(DeviceMasterEntity::getDeviceSerialNo)
//					.filter(Objects::nonNull).collect(Collectors.toList());
//
//			// DEVICE MAP
//			Map<String, DeviceMasterEntity> deviceMap = deviceList.stream()
//					.collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo, device -> device, (a, b) -> a));
//
//			Pageable pageable = PageRequest.of(page, size);
//
//			Page<SiaMaster> siaMasterPage = siaMasterRepository.findByPanelSerialNoIn(panelSerialNos, pageable);
//
//			// SIA DEVICE ID LIST FOR THIS PAGE
//			List<Long> siaDeviceIds = siaMasterPage.getContent().stream().map(SiaMaster::getId)
//					.collect(Collectors.toList());
//
//			// FETCH ALL ZONES FOR THESE DEVICES IN ONE QUERY
//			List<SiaPhysicalZoneMaster> allZones = zoneRepository.findBySiaDeviceIdIn(siaDeviceIds);
//
//			// GROUP ZONES BY SIA DEVICE ID
//			Map<Long, List<SiaPhysicalZoneMaster>> zoneMapByDeviceId = allZones.stream()
//					.collect(Collectors.groupingBy(SiaPhysicalZoneMaster::getSiaDeviceId));
//
//			// ZONE ID LIST (ACROSS ALL DEVICES ON THIS PAGE)
//			List<Long> zoneIds = allZones.stream().map(SiaPhysicalZoneMaster::getId).collect(Collectors.toList());
//
//			// FETCH ALL CAMERA MAPPINGS FOR THESE ZONES IN ONE QUERY
//			List<SiaZoneCameraMapping> allCameraMappings = siaZoneCameraMappingRespository.findBySiaZoneIdIn(zoneIds);
//
//			// GROUP CAMERA MAPPINGS BY ZONE ID
//			Map<Long, List<SiaZoneCameraMapping>> cameraMapByZoneId = allCameraMappings.stream()
//					.collect(Collectors.groupingBy(SiaZoneCameraMapping::getSiaZoneId));
//
//			List<Map<String, Object>> responseList = new ArrayList<>();
//
//			for (SiaMaster siaMaster : siaMasterPage.getContent()) {
//
//				// STATUS CONVERT
//				siaMaster.setStatus(statusMap.getOrDefault(siaMaster.getStatus(), siaMaster.getStatus()));
//
//				// VENDOR NAME
//				String vendorName = vendorMap.get(siaMaster.getVendor());
//
//				// BRANCH DESC
//				String branchDesc = "";
//
//				DeviceMasterEntity device = deviceMap.get(siaMaster.getPanelSerialNo());
//
//				if (device != null) {
//
//					branchDesc = branchMap.getOrDefault(device.getBranchCode(), "");
//				}
//
//				// CUSTOM RESPONSE
//				Map<String, Object> siaMasterMap = new HashMap<>();
//
//				siaMasterMap.put("id", siaMaster.getId());
//
//				siaMasterMap.put("siaSerialNo", siaMaster.getSiaSerialNo());
//
//				siaMasterMap.put("panelSerialNo", siaMaster.getPanelSerialNo());
//
//				siaMasterMap.put("status", siaMaster.getStatus());
//
//				siaMasterMap.put("vendor", siaMaster.getVendor());
//
//				siaMasterMap.put("vendorName", vendorName);
//
//				siaMasterMap.put("branchDesc", branchDesc);
//
//				// GET ZONES FOR THIS DEVICE (FROM PRE-FETCHED MAP)
//				List<SiaPhysicalZoneMaster> zoneList = zoneMapByDeviceId.getOrDefault(siaMaster.getId(),
//						new ArrayList<>());
//
//				// BUILD ZONE DETAILS WITH CAMERA LIST + STATUS CONVERT
//				List<Map<String, Object>> zoneDetailsList = new ArrayList<>();
//
//				for (SiaPhysicalZoneMaster zone : zoneList) {
//
//					// ZONE STATUS CONVERT
//					zone.setStatus(statusMap.getOrDefault(zone.getStatus(), zone.getStatus()));
//
//					Map<String, Object> zoneMap = new HashMap<>();
//
//					zoneMap.put("id", zone.getId());
//					zoneMap.put("zone", zone.getZone());
//					zoneMap.put("zoneName", zone.getZoneName());
//					zoneMap.put("status", zone.getStatus());
//
//					List<SiaZoneCameraMapping> cameraList = cameraMapByZoneId.getOrDefault(zone.getId(),
//							new ArrayList<>());
//
//					zoneMap.put("cameraList", cameraList);
//
//					zoneDetailsList.add(zoneMap);
//				}
//
//				Map<String, Object> map = new HashMap<>();
//
//				map.put("siaMasterDetails", siaMasterMap);
//
//				map.put("physicalZoneDetails", zoneDetailsList);
//
//				responseList.add(map);
//			}
//
//			response.setMessage("SIA Master List Fetched Successfully");
//
//			response.setStatusCode(HttpStatus.OK.value());
//
//			response.setPayload(responseList);
//
//			response.setCurrentPage(siaMasterPage.getNumber());
//
//			response.setTotalPages(siaMasterPage.getTotalPages());
//
//			response.setTotalElements(siaMasterPage.getTotalElements());
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			response.setMessage("Failed To Fetch SIA Master List");
//
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//			response.setPayload(e.getMessage());
//		}
//
//		return response;
//	}
//
//	public ResponseEntity getAllData(String branchCode) {
//
//		ResponseEntity response = new ResponseEntity();
//
//		try {
//
//			// STATUS MAP
//			Map<String, String> statusMap = getStatusMap();
//
//			// VENDOR MAP
//			Map<String, String> vendorMap = getVendorMap();
//
//			// CHILD BRANCHES
//			ResponseEntity childBranchResponse = branchService.getAllChildren(branchCode);
//
//			List<BranchMasterEntity> branches = (List<BranchMasterEntity>) childBranchResponse.getPayload();
//
//			// BRANCH CODE LIST
//			List<String> branchCodes = branches.stream().map(BranchMasterEntity::getBranchCode).filter(Objects::nonNull)
//					.collect(Collectors.toList());
//
//			// BRANCH DESC MAP
//			Map<String, String> branchMap = branches.stream().collect(Collectors
//					.toMap(BranchMasterEntity::getBranchCode, BranchMasterEntity::getBranchDesc, (a, b) -> a));
//
//			// DEVICE LIST
//			List<DeviceMasterEntity> deviceList = deviceMasterRepository.findByBranchCodeIn(branchCodes);
//
//			// PANEL SERIAL LIST
//			List<String> panelSerialNos = deviceList.stream().map(DeviceMasterEntity::getDeviceSerialNo)
//					.filter(Objects::nonNull).collect(Collectors.toList());
//
//			// DEVICE MAP
//			Map<String, DeviceMasterEntity> deviceMap = deviceList.stream()
//					.collect(Collectors.toMap(DeviceMasterEntity::getDeviceSerialNo, device -> device, (a, b) -> a));
//
//			List<SiaMaster> siaMasterList = siaMasterRepository.findByPanelSerialNoIn(panelSerialNos);
//
//			// SIA DEVICE ID LIST
//			List<Long> siaDeviceIds = siaMasterList.stream().map(SiaMaster::getId).collect(Collectors.toList());
//
//			// FETCH ALL ZONES FOR THESE DEVICES IN ONE QUERY
//			List<SiaPhysicalZoneMaster> allZones = zoneRepository.findBySiaDeviceIdIn(siaDeviceIds);
//
//			// GROUP ZONES BY SIA DEVICE ID
//			Map<Long, List<SiaPhysicalZoneMaster>> zoneMapByDeviceId = allZones.stream()
//					.collect(Collectors.groupingBy(SiaPhysicalZoneMaster::getSiaDeviceId));
//
//			// ZONE ID LIST (ACROSS ALL DEVICES)
//			List<Long> zoneIds = allZones.stream().map(SiaPhysicalZoneMaster::getId).collect(Collectors.toList());
//
//			// FETCH ALL CAMERA MAPPINGS FOR THESE ZONES IN ONE QUERY
//			List<SiaZoneCameraMapping> allCameraMappings = siaZoneCameraMappingRespository.findBySiaZoneIdIn(zoneIds);
//
//			// GROUP CAMERA MAPPINGS BY ZONE ID
//			Map<Long, List<SiaZoneCameraMapping>> cameraMapByZoneId = allCameraMappings.stream()
//					.collect(Collectors.groupingBy(SiaZoneCameraMapping::getSiaZoneId));
//
//			List<Map<String, Object>> responseList = new ArrayList<>();
//
//			for (SiaMaster siaMaster : siaMasterList) {
//
//				// STATUS CONVERT
//				siaMaster.setStatus(statusMap.getOrDefault(siaMaster.getStatus(), siaMaster.getStatus()));
//
//				// VENDOR NAME
//				String vendorName = vendorMap.get(siaMaster.getVendor());
//
//				// BRANCH DESC
//				String branchDesc = "";
//
//				DeviceMasterEntity device = deviceMap.get(siaMaster.getPanelSerialNo());
//
//				if (device != null) {
//
//					branchDesc = branchMap.getOrDefault(device.getBranchCode(), "");
//				}
//
//				// CUSTOM RESPONSE
//				Map<String, Object> siaMasterMap = new HashMap<>();
//
//				siaMasterMap.put("id", siaMaster.getId());
//
//				siaMasterMap.put("siaSerialNo", siaMaster.getSiaSerialNo());
//
//				siaMasterMap.put("panelSerialNo", siaMaster.getPanelSerialNo());
//
//				siaMasterMap.put("status", siaMaster.getStatus());
//
//				siaMasterMap.put("vendor", siaMaster.getVendor());
//
//				siaMasterMap.put("vendorName", vendorName);
//
//				siaMasterMap.put("branchDesc", branchDesc);
//
//				// GET ZONES FOR THIS DEVICE (FROM PRE-FETCHED MAP)
//				List<SiaPhysicalZoneMaster> zoneList = zoneMapByDeviceId.getOrDefault(siaMaster.getId(),
//						new ArrayList<>());
//
//				// BUILD ZONE DETAILS WITH CAMERA LIST + STATUS CONVERT
//				List<Map<String, Object>> zoneDetailsList = new ArrayList<>();
//
//				for (SiaPhysicalZoneMaster zone : zoneList) {
//
//					// ZONE STATUS CONVERT
//					zone.setStatus(statusMap.getOrDefault(zone.getStatus(), zone.getStatus()));
//
//					Map<String, Object> zoneMap = new HashMap<>();
//
//					zoneMap.put("id", zone.getId());
//					zoneMap.put("zone", zone.getZone());
//					zoneMap.put("zoneName", zone.getZoneName());
//					zoneMap.put("status", zone.getStatus());
//
//					List<SiaZoneCameraMapping> cameraList = cameraMapByZoneId.getOrDefault(zone.getId(),
//							new ArrayList<>());
//
//					zoneMap.put("cameraList", cameraList);
//
//					zoneDetailsList.add(zoneMap);
//				}
//
//				Map<String, Object> map = new HashMap<>();
//
//				map.put("siaMasterDetails", siaMasterMap);
//
//				map.put("physicalZoneDetails", zoneDetailsList);
//
//				responseList.add(map);
//			}
//
//			response.setMessage("All SIA Master Data Fetched Successfully");
//
//			response.setStatusCode(HttpStatus.OK.value());
//
//			response.setPayload(responseList);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			response.setMessage("Failed To Fetch SIA Master Data");
//
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//			response.setPayload(e.getMessage());
//		}
//
//		return response;
//	}
//
//	// STATUS MAP
//	private Map<String, String> getStatusMap() {
//
//		Map<String, String> statusMap = new HashMap<>();
//
//		List<ParamEntity> params = deviceMasterService.getParamByCode("STATUS");
//
//		if (params != null && !params.isEmpty()) {
//
//			for (ParamEntity param : params) {
//
//				statusMap.put(param.getSerial(), param.getDescp1());
//			}
//		}
//
//		return statusMap;
//	}
//
//	public ResponseEntity checkSiaSerialNo(String siaSerialNo) {
//
//		ResponseEntity response = new ResponseEntity();
//
//		try {
//
//			if (siaSerialNo == null || siaSerialNo.trim().isEmpty()) {
//
//				response.setMessage("SIA Serial No Cannot Be Empty");
//
//				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//
//				return response;
//			}
//
//			boolean exists = siaMasterRepository.existsBySiaSerialNo(siaSerialNo);
//
//			if (exists) {
//
//				response.setMessage("SIA Serial No Already Exists");
//
//				response.setStatusCode(HttpStatus.CONFLICT.value());
//
//				response.setPayload(true);
//			}
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			response.setMessage("Failed To Check SIA Serial No");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//			response.setPayload(e.getMessage());
//		}
//		return response;
//	}
}