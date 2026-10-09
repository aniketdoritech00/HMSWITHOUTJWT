package com.doritech.api.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import org.springframework.http.HttpStatus;

@Service
public class ChatBotService {
	
	@Autowired
	BranchMasterService branchMasterService;
	@Autowired
	DeviceMasterService deviceMasterService;
	
	public ResponseEntity getBranchSuggestions(String branchCode, String branchName) {
	    ResponseEntity response = new ResponseEntity();
	    
	    
	    ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
	    List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
	    if (branchList.isEmpty()) {
	        response.setStatusCode(HttpStatus.NOT_FOUND.value());
	        response.setMessage("No branches found");
	        return response;
	    }

	    // Filter branches where branchDesc contains the input branchName (case-insensitive)
	    List<BranchMasterEntity> matchedBranches = branchList.stream()
	            .filter(branch -> branch.getBranchDesc() != null &&
	                              branch.getBranchDesc().toLowerCase().contains(branchName.toLowerCase()))
	            .collect(Collectors.toList());

	    if (matchedBranches.isEmpty()) {
	        response.setStatusCode(HttpStatus.NOT_FOUND.value());
	        response.setMessage("No matching branches found for: " + branchName);
	        return response;
	    }

	    response.setStatusCode(HttpStatus.OK.value());
	    response.setMessage("Matching branches found");
	    response.setPayload(matchedBranches);
	    return response;
	}


}
