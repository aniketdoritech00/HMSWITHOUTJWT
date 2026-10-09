package com.doritech.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Service.ChatBotService;

@RestController
public class ChatBotController {
	
	@Autowired
	ChatBotService chatBotService;
	
	@GetMapping("getBranchSuggestions")
	public ResponseEntity getBranchSuggestions(@RequestParam String branchCode,@RequestParam String branchName) {
		return chatBotService.getBranchSuggestions(branchCode, branchName);
	}
	    

}
