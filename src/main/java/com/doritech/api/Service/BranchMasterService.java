package com.doritech.api.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Common.Message;
import com.doritech.api.DTO.BOIMasterDataDTO;
import com.doritech.api.DTO.BranchDTO;
import com.doritech.api.DTO.BranchDeviceDTO;
import com.doritech.api.DTO.BranchDropdownDto;
import com.doritech.api.DTO.BranchMasterDTO;
import com.doritech.api.DTO.DeviceZoneDTO;
import com.doritech.api.Entity.BOIMasterDataEntity;
import com.doritech.api.Entity.BankMasterEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.EmployeeMasterEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.BOIMasterDataRepository;
import com.doritech.api.Repository.BankMasterRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Repository.ParamRepository;
import com.doritech.api.Repository.VendorMasterRepository;

import javassist.NotFoundException;

@Service
public class BranchMasterService {
	@Autowired
	private BranchMasterRepository branchMasterRepository;

	@Autowired
	private ParamRepository paramRepo;
	@Autowired
	private ParamService paramService;
	@Autowired
	DeviceMasterRepository deviceRepo;
	@Autowired
	DeviceZoneDetailsRepository detailsRepository;
	@Autowired
	DeviceMasterService deviceMasterService;
	@Autowired
	EmployeeMasterRepository employeeMasterRepository;
	@Autowired
	BOIMasterDataRepository boiMasterDataRepository;
	@Autowired
	VendorMasterRepository vendorMasterRepository;
	@Autowired
	BankMasterRepository bankMasterRepository;

	/**
	 * @Author Akansha
	 */

	public ResponseEntity getAllConnectedBranches(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> responseList = new ArrayList<>();
			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);

			DeviceMasterEntity deviceMasterEntity = deviceRepo.findBybranchCode(branchCode);
			if (deviceMasterEntity != null) {
				responseList.add(branchEntity);
			}

			List<BranchMasterEntity> list = branchMasterRepository.findByParentCode(branchCode);

			if (list.isEmpty()) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Branches not found");
			} else {
				list.forEach(x -> {
					DeviceMasterEntity deviceMaster = deviceRepo.findBybranchCode(x.getBranchCode());
					if (deviceMaster != null) {
						responseList.add(x);
					}

				});

				if (branchMasterRepository.findBybranchCode(branchCode).getBranchType()
						.equalsIgnoreCase("HEAD OFFICE")) {
					for (BranchMasterEntity x : list) {
						List<BranchMasterEntity> zoneChildren = branchMasterRepository
								.findByParentCode(x.getBranchCode());

						zoneChildren.forEach(item -> {
							DeviceMasterEntity deviceEntity = deviceRepo.findBybranchCode(item.getBranchCode());

							if (deviceEntity != null) {
								responseList.add(item);
							}

						});

					}

				}

			}
			responseList.sort(Comparator.comparing(BranchMasterEntity::getBranchDesc));
			response.setPayload(responseList);
			response.setMessage("Branches fetched successfully");

		} catch (Exception e) {
			response.setMessage("Error occuring while fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/**
	 * ADD BRANCH INFORMATION
	 */
	public ResponseEntity addOrUpdateBranch(BranchMasterDTO branchRequest, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDate = date.format(formatter);

		String loggedInUserEmpId = (String) session.getAttribute("empId");
		try {

			String phoneNumberWithCountryCode = "91" + branchRequest.getMobile();
			branchRequest.setMobile(phoneNumberWithCountryCode);
			ParamEntity paramEntity = paramRepo.findByCode("BRANCH_ID");
			branchRequest.setBranchCode(paramEntity.getDescp1() + paramEntity.getDescp2());
			paramService.updateBranchDescp2Value(paramEntity);

			BranchMasterEntity entity = null;

			BranchMasterEntity branchMaster = branchMasterRepository.findByIfscAndBank(branchRequest.getIfsc(),
					branchRequest.getBank());

			/*
			 * if (branchMaster != null &&
			 * "Active".equalsIgnoreCase(branchMaster.getStatus())) {
			 * response.setMessage("This branch ifsc is already present.");
			 * response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			 * response.setPayload(branchMaster); } else {
			 */

			Optional<BranchMasterEntity> optional = branchMasterRepository.findById(branchRequest.getBranchCode());
			if (!optional.isPresent()) {
				entity = new BranchMasterEntity();
				String latitude = "0", longitude = "0";
				try {
					if (branchRequest.getGeoLocation() != null && !branchRequest.getGeoLocation().isEmpty()) {
						String[] latLong = branchRequest.getGeoLocation().split(",");

						System.out.println(latLong);
						if (latLong.length == 2) {
							latitude = latLong[0];
							longitude = latLong[1];
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				entity.setLatitude(latitude);
				entity.setLongitude(longitude);

			} else if (optional.isPresent()) {
				entity = optional.get();
			}

			BeanUtils.copyProperties(branchRequest, entity);
			entity.setCreatedDate(formattedDate);
			// entity.setUpdatedDate(formattedDate);
			entity.setCount(1);
			entity.setUpdationStatus("Save");
			entity.setCreatedBy(loggedInUserEmpId);
			branchMasterRepository.save(entity);

			response.setMessage("Branch added successfully");
			response.setStatusCode(HttpStatus.OK.value());
			response.setPayload(entity);

		} catch (Exception e) {
			response.setMessage("Error occuring while saving branch");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity editBranch(BranchMasterDTO branchRequest, HttpSession session) {
		ResponseEntity response = new ResponseEntity();
		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDate = date.format(formatter);

		String loggedInUserEmpId = (String) session.getAttribute("empId");
		try {

			BranchMasterEntity entity = null;
			Optional<BranchMasterEntity> optional = branchMasterRepository.findById(branchRequest.getBranchCode());
			if (optional.isPresent()) {
				entity = optional.get();
				BeanUtils.copyProperties(branchRequest, entity);

				entity.setMobile("91" + branchRequest.getMobile());
				entity.setUpdatedDate(formattedDate);
				int newCount = entity.getCount() + 1;
				entity.setCount(newCount);
				entity.setUpdationStatus("Edit");
				entity.setUpdatedBy(loggedInUserEmpId);
				branchMasterRepository.save(entity);
				response.setMessage("Branch edited successfully");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(entity);
			} else {

				response.setMessage("Branch not found with this code :" + branchRequest.getBranchCode());
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}

		} catch (Exception e) {
			response.setMessage("Error occuring while saving branch");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getBranchByParentCode(String parentCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterDTO> responseList = new ArrayList<>();
			List<BranchMasterEntity> list = branchMasterRepository.findByParentCode(parentCode);
			if (list.isEmpty()) {
				response.setMessage("No branch found for this parent code");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				list.forEach(x -> {
					BranchMasterDTO branchResponse = new BranchMasterDTO();
					BeanUtils.copyProperties(x, branchResponse);
					responseList.add(branchResponse);
				});
				response.setPayload(responseList);
				response.setMessage("Branches found successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("BError occured while branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllBranch() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterDTO> responseList = new ArrayList<>();
			List<BranchMasterEntity> list = branchMasterRepository.findAll();
			if (list.isEmpty()) {
				response.setMessage("No branch found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				list.forEach(x -> {
					BranchMasterDTO branchResponse = new BranchMasterDTO();
					BeanUtils.copyProperties(x, branchResponse);
					responseList.add(branchResponse);
				});
				response.setPayload(responseList);
				response.setMessage("Branches found successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("BError occured while branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/**
	 * DELETE BRANCH INFORMATION
	 * 
	 * @return
	 * @throws Exception
	 */
	public String deleteBranchInformation(String branchCode) throws Exception {
		try {
			BranchMasterEntity branchMasterEntity = branchMasterRepository.findById(branchCode)
					.orElseThrow(() -> new NotFoundException("Branch not found with ID: " + branchCode));

			// branchMasterRepository.delete(branchMasterEntity);
			branchMasterEntity.setStatus("inActive");
			branchMasterRepository.save(branchMasterEntity);
			DeviceMasterEntity deviceMasterEntity = deviceRepo.findBybranchCode(branchCode);

			if (deviceMasterEntity != null) {
				deviceMasterService.deleteDeviceMasterByDeviceId(deviceMasterEntity.getDeviceId());

			}

			List<EmployeeMasterEntity> employeeMasterEntity = employeeMasterRepository.findByBranch(branchCode);

			if (!employeeMasterEntity.isEmpty()) {

				employeeMasterEntity.forEach(employee -> {

					employee.setStatus("inActive");
					employeeMasterRepository.save(employee);

				});

			}

			return "delete Branch information sucessfully";

		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to delete branch information.", e);
		}
	}

	public ResponseEntity updateActiveStatus(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {

			Optional<BranchMasterEntity> optional = branchMasterRepository.findById(branchCode);

			if (!optional.isPresent()) {
				response.setMessage("Branch not found ");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else if (optional.isPresent()) {
				BranchMasterEntity entity = optional.get();
				entity.setStatus("Active");

				branchMasterRepository.save(entity);
				response.setMessage("Status updated successfully");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(entity);
			}
		} catch (Exception e) {
			response.setMessage("Error occuring while updating branch status");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity deactivateBranch(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {

			Optional<BranchMasterEntity> optional = branchMasterRepository.findById(branchCode);

			if (!optional.isPresent()) {
				response.setMessage("Branch not found ");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else if (optional.isPresent()) {
				BranchMasterEntity entity = optional.get();
				entity.setStatus("deActive");

				branchMasterRepository.save(entity);
				response.setMessage("Status updated successfully");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(entity);
			}
		} catch (Exception e) {
			response.setMessage("Error occuring while updating branch status");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getBranchByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterDTO> responseList = new ArrayList<>();
			BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCode);

			if (branch == null) {
				response.setMessage("No branch found for this branch code");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				BranchMasterDTO branchResponse = new BranchMasterDTO();

				BeanUtils.copyProperties(branch, branchResponse);

				BranchMasterEntity parentBranchEntity = (branch.getParentCode() != null)
						? branchMasterRepository.findBybranchCode(branch.getParentCode())
						: null;

				if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
					branchResponse.setParentBranchName(parentBranchEntity.getBranchDesc());
				}
				DeviceMasterEntity device = deviceRepo.findBybranchCode(branchCode);

				if (device != null) {
					branchResponse.setVendorName(device.getVendorName());
				}
				responseList.add(branchResponse);

				response.setPayload(responseList);
				response.setMessage("Branches found successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Error occurred while retrieving branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	/**
	 * VALIDATIONS FOR ADDING BRANCH MASTER
	 */
	public List<Message> validation(BranchMasterDTO branchMasterDTO) {
		List<Message> validateError = new ArrayList<>();

		if (branchMasterDTO.getBranchCode() == null || branchMasterDTO.getBranchCode().isEmpty()) {
			validateError.add(new Message("branchCode", " Branch code cannot be null"));
		}
		if (branchMasterDTO.getBranchDesc() == null || branchMasterDTO.getBranchDesc().isEmpty()) {
			validateError.add(new Message("branchDesc", " Branch desc cannot be null"));
		}
		if (branchMasterDTO.getAddress1() == null || branchMasterDTO.getAddress1().isEmpty()) {
			validateError.add(new Message("address1", " Address1 cannot be null"));
		}
		if (branchMasterDTO.getBranchType() == null || branchMasterDTO.getBranchType().isEmpty()) {
			validateError.add(new Message("branchType", "Branch type cannot be null"));
		}
		if (branchMasterDTO.getContractPerson() == null || branchMasterDTO.getContractPerson().isEmpty()) {
			validateError.add(new Message("contractPerson", " Contract person cannot be null"));
		}
		if (branchMasterDTO.getEmail() == null || branchMasterDTO.getEmail().isEmpty()) {
			validateError.add(new Message("email", " Email cannot be null"));
		}
		if (branchMasterDTO.getParentCode() == null || branchMasterDTO.getParentCode().isEmpty()) {
			validateError.add(new Message("parentCode", "Parent code cannot be null"));
		}

		if (branchMasterDTO.getMobile() == null || branchMasterDTO.getMobile().isEmpty()) {
			validateError.add(new Message("mobile", " Mobile cannot be null"));
		}

		if (branchMasterDTO.getFaxNo() == null || branchMasterDTO.getFaxNo().isEmpty()) {
			validateError.add(new Message("faxNo", " Fax no cannot be null"));
		}
		if (branchMasterDTO.getStatus() == null || branchMasterDTO.getStatus().isEmpty()) {
			validateError.add(new Message("status", " Status cannot be null"));
		}

		return validateError;
	}

//	public ResponseEntity getAllBranchForHierarchy() {
//	    ResponseEntity response = new ResponseEntity();
//	    try {
//	        List<BranchMasterDTO> responseList = new ArrayList<>();
//	        List<BranchMasterEntity> list = branchMasterRepository.findAll();
//	        if (list.isEmpty()) {
//	            response.setMessage("No branch found");
//	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
//	        } else {
//	            // Map to store branches with parent code as key
//	            Map<String, BranchMasterDTO> branchMap = new HashMap<>();
//
//	            // First pass to build the map
//	            for (BranchMasterEntity branch : list) {
//	                BranchMasterDTO branchDTO = new BranchMasterDTO();
//	                BeanUtils.copyProperties(branch, branchDTO);
//	                String branchCode = branchDTO.getBranchCode();
//	                branchMap.put(branchCode, branchDTO);
//	            }
//
//	            // Second pass to build the hierarchy
//	            for (BranchMasterEntity branch : list) {
//	                String parentCode = branch.getParentCode();
//	                if (!parentCode.equals("0")) {
//	                    BranchMasterDTO parentBranch = branchMap.get(parentCode);
//	                    if (parentBranch != null) {
//	                        BranchMasterDTO currentBranch = branchMap.get(branch.getBranchCode());
//	                        parentBranch.addChild(currentBranch);
//	                    }
//	                } else {
//	                    responseList.add(branchMap.get(branch.getBranchCode()));
//	                }
//	            }
//
//	            response.setPayload(responseList);
//	            response.setMessage("Branches found successfully");
//	            response.setStatusCode(HttpStatus.OK.value());
//	        }
//	    } catch (Exception e) {
//	        response.setMessage("Error occurred while fetching branches");
//	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	    }
//	    return response;
//	}

//	public ResponseEntity getAllBranchForHierarchy(String loggedInBranch) {
//		ResponseEntity response = new ResponseEntity();
//		try {
//			System.out.println("loggedInBranch---->>>>>>>>" + loggedInBranch);
//			List<BranchMasterDTO> responseList = new ArrayList<>();
//
//			ResponseEntity branchesList = getAllChildren(loggedInBranch);
//			List<BranchMasterEntity> list = (List<BranchMasterEntity>) branchesList.getPayload();
//
//			// System.out.println("List---->>>>>>>>"+list);
//			// List<BranchMasterEntity> list = branchMasterRepository.findAll();
//			if (list.isEmpty()) {
//				System.out.println("List1---->>>>>>>>" + list);
//
//				response.setMessage("No branch found");
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//			} else {
//
//				System.out.println("List2---->>>>>>>>" + list);
//
//				list = list.stream().filter(branch -> !"inActive".equalsIgnoreCase(branch.getStatus()))
//						.collect(Collectors.toList());
//
//				// Map to store branches with parent code as key
//				Map<String, BranchMasterDTO> branchMap = new HashMap<>();
//
//				for (BranchMasterEntity branch : list) {
//					BranchMasterDTO branchDTO = new BranchMasterDTO();
//					System.out.println("BranchEntity  " + branch);
//
//					BeanUtils.copyProperties(branch, branchDTO);
//					System.out.println("BranchDTO" + branchDTO);
//					String branchCode = branchDTO.getBranchCode();
//					branchMap.put(branchCode, branchDTO);
//					System.out.println("3---->>>>>>>>" + branchMap.toString());
//				}
//
//				for (BranchMasterEntity branch : list) {
//					String parentCode = branch.getParentCode();
//					if (!parentCode.equals("0")) {
//						BranchMasterDTO parentBranch = branchMap.get(parentCode);
//						if (parentBranch != null) {
//							BranchMasterDTO currentBranch = branchMap.get(branch.getBranchCode());
//							System.out.println("4---->>>>>>>>" + branch.getBranchCode());
//							parentBranch.addChild(currentBranch);
//
//						} else {
//							System.out.println(
//									"Parent branch not found for: " + branch.getBranchCode() + ". Treating as root.");
//							BranchMasterDTO currentBranch = branchMap.get(branch.getBranchCode());
//							responseList.add(currentBranch);
//						}
//					} else {
//						responseList.add(branchMap.get(branch.getBranchCode()));
//						System.out.println("5---->>>>>>>>" + branch.getBranchCode());
//					}
//				}
//
//				response.setPayload(responseList);
//				response.setMessage("Branches found successfully");
//				response.setStatusCode(HttpStatus.OK.value());
//			}
//		} catch (Exception e) {
//			response.setMessage("Error occurred while fetching branches");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		}
//		return response;
//	}

	public ResponseEntity getAllBranchForHierarchy(String loggedInBranch) {

		ResponseEntity response = new ResponseEntity();

		try {

			List<BranchMasterDTO> responseList = new ArrayList<>();

			ResponseEntity branchesList = getAllChildren(loggedInBranch);

			List<BranchMasterEntity> list = (List<BranchMasterEntity>) branchesList.getPayload();

			if (list.isEmpty()) {

				response.setMessage("No branch found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());

			} else {

				list = list.stream().filter(branch -> !"inActive".equalsIgnoreCase(branch.getStatus()))
						.sorted(Comparator.comparing(BranchMasterEntity::getCreatedDate)).collect(Collectors.toList());

				/*
				 * ========================================== NEW LOGIC Load all banks once
				 * ==========================================
				 */
				List<BankMasterEntity> bankList = bankMasterRepository.findAll();

				Map<String, String> bankNameMap = bankList.stream()
						.collect(Collectors.toMap(BankMasterEntity::getBankCode, BankMasterEntity::getBankName,
								(existing, replacement) -> existing));

				Map<String, BranchMasterDTO> branchMap = new HashMap<>();

				for (BranchMasterEntity branch : list) {

					BranchMasterDTO branchDTO = new BranchMasterDTO();

					System.out.println("BranchEntity  " + branch);

					BeanUtils.copyProperties(branch, branchDTO);

					/*
					 * OLD LOGIC SAME
					 */
					BranchMasterEntity parentBranchEntity = (branch.getParentCode() != null)
							? branchMasterRepository.findBybranchCode(branch.getParentCode())
							: null;

					if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {

						branchDTO.setParentBranchName(parentBranchEntity.getBranchDesc());
					}

					/*
					 * ========================================== NEW LOGIC Bank Code -> Bank Name
					 * ==========================================
					 */
					String bankCode = branch.getBank();

					if (bankCode != null && !bankCode.trim().isEmpty()) {

						String bankName = bankNameMap.get(bankCode);

						branchDTO.setBank(bankName);
					}

					System.out.println("BranchDTO" + branchDTO);

					String branchCode = branchDTO.getBranchCode();

					branchMap.put(branchCode, branchDTO);
				}

				/*
				 * OLD LOGIC SAME
				 */
				for (BranchMasterEntity branch : list) {

					String parentCode = branch.getParentCode();

					if (!parentCode.equals("0")) {

						BranchMasterDTO parentBranch = branchMap.get(parentCode);

						if (parentBranch != null) {

							BranchMasterDTO currentBranch = branchMap.get(branch.getBranchCode());

							parentBranch.addChild(currentBranch);

						} else {

							System.out.println(
									"Parent branch not found for: " + branch.getBranchCode() + ". Treating as root.");

							BranchMasterDTO currentBranch = branchMap.get(branch.getBranchCode());

							responseList.add(currentBranch);
						}

					} else {

						responseList.add(branchMap.get(branch.getBranchCode()));
					}
				}

				response.setPayload(responseList);

				response.setMessage("Branches found successfully");

				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Error occurred while fetching branches");

			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getAllHeadOffice() {
		ResponseEntity response = new ResponseEntity();
		try {

			List<BranchMasterEntity> list = branchMasterRepository.findByBranchType("HEAD OFFICE");
			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {

				response.setPayload(list);
				response.setMessage("Head office fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			response.setMessage("Error fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllHeadOfficeAndZones(String branchType, String bank) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> headOfficeList = branchMasterRepository.findByBranchTypeAndBank(branchType, bank);
			// List<BranchMasterEntity> zoneList =
			// branchMasterRepository.findByBranchType("ZONE");

			// Filter only active ones
			List<BranchMasterEntity> activeHeadOffices = headOfficeList.stream()
					.filter(branch -> "ACTIVE".equalsIgnoreCase(branch.getStatus())).collect(Collectors.toList());

			if (activeHeadOffices.isEmpty()) {
				response.setMessage("No active head offices or zones found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				List<BranchMasterEntity> mergedList = new ArrayList<>();
				mergedList.addAll(activeHeadOffices);
				// mergedList.addAll(activeZones);

				response.setPayload(mergedList);
				response.setMessage("Active head offices and zones fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Error fetching branches: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

//	public ResponseEntity getAllChildren(String branchCode) {
//		ResponseEntity response = new ResponseEntity();
//		try {
//			
//			List<BranchMasterEntity> list = branchMasterRepository.findByParentCode(branchCode);
//			if(list.isEmpty()) {
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//				response.setMessage("Branches not found");
//			}else {
//				response.setPayload(list);
//				response.setMessage("Branches fetched successfully");
//				response.setStatusCode(HttpStatus.OK.value());
//			}
//			
//			
//		}catch (Exception e) {
//			response.setMessage("Error occuring while fetching branches");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		}
//		return response;
//	}

//	public ResponseEntity getAllChildren(String branchCode) {
//		ResponseEntity response = new ResponseEntity();
//		try {
//			List<BranchMasterEntity> responseList = new ArrayList<>();
//			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
//			responseList.add(branchEntity);
//			List<BranchMasterEntity> list = branchMasterRepository.findByParentCode(branchCode);
//			
//			if(list.isEmpty()) {
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//				response.setMessage("Branches not found");
//			}else {
//				responseList.addAll(list);
//				if(branchMasterRepository.findBybranchCode(branchCode).getBranchType().equalsIgnoreCase("HEAD OFFICE")) {
//					for(BranchMasterEntity x:list) {
//						List<BranchMasterEntity> zoneChildren=branchMasterRepository.findByParentCode(x.getBranchCode());
//						responseList.addAll(zoneChildren);
//					}
//					
//				}
//				
//				
//				
//				
//				
//			}
//			
//			responseList = responseList.stream()
//			           .filter(branch -> !"inactive".equalsIgnoreCase(branch.getStatus()))
//			           .collect(Collectors.toList());
//			
//			
//			//System.out.println("response =====>>>>"+responseList);
//			response.setPayload(responseList);
//			response.setMessage("Branches fetched successfully");
//			
//		}catch (Exception e) {
//			response.setMessage("Error occuring while fetching branches");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		}
//		return response;
//	}

	public ResponseEntity getAllChildren(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> resultList = new ArrayList<>();
			Set<String> visited = new HashSet<>();

			BranchMasterEntity root = branchMasterRepository.findBybranchCode(branchCode);
			if (root == null || "inactive".equalsIgnoreCase(root.getStatus())) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Branch not found or inactive");
				return response;
			}

			// Start the recursion with root
			fetchChildBranchesRecursively(root, resultList, visited);

			response.setPayload(resultList);
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Branches fetched successfully");

		} catch (Exception e) {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error occurred while fetching branches");
		}
		return response;
	}

	private void fetchChildBranchesRecursively(BranchMasterEntity currentBranch, List<BranchMasterEntity> resultList,
			Set<String> visited) {
		if (currentBranch == null || visited.contains(currentBranch.getBranchCode())) {
			return;
		}

		visited.add(currentBranch.getBranchCode());

		BranchMasterEntity parentBranchEntity = (currentBranch.getParentCode() != null)
				? branchMasterRepository.findBybranchCode(currentBranch.getParentCode())
				: null;

		if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
			currentBranch.setCreatedBy(parentBranchEntity.getBranchDesc());
		}

// Add only active branches
		if (!"inactive".equalsIgnoreCase(currentBranch.getStatus())) {
			resultList.add(currentBranch);
		}

// Get direct children of this branch
		List<BranchMasterEntity> children = branchMasterRepository.findByParentCode(currentBranch.getBranchCode());

		for (BranchMasterEntity child : children) {
			fetchChildBranchesRecursively(child, resultList, visited);
		}
	}

	public ResponseEntity findByBranchCodeContainingPattern(String branchCode) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> responseList = branchMasterRepository
					.findByBranchCodeContainingPattern(branchCode);
			if (responseList != null) {
				response.setPayload(responseList);
				response.setMessage("Branches fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No Branch is present in the database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getBranchMasterByStatus(String status) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> branchList = branchMasterRepository.findByStatus(status);
			List<BranchMasterDTO> branchListDTO = new ArrayList<>();
			if (!branchList.isEmpty()) {
				for (BranchMasterEntity branchEntity : branchList) {
					BranchMasterDTO branchDTO = new BranchMasterDTO();
					BeanUtils.copyProperties(branchEntity, branchDTO);
					branchListDTO.add(branchDTO);

				}
				response.setPayload(branchListDTO);
				response.setMessage("Data Fetched..");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				// response.setPayload(branchListDTO);
				response.setMessage("Data NOT Fetched..");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occured!!" + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getBranchMasterByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> branchList = branchMasterRepository.findByBranchCode(branchCode);
			List<BranchMasterDTO> branchListDTO = new ArrayList<>();
			if (!branchList.isEmpty()) {
				for (BranchMasterEntity branchEntity : branchList) {
					BranchMasterDTO branchDTO = new BranchMasterDTO();
					BeanUtils.copyProperties(branchEntity, branchDTO);
					branchListDTO.add(branchDTO);

				}
				response.setPayload(branchListDTO);
				response.setMessage("Data Fetched..");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				// response.setPayload(branchListDTO);
				response.setMessage("Data NOT Fetched..");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error occured!!" + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity findByBranchCodeContainingPatternForZone(String branchCode) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> responseList = branchMasterRepository
					.findByBranchCodeContainingPatternForZone(branchCode);
			List<BranchMasterDTO> branchDTOList = new ArrayList<BranchMasterDTO>();
			List<BranchMasterEntity> branchResponseList = new ArrayList<BranchMasterEntity>();
			if (responseList != null) {

				for (BranchMasterEntity bMasterEntity : responseList) {
					branchResponseList.add(bMasterEntity);
					branchResponseList.addAll(branchMasterRepository.findByParentCode(bMasterEntity.getBranchCode()));
				}
				System.out.println(branchResponseList);
				response.setPayload(branchResponseList);
				response.setMessage("Branches fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No Branch is present in the database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity findByBranchCodeContainingPatternForZone(String branchCode, String status) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> responseList;
			List<BranchMasterDTO> branchDTOList = new ArrayList<>();

			if (status != null && !status.isEmpty()) {
				if (branchCode != null && !branchCode.isEmpty()) {
					responseList = branchMasterRepository.findByBranchCodeContainingPatternForZoneWithStatus(branchCode,
							status);
				} else {
					responseList = branchMasterRepository.findByStatus(status);
				}
			} else {
				responseList = branchMasterRepository.findByBranchCodeContainingPatternForZone(branchCode);
			}

			if (!responseList.isEmpty()) {
				for (BranchMasterEntity branch : responseList) {
					BranchMasterDTO branchDTO = new BranchMasterDTO();
					BeanUtils.copyProperties(branch, branchDTO);
					branchDTOList.add(branchDTO);
				}
				response.setPayload(branchDTOList);
				response.setMessage("Branches fetched successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No Branch is present in the database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getAllZones() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> list = branchMasterRepository.findByBranchType("ZONE");

			if (!list.isEmpty()) {
				response.setPayload(list);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			response.setMessage("Error occured while fetching data");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
		}
		return response;
	}

	public ResponseEntity getAllBranchesAndDevicesbyBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			ResponseEntity branchListEntity = getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchListEntity.getPayload();
			List<BranchDeviceDTO> responseList = new ArrayList<>();
			if (branchList.isEmpty()) {
				response.setMessage("Branches not found for branchcode :" + branchCode);
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				branchList.forEach(branch -> {
					DeviceMasterEntity deviceMasterEntity = deviceRepo.findBybranchCode(branch.getBranchCode());
					if (deviceMasterEntity != null) {
						BranchDeviceDTO branchDeviceDTO = new BranchDeviceDTO();
						branchDeviceDTO.setBranchCode(branch.getBranchCode());
						branchDeviceDTO.setBranchName(branch.getBranchDesc());
						branchDeviceDTO.setDeviceId(deviceMasterEntity.getDeviceSerialNo());
						responseList.add(branchDeviceDTO);
					} else {
						BranchDeviceDTO branchDeviceDTO = new BranchDeviceDTO();
						branchDeviceDTO.setBranchCode(branch.getBranchCode());
						branchDeviceDTO.setBranchName(branch.getBranchDesc());
						responseList.add(branchDeviceDTO);
					}
				});
			}
			response.setPayload(responseList);
			response.setMessage("Data fetched successfully for branchcode :" + branchCode);
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			response.setMessage("Error fetching data for branchcode :" + branchCode);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllDetailsByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchDTO> responseList = new ArrayList<>();

			BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(branchCode);
			if (branchEntity != null) {
				BranchDTO branchDTO = mapBranchEntityToDTO(branchEntity);
				responseList.add(branchDTO);
			}

			List<BranchMasterEntity> childBranches = branchMasterRepository.findByParentCode(branchCode);
			if (!childBranches.isEmpty()) {
				for (BranchMasterEntity branch : childBranches) {
					BranchDTO branchDTO = mapBranchEntityToDTO(branch);
					responseList.add(branchDTO);

					if (branchEntity != null && branchEntity.getBranchType().equalsIgnoreCase("HEAD OFFICE")) {
						List<BranchMasterEntity> zoneChildren = branchMasterRepository
								.findByParentCode(branch.getBranchCode());
						for (BranchMasterEntity zoneChild : zoneChildren) {
							BranchDTO zoneChildDTO = mapBranchEntityToDTO(zoneChild);
							responseList.add(zoneChildDTO);
						}
					}
				}
			}

			if (responseList.isEmpty()) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Branches not found");
			} else {
				response.setPayload(responseList);
				response.setStatusCode(HttpStatus.OK.value());
				response.setMessage("Branches fetched successfully");
			}
		} catch (Exception e) {
			response.setMessage("Error occurred while fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Helper method to map BranchMasterEntity to BranchDTO.
	 */
	private BranchDTO mapBranchEntityToDTO(BranchMasterEntity branchEntity) {
		BranchDTO branchDTO = new BranchDTO();
		branchDTO.setBranchCode(branchEntity.getBranchCode());
		branchDTO.setBranchType(branchEntity.getBranchType());
		branchDTO.setBranchDesc(branchEntity.getBranchDesc());

		DeviceMasterEntity device = deviceRepo.findBybranchCode(branchEntity.getBranchCode());
		if (device != null) {
			branchDTO.setDeviceId(device.getDeviceId());
			branchDTO.setDeviceSerialNo(device.getDeviceSerialNo());
			List<DeviceZoneDetailsEntity> deviceZones = detailsRepository.findByDeviceId(device.getDeviceId());
			if (deviceZones != null && !deviceZones.isEmpty()) {
				List<DeviceZoneDTO> deviceZoneDetails = new ArrayList<>();
				for (DeviceZoneDetailsEntity deviceEntity : deviceZones) {
					DeviceZoneDTO deviceZoneDTO = new DeviceZoneDTO();
					deviceZoneDTO.setDeviceId(deviceEntity.getDeviceId());
					deviceZoneDTO.setZoneOriginalName(deviceEntity.getZoneOriginalName());
					deviceZoneDTO.setAlertType(deviceEntity.getAlertType());
					deviceZoneDTO.setZoneDesiredName(deviceEntity.getZoneDesiredName());
					deviceZoneDetails.add(deviceZoneDTO);
				}
				branchDTO.setDeviceZones(deviceZoneDetails);
			}
		}

		return branchDTO;
	}

	public ResponseEntity getBranchesByBranchCodes(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BOIMasterDataDTO> responseList = new ArrayList<>();

			List<BranchMasterEntity> branchList = new ArrayList<>();
			BranchMasterEntity rootBranch = branchMasterRepository.findBybranchCode(branchCode);
			if (rootBranch == null) {
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				response.setMessage("Branch code not found");
				return response;
			}

			if (rootBranch.getBranchType().equalsIgnoreCase("BRANCH")) {
				branchList.add(rootBranch);
			}

			List<BranchMasterEntity> children = branchMasterRepository.findByParentCode(branchCode);
			branchList.addAll(children);

			if (rootBranch.getBranchType().equalsIgnoreCase("HEAD OFFICE")) {
				for (BranchMasterEntity zone : children) {
					if (zone.getBranchType().equalsIgnoreCase("BRANCH")) {
						List<BranchMasterEntity> zones = branchMasterRepository.findByParentCode(zone.getBranchCode());
						branchList.addAll(zones);
					}
				}
			}

			Map<String, String> ifscToBranchCodeMap = branchList.stream()
					.filter(b -> !"inactive".equalsIgnoreCase(b.getStatus()))
					.collect(Collectors.toMap(BranchMasterEntity::getIfsc, BranchMasterEntity::getBranchCode));

			List<BOIMasterDataEntity> boiList = boiMasterDataRepository.findAll();

			for (BOIMasterDataEntity boi : boiList) {
				String branchCodeFromMap = ifscToBranchCodeMap.get(boi.getBranchCode());
				if (branchCodeFromMap != null) {
					BOIMasterDataDTO dto = new BOIMasterDataDTO();
					BeanUtils.copyProperties(boi, dto);
					dto.setOriginalBranchCode(branchCodeFromMap);
					responseList.add(dto);
				}
			}

			response.setPayload(responseList);
			response.setMessage("BOI Master data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			response.setMessage("Error occurred while fetching BOI master data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllZonesByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			// List<BranchMasterEntity> responseList = new ArrayList<>();

			ResponseEntity responseList = getAllChildren(branchCode);
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseList.getPayload();

			List<BranchMasterEntity> branchResponseList = branchList.stream()
					.filter(branch -> branch.getBranchType().equalsIgnoreCase("ZONE")).collect(Collectors.toList());

			// System.out.println("response =====>>>>"+responseList);
			response.setPayload(branchResponseList);
			response.setMessage("Branches fetched successfully");

		} catch (Exception e) {
			response.setMessage("Error occuring while fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

//	public ResponseEntity getHeadOfficeByBranchCode(String branchCode) {
//		ResponseEntity response = new ResponseEntity();
//		try {
//			BranchMasterEntity currentBranch = branchMasterRepository.findBybranchCode(branchCode);
//
//			if (currentBranch == null) {
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//				response.setMessage("Branch not found with code: " + branchCode);
//				return response;
//			}
//
//			// Traverse up the hierarchy until head office is found
//			while (currentBranch != null && !currentBranch.getBranchType().equalsIgnoreCase("HEAD OFFICE")) {
//				String parentCode = currentBranch.getParentCode();
//				if (parentCode == null || parentCode.trim().isEmpty()) {
//					break; // Avoid infinite loop if data is bad
//				}
//				currentBranch = branchMasterRepository.findBybranchCode(parentCode);
//			}
//
//			if (currentBranch != null && currentBranch.getBranchType().equalsIgnoreCase("HEAD OFFICE")) {
//				response.setPayload(currentBranch);
//				response.setMessage("Head Office found successfully.");
//			} else {
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//				response.setMessage("Head Office not found for branch: " + branchCode);
//			}
//		} catch (Exception e) {
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//			response.setMessage("Error occurred while fetching Head Office.");
//		}
//
//		return response;
//	}

	
	  public ResponseEntity getHeadOfficeByBranchCode(String branchCode) {
	  ResponseEntity response = new ResponseEntity(); try { ResponseEntity
	  responseEntity = getAllChildren(branchCode);
	  
	  List<BranchMasterEntity> branchList = (List<BranchMasterEntity>)
	  responseEntity.getPayload();
	  
	  if (branchList != null && !branchList.isEmpty()) { // Filter HEAD OFFICE only
	  List<BranchMasterEntity> headOffices = branchList.stream() .filter(branch ->
	  "HEAD OFFICE".equalsIgnoreCase(branch.getBranchType()))
	  .collect(Collectors.toList());
	  
	  if (!headOffices.isEmpty()) { response.setPayload(headOffices);
	  response.setStatusCode(HttpStatus.OK.value());
	  response.setMessage("Head Offices found successfully for branch: " +
	  branchCode); } else { response.setPayload(0);
	  response.setStatusCode(HttpStatus.NOT_FOUND.value());
	  response.setMessage("No Head Office found for branch: " + branchCode); } }
	  else { response.setPayload(0);
	  response.setStatusCode(HttpStatus.NOT_FOUND.value());
	  response.setMessage("No branches found for branch: " + branchCode); }
	  
	  } catch (Exception e) { response.setPayload(0);
	  response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	  response.setMessage("Error occurred while fetching Head Office.");
	  e.printStackTrace(); }
	  
	  return response; }
	 

	

	public ResponseEntity getBranchDetailsByVendorId(String vendorId) {
		ResponseEntity response = new ResponseEntity();

		List<BranchMasterEntity> branchMasterList = branchMasterRepository.findBySystemIntegrator(vendorId);

		if (branchMasterList.isEmpty()) {
			// throw new GenericException("No branch found for vendor ID: " + vendorId);
			response.setMessage(
					"BranchMasterService ::: getBranchDetailsByVendorId(vendorId) ::: Branches not found by vendor id :: "
							+ vendorId);
			response.setStatusCode(HttpStatus.NO_CONTENT.value());
		}
		List<BranchMasterDTO> dtoList = branchMasterList.stream().map(entity -> {

			String vendorName = vendorMasterRepository.findById(entity.getSystemIntegrator()).get().getVendorName();
			BranchMasterDTO dto = new BranchMasterDTO();
			BeanUtils.copyProperties(entity, dto);
			dto.setVendorName(vendorName);
			return dto;
		}).collect(Collectors.toList());

		response.setMessage("Branch details fetched successfully.");
		response.setPayload(dtoList);
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

	public ResponseEntity getAllSuperHO() {
		ResponseEntity response = new ResponseEntity();
		try {

			List<BranchMasterEntity> list = branchMasterRepository.findByBranchType("SUPER HO");
			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {

				response.setPayload(list);
				response.setMessage("Head office fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			response.setMessage("Error fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllSuperHOAndZones() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> list1 = branchMasterRepository.findByBranchType("SUPER HO");
			List<BranchMasterEntity> list2 = branchMasterRepository.findByBranchType("ZONE");

			List<BranchMasterEntity> combinedList = new ArrayList<>();
			combinedList.addAll(list1);
			combinedList.addAll(list2);

			if (combinedList.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setPayload(combinedList);
				response.setMessage("Branches fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Error fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllHOAndZones() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<BranchMasterEntity> list1 = branchMasterRepository.findByBranchType("HEAD OFFICE");
			List<BranchMasterEntity> list2 = branchMasterRepository.findByBranchType("ZONE");

			List<BranchMasterEntity> combinedList = new ArrayList<>();
			combinedList.addAll(list1);
			combinedList.addAll(list2);

			if (combinedList.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setPayload(combinedList);
				response.setMessage("Branches fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Error fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

//	public ResponseEntity getAllBranchByBranchType(String branchType, String branchCode) {
//
//	    ResponseEntity response = new ResponseEntity();
//
//	    try {
//
//	        ResponseEntity childResponse = getAllChildren(branchCode);
//
//	        if (childResponse.getPayload() == null) {
//	            response.setMessage("No branches found");
//	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
//	            return response;
//	        }
//
//	        @SuppressWarnings("unchecked")
//	        List<BranchMasterEntity> branchMasterEntities =
//	                (List<BranchMasterEntity>) childResponse.getPayload();
//
//	        List<BranchMasterEntity> filteredList = branchMasterEntities.stream()
//	                .filter(b -> branchType.equalsIgnoreCase(b.getBranchType()))
//	                .collect(Collectors.toList());
//	        
//	        filteredList.sort(
//	        	    Comparator.comparing(
//	        	        BranchMasterEntity::getBranchDesc,
//	        	        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
//	        	    )
//	        	);
//
//
//	        if (filteredList.isEmpty()) {
//	            response.setMessage("Data not found");
//	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
//	        } else {
//	            Map<String, Object> responseBody = new HashMap<>();
//	            responseBody.put("count", filteredList.size());
//	            responseBody.put("branches", filteredList);
//
//	            response.setPayload(responseBody);
//	            response.setMessage("Branches fetched successfully");
//	            response.setStatusCode(HttpStatus.OK.value());
//	        }
//
//	    } catch (Exception e) {
//	        response.setMessage("Error fetching branches");
//	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//	    }
//
//	    return response;
//	}

	public ResponseEntity getAllBranchByBranchType(String branchType, String branchCode) {

		ResponseEntity response = new ResponseEntity();

		try {
			// Fetch the requested branch first
			BranchMasterEntity requestedBranch = branchMasterRepository.findBybranchCode(branchCode);

			if (requestedBranch == null) {
				response.setMessage("Branch not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
				return response;
			}

			List<BranchMasterDTO> dtoList = new ArrayList<>();

			if ("Branch".equalsIgnoreCase(requestedBranch.getBranchType())) {
				// Special case: return parent branch only
				if (requestedBranch.getParentCode() != null) {
					BranchMasterEntity parentBranch = branchMasterRepository
							.findBybranchCode(requestedBranch.getParentCode());
					if (parentBranch != null) {

						BranchMasterEntity parentBranchEntity = (parentBranch.getParentCode() != null)
								? branchMasterRepository.findBybranchCode(parentBranch.getParentCode())
								: null;

						BranchMasterDTO dto = new BranchMasterDTO();
						dto.setBranchCode(parentBranch.getBranchCode());
						dto.setBranchDesc(parentBranch.getBranchDesc());
						dto.setBranchType(parentBranch.getBranchType());
						dto.setBank(parentBranch.getBank());
						dto.setParentBranchName(null); // parent of parent is optional
						if (parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
							dto.setParentBranchName(parentBranchEntity.getBranchDesc());
						}
						dtoList.add(dto);
					}
				}
			} else {
				// Normal case: fetch children
				ResponseEntity childResponse = getAllChildren(branchCode);

				if (childResponse.getPayload() != null) {
					@SuppressWarnings("unchecked")
					List<BranchMasterEntity> branchMasterEntities = (List<BranchMasterEntity>) childResponse
							.getPayload();

					dtoList = branchMasterEntities.stream().filter(b -> branchType.equalsIgnoreCase(b.getBranchType()))
							.map(branch -> {
								BranchMasterDTO dto = new BranchMasterDTO();
								dto.setBranchCode(branch.getBranchCode());
								dto.setBranchDesc(branch.getBranchDesc());
								dto.setBranchType(branch.getBranchType());
								dto.setBank(branch.getBank());

								// Set parent branch name
								Optional.ofNullable(branch.getParentCode())
										.map(parentCode -> branchMasterRepository.findBybranchCode(parentCode))
										.ifPresent(parent -> dto.setParentBranchName(parent.getBranchDesc()));

								return dto;
							})
							// Sort alphabetically by branchDesc
							.sorted(Comparator.comparing(BranchMasterDTO::getBranchDesc,
									Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
							.collect(Collectors.toList());
				}
			}

			if (dtoList.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				Map<String, Object> responseBody = new HashMap<>();
				responseBody.put("count", dtoList.size());
				responseBody.put("branches", dtoList);

				response.setPayload(responseBody);
				response.setMessage("Branches fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching branches");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getBranchDropdown(String branchCode) {
		ResponseEntity response = new ResponseEntity();

		ResponseEntity branchResponseEntity = getAllChildren(branchCode);
		if (branchResponseEntity.getPayload() == null) {
			response.setMessage("No branches found for branch code: " + branchCode);
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}

		List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) branchResponseEntity.getPayload();

		List<BranchDropdownDto> result = branchList.stream()
				.map(b -> new BranchDropdownDto(b.getBranchCode(), b.getBranchDesc())).collect(Collectors.toList());

		response.setPayload(result);
		response.setTotalElements(result.size());
		response.setMessage("Branch list fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

}
