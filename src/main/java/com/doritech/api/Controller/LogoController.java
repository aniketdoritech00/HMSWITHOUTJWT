package com.doritech.api.Controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.DTO.LogoDTO;
import com.doritech.api.Service.LogoService;

@Controller
@RequestMapping("/logos")
public class LogoController {

	@Autowired
	private LogoService logoService;

	@PostMapping(value = "/save")
	public ResponseEntity<Object> saveLogoImage(String branchCode, MultipartFile file) {
		return logoService.saveLogoImage(branchCode, file);
	}

	@GetMapping("/{branchCode}")
	public String getLogo(@PathVariable String branchCode, Model model) {
		if (branchCode != null && !branchCode.trim().isEmpty()) {
			Optional<LogoDTO> logoOptional = logoService.getImageById(branchCode);
			if (logoOptional.isPresent()) {
				model.addAttribute("logo", logoOptional.get());
				return "welcome";
			} else {
				model.addAttribute("errorMessage", "No logo found for the branch code: " + branchCode);
				return "error/404";
			}
		} else {
			model.addAttribute("errorMessage", "Branch code cannot be null or empty.");
			return "error/404";
		}
	}

	@GetMapping("/image/{id}")
	public ResponseEntity<Resource> getImage(@PathVariable Long id) {
		Optional<LogoDTO> logoOptional = logoService.getImageById(id);
		if (logoOptional.isPresent()) {
			LogoDTO logo = logoOptional.get();
			ByteArrayResource resource = new ByteArrayResource(logo.getImage());
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).contentLength(resource.contentLength())
					.body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping({ "/logo/{branchCode}" })
	public ResponseEntity<?> getLogo(@PathVariable String branchCode) {
		if (branchCode != null && !branchCode.trim().isEmpty()) {
			Optional<LogoDTO> logoOptional = this.logoService.getImageById(branchCode);
			if (logoOptional.isPresent()) {
				LogoDTO dto = (LogoDTO) logoOptional.get();
				String base64Image = Base64.getEncoder().encodeToString(dto.getImage());
				Map<String, Object> response = new HashMap<>();
				response.put("imageBase64", base64Image);
				response.put("id", dto.getId());
				response.put("branchCode", dto.getBranchCode());
				response.put("timeStamp", dto.getTimeStamp());
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("No logo found for the branch code: " + branchCode);
			}
		} else {
			return ResponseEntity.badRequest().body("Branch code cannot be null or empty.");
		}
	}

	@GetMapping("/getBranchBankDetails")
	@ResponseBody
	public com.doritech.api.Entity.ResponseEntity getBranchBankDetails(@RequestParam("branchCode") String branchCode) {
		return logoService.getBranchBankDetails(branchCode);
	}

}