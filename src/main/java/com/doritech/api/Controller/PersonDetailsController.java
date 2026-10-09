package com.doritech.api.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.doritech.api.DTO.PersonDetailsDTO;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.MailService;
import com.doritech.api.Service.PersonDetailsService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@RestController
public class PersonDetailsController {

	@Autowired
	PersonDetailsService personDetailsService;
	@Autowired
	MailService mailService;
	
	@PostMapping("savePersonDetails")
	public ResponseEntity savePersonDetails(@ModelAttribute PersonDetailsDTO personDetailsRequest ,   @RequestParam(value = "files") List<MultipartFile> files ){
	return personDetailsService.savePersonDetails(personDetailsRequest ,files);	
	}
	
	@GetMapping("getAllDetailsByBranchCode")
	public ResponseEntity getAllDetailsByBranchCode(@RequestParam String branchCode) {
		return personDetailsService.getAllDetailsByBranchCode(branchCode);
	}

	@PostMapping("updatePersonDetailsBatch")
	public ResponseEntity updatePersonDetails(@RequestBody List<PersonDetailsDTO> personDetailsRequest){
		return personDetailsService.updatePersonDetailsBatch(personDetailsRequest);
	}

//	@PostMapping("sendComplaintMail")
//	public String sendComplaintMail(@RequestBody PersonDetailsDTO personDetailsRequest) throws Exception {
//		return mailService.sendComplaintMail(personDetailsRequest);
//	}
//		
//		
	
	 @PostMapping("sendComplaintMail")
	    public String sendComplaintMail(MultipartHttpServletRequest request) {
	        	try {
					   mailService.sendComplaintMail(request);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        return "done";
	    }
}
