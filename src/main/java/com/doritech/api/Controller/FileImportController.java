package com.doritech.api.Controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpSession;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.Entity.EmployeeMasterEntity;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Service.EmployeeMasterService;
import com.doritech.api.Service.FileImportService;
import com.google.common.net.HttpHeaders;

@Controller
public class FileImportController {

	@Autowired
	FileImportService fileImportService;
	@Autowired 
	EmployeeMasterService employeeMasterService;
	@Autowired
	EmployeeMasterRepository employeeMasterRepository;
	
	@PostMapping("/importEmployeeMasterCSV")
	public @ResponseBody String importEmployeeMasterCSV(@RequestParam("employeeMasterFile") MultipartFile file) throws Exception {
	   BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
	   return fileImportService.importEmployeeMasterCSV(br);
	}
	
	@PostMapping("/importEmployeeMaster")
	public @ResponseBody String importEmployeeMaster(@RequestParam("employeeMasterFile") MultipartFile file) throws Exception {
	    try {
	        Workbook workbook;
	        if (file.getOriginalFilename().endsWith(".xlsx")) {
	            workbook = new XSSFWorkbook(file.getInputStream());
	            if (workbook instanceof XSSFWorkbook) {
	                return fileImportService.importEmployeeMasterXSSF((XSSFWorkbook) workbook);
	            } else if (workbook instanceof HSSFWorkbook) {
	                return fileImportService.importEmployeeMasterHSSF((HSSFWorkbook) workbook);
	            }
	        } else if (file.getOriginalFilename().endsWith(".xls")) {
	            workbook = new HSSFWorkbook(file.getInputStream());
	            return fileImportService.importEmployeeMasterHSSF((HSSFWorkbook) workbook);
	        } else {	           
	            return "Unsupported file format";
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return "Import failed";
	    }
	    return "Import failed";
	}
	
	@RequestMapping("/downloadEmployeesData")
	public ResponseEntity<Resource> downloadEmployeesData() throws IOException {
		String fileName = "EmployeeDetails.xlsx";
		ByteArrayInputStream actualData = exportEmployeeMaster();
		InputStreamResource file = new InputStreamResource(actualData);
		
		ResponseEntity<Resource> bodyResource = ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename= "+fileName)
			.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
			.body(file);
		
		return bodyResource;
		
	}
	
	
	public static String[] HEADERS = {
			"User ID", "User Role", "User Name", "Pancard", "Address", "City", "Pincode", "State", "Country", "Mobile No.", "Email-ID", "Branch", "Group-ID"
	};
		
	public ByteArrayInputStream exportEmployeeMaster() {
		try {
			List<EmployeeMasterEntity> list = employeeMasterRepository.findAll();
			Workbook workbook = new XSSFWorkbook();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			Sheet sheet = workbook.createSheet("EmployeeDetails");
			
			// Header row
			Row row = sheet.createRow(0);
			
			for(int i=0; i<HEADERS.length; i++) {
				row.createCell(i);
				Cell cell = row.createCell(i);
				cell.setCellValue(HEADERS[i]);
			}
			
			// Value row
			int rowIndex = 1;
			for(EmployeeMasterEntity employee : list) {
				Row dataRow = sheet.createRow(rowIndex);
				rowIndex++;
				
				dataRow.createCell(0).setCellValue(employee.getEmpId());
				dataRow.createCell(1).setCellValue(employee.getEmpRole());
				dataRow.createCell(2).setCellValue(employee.getEmpName());
				//dataRow.createCell(3).setCellValue(employee.getEmpPan());
				dataRow.createCell(4).setCellValue(employee.getAddress());
				dataRow.createCell(5).setCellValue(employee.getCity());
				dataRow.createCell(6).setCellValue(employee.getPin());
				dataRow.createCell(7).setCellValue(employee.getState());
				dataRow.createCell(8).setCellValue(employee.getCountry());
				dataRow.createCell(9).setCellValue(employee.getMobile());
				dataRow.createCell(10).setCellValue(employee.getEmail());
				dataRow.createCell(11).setCellValue(employee.getBranch());
				dataRow.createCell(12).setCellValue(employee.getGroupId());
				
			}
			
			workbook.write(out);
			workbook.close();
			return new ByteArrayInputStream(out.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to export data.");
		}
		return null;
	}
	
	
	

	
	@PostMapping("/importBranchMasterCSV")
	public @ResponseBody String importBranchMasterCSV(@RequestParam("branchMasterFile") MultipartFile file) throws Exception {
	   BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
	   return fileImportService.importBranchMasterCSV(br);
	}
	
	@PostMapping("/importBranchMaster")
	public @ResponseBody String importBranchMaster(@RequestParam("branchMasterFile") MultipartFile file) throws Exception {
	    try {
	        Workbook workbook;
	        if (file.getOriginalFilename().endsWith(".xlsx")) {
	            workbook = new XSSFWorkbook(file.getInputStream());
	            if (workbook instanceof XSSFWorkbook) {
	                return fileImportService.importBranchMasterXSSF((XSSFWorkbook) workbook);
	            } else if (workbook instanceof HSSFWorkbook) {
	                return fileImportService.importBranchMasterHSSF((HSSFWorkbook) workbook);
	            }
	        } else if (file.getOriginalFilename().endsWith(".xls")) {
	            workbook = new HSSFWorkbook(file.getInputStream());
	            return fileImportService.importBranchMasterHSSF((HSSFWorkbook) workbook);
	        } else {	           
	            return "Unsupported file format";
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); 
	        return "Import failed";
	    }
	    return "Import failed";
	}
	
	  @PostMapping("/uploadBranchExcel")
	    public ResponseEntity<byte[]> processExcelFile (@RequestParam("file") MultipartFile file,HttpSession session) {
	        return fileImportService.processExcelFile(file,session);
	    }
}
