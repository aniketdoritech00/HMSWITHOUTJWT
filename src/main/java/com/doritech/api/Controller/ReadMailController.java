package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.ReadMail;

@Controller
public class ReadMailController {

	@Autowired
	ReadMail readMail;

	@GetMapping("/Mail/check")
	@ResponseBody
	public ResponseEntity checkMail() {
		ResponseEntity response = new ResponseEntity();
		try {
			readMail.check();
			response.setMessage("Start Reading Successfully!! ");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error Occured!!.." + e.getLocalizedMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	@GetMapping("/getReadMailDetails")
	@ResponseBody
	public ResponseEntity getReadMailDetails() {
		return readMail.getReadMailDetails();
	}
}
