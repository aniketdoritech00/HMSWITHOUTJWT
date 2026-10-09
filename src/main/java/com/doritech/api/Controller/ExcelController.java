package com.doritech.api.Controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.Service.ExcelService;
import com.doritech.api.Service.ParamService;

@RestController
public class ExcelController {

	@Autowired
	private ExcelService excelService;

	@Autowired
	private ParamService paramService;
	/* <==================Upload Excel File==================> */
	
	@PostMapping("/importExcel")
	public ResponseEntity<ByteArrayResource> importExcelFile(@RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException, EncryptedDocumentException, InvalidFormatException {

		if (file == null || file.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		return excelService.saveDataFromExcel(file, session);
	}

	/* <======Get all the data records from database to excel=======> */
	@GetMapping("/exportExcel")
	public ResponseEntity<InputStreamResource> downloadExcelFile() throws IOException {
		ClassPathResource excelFile = new ClassPathResource("DemoExcelFile/DemoExcelFile.xlsx");

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=DemoExcelFile.xlsx");
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

		return ResponseEntity.status(HttpStatus.OK).headers(headers).contentLength(excelFile.contentLength())
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.body(new InputStreamResource(excelFile.getInputStream()));
	}

	@GetMapping("getDeviceId")
	public String getDeviceId() {
		return paramService.getDeviceId();
	}

}
