package com.doritech.api.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Entity.SIACodesEntity;
import com.doritech.api.Repository.SIACodesRepository;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service
public class SIACodesService {
	
	@Autowired
	SIACodesRepository siaCodesRepository;
	
	public ResponseEntity getAllSiaCodes() {
		ResponseEntity response = new ResponseEntity();
		List<SIACodesEntity> siaCodesList = siaCodesRepository.findAll();
		
		if(siaCodesList.isEmpty()) {
			response.setMessage("No codes found");
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			return response;
		}
		
		response.setPayload(siaCodesList);
		response.setMessage("Data fetched succesfully");
		response.setStatusCode(HttpStatus.OK.value());
		
		return response;
	}

}
