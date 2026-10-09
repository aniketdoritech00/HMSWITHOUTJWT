package com.doritech.api.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.DTO.BranchComplaintDTO;
import com.doritech.api.DTO.PersonDetailsDTO;
import com.doritech.api.Entity.BranchComplainEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.PersonDetailsEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.BranchComplaintRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.PersonDetailsRepository;

@Service
public class PersonDetailsService {

	@Autowired
	PersonDetailsRepository personRepo;
	@Autowired
	MailService mailService;
	@Autowired
	ParamService paramService;
	@Autowired
	BranchMasterService branchService;
	@Autowired
	BranchMasterRepository branchRepo;
	
	@Autowired
	BranchComplaintRepository branchComplaintRepository;
	
	public ResponseEntity savePersonDetails(PersonDetailsDTO personDetailsRequest , List<MultipartFile> files){
		ResponseEntity response = new ResponseEntity();
		
		try {
			//if(!files.isEmpty()) {
				
					List<PersonDetailsEntity> list = new   ArrayList<>();
					String complaintId = paramService.getComplaintId();
					personDetailsRequest.setComplaintId(complaintId);
					paramService.updateDescp2("COMPLAINT_ID");
					
					if(personDetailsRequest!=null&& !files.isEmpty()) {
						for(MultipartFile file : files) {
						PersonDetailsEntity entity = null;
						
						Optional<PersonDetailsEntity> optional = personRepo.findById(personDetailsRequest.getId());
						if(!optional.isPresent()) {
							entity = new PersonDetailsEntity();
						}else {
							entity = optional.get();
						}
						
						
						BeanUtils.copyProperties(personDetailsRequest, entity);
						entity.setImage(file.getBytes());
						personRepo.save(entity);
						//entity.setImage(null);
						list.add(entity);
						
						//return response;
						}
						response.setPayload(list);
						response.setMessage("Data saved succesfully");
						response.setStatusCode(HttpStatus.OK.value());
					}else {
						
						response.setMessage("Bad request");
						response.setStatusCode(HttpStatus.BAD_REQUEST.value());
						//return response;
					}
				
		//return  response;
			
			
			
			
			
		}catch (Exception e) {
			response.setMessage("Error saving data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			//return response;
		}
		
		
		return response;
	}

	
	
//	public ResponseEntity updatePersonDetails(PersonDetailsDTO personDetailsRequest){
//		ResponseEntity response = new ResponseEntity();
//		
//		try {
//			//if(!files.isEmpty()) {
//				
//					
//					if(personDetailsRequest!=null) {
//						
//						PersonDetailsEntity entity = null;
//						
//						Optional<PersonDetailsEntity> optional = personRepo.findById(personDetailsRequest.getId());
//						if(optional.isEmpty()) {
//							entity = new PersonDetailsEntity();
//						}else {
//							entity = optional.get();
//						}
//						
//						personDetailsRequest.setImage(entity.getImage());
//						BeanUtils.copyProperties(personDetailsRequest, entity);
//						//entity.setImage(file.getBytes());
//						personRepo.save(entity);
//						
//						
//						response.setPayload(entity);
//						response.setMessage("Data saved succesfully");
//						response.setStatusCode(HttpStatus.OK.value());
//						//return response;
//					}else {
//						
//						response.setMessage("Bad request");
//						response.setStatusCode(HttpStatus.BAD_REQUEST.value());
//						//return response;
//					}
//				
//		//return  response;
//			
//			
//			
//			
//			
//		}catch (Exception e) {
//			response.setMessage("Error saving data");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//			//return response;
//		}
//		
//		
//		return response;
//	}
//	
	
	
	 public ResponseEntity updatePersonDetailsBatch(List<PersonDetailsDTO> personDetailsList) {
		        
		        ResponseEntity response = new ResponseEntity();
		        
		        try {
		            List<PersonDetailsEntity> entities = new ArrayList<>();
		            
		            if (personDetailsList != null && !personDetailsList.isEmpty()) {
		                for (PersonDetailsDTO personDetailsRequest : personDetailsList) {
		                    
		                    PersonDetailsEntity entity;
		                    
		                    Optional<PersonDetailsEntity> optional = personRepo.findById(personDetailsRequest.getId());
		                    if (!optional.isPresent()) {
		                        entity = new PersonDetailsEntity();
		                    } else {
		                        entity = optional.get();
		                    }
		                    
		                    // Retain the existing image if present
		                    if (entity.getImage() != null) {
		                        personDetailsRequest.setImage(entity.getImage());
		                    }
		                    
		                    BeanUtils.copyProperties(personDetailsRequest, entity);
		                    entities.add(entity);
		                }
		                
		                // Save all entities in batch
		                personRepo.saveAll(entities);
		                
		                response.setPayload(entities);
		                response.setMessage("Data updated successfully");
		                response.setStatusCode(HttpStatus.OK.value());
		            } else {
		                response.setMessage("Bad request");
		                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		            }
		        } catch (Exception e) {
		            response.setMessage("Error updating data");
		            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		            e.printStackTrace(); // Log the error for debugging
		        }
		        
		        return response;
		    }
	 
	public ResponseEntity getAllDetailsByBranchCode(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<PersonDetailsDTO> responseList = new ArrayList<>();
			ResponseEntity responseEntity = branchService.getAllChildren(branchCode);
			if(responseEntity!=null) {
				List<BranchMasterEntity> list= (List<BranchMasterEntity>) responseEntity.getPayload();
				
				for(BranchMasterEntity branch:list) {
					List<PersonDetailsEntity> personDetailsEntity = personRepo.findByBranchCode(branch.getBranchCode());
					
					if(!personDetailsEntity.isEmpty()) {
						for(PersonDetailsEntity person: personDetailsEntity) {
							PersonDetailsDTO personReponse = new PersonDetailsDTO();
							BeanUtils.copyProperties(person, personReponse);
							BranchMasterEntity branchDetails = branchRepo.findBybranchCode(person.getBranchCode());
							Optional<BranchComplainEntity> optional = branchComplaintRepository.findById(person.getComplaintId());
							
							if(optional.isPresent() && branchDetails!=null) {
								BranchComplainEntity branchComplainEntity=optional.get();
							
//							if(branchDetails!=null) {
								personReponse.setComplaintId(person.getComplaintId());
								personReponse.setBranchName(branchDetails.getBranchDesc());
								personReponse.setBank(branchDetails.getBank());
								personReponse.setIfsc(branchDetails.getIfsc());
								personReponse.setImageBase64(convertBytesToBase64(person.getImage()));
								personReponse.setRemarks(branchComplainEntity.getRemarks());
								responseList.add(personReponse);
								
//							}
							}
							
						}
						
						
						
						
						
						//responseList.addAll(personDetailsEntity);
					}
				}
			}
			
			response.setPayload(responseList);
			response.setMessage("Data fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
			
		}catch (Exception e) {
			
			e.printStackTrace();
			response.setMessage("Error occured while fetching data.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}
	
	
	 public String convertBytesToBase64(byte[] imageBytes) {
	        return Base64.getEncoder().encodeToString(imageBytes);
	    }
	
}
