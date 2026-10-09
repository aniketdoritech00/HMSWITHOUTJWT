package com.doritech.api.Controller;

//import com.doritech.api.Service.ExcelUploadForIntegratorProductsService;
import com.doritech.api.Service.ExcelUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
public class ExcelUploadController {

    @Autowired
    private ExcelUploadService uploadService;

//    @Autowired
//    private ExcelUploadForIntegratorProductsService excelService;

    
    @PostMapping("/uploadMasterData")
    public String uploadExcel(@RequestParam("file") MultipartFile file) {
        uploadService.saveExcelData(file);
        return "Excel uploaded successfully!";
    }
    
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadExcelforintegrator(@RequestParam("file") MultipartFile file) {
//        try {
//            if (file.isEmpty()) {
//                return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
//            }
//
//            excelService.saveExcelData(file);
//            return ResponseEntity.ok("Excel data uploaded and saved successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
//        }
//    }
}
