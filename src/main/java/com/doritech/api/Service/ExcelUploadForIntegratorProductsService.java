//package com.doritech.api.Service;
//
//import com.doritech.api.Entity.ProductChecklistEntity;
//import com.doritech.api.Entity.VendorBranchMappedEntity;
//import com.doritech.api.Repository.VendorBranchRepository;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//import java.util.*;
//
//@Service
//public class ExcelUploadForIntegratorProductsService {
//
//    @Autowired
//    private VendorBranchRepository vendorBranchRepo;
//
//    public void saveExcelData(MultipartFile file) {
//        try {
//            List<VendorBranchMappedEntity> branchList = new ArrayList<>();
//
//            InputStream is = file.getInputStream();
//            Workbook workbook = new XSSFWorkbook(is);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            Iterator<Row> rows = sheet.iterator();
//            if (rows.hasNext()) rows.next(); // skip header
//
//            while (rows.hasNext()) {
//                Row row = rows.next();
//                VendorBranchMappedEntity branch = new VendorBranchMappedEntity();
//                ProductChecklistEntity product = new ProductChecklistEntity();
//
//                branch.setId(getIntegerCellValue(row.getCell(0)));
//                branch.setBranchCode(getStringCellValue(row.getCell(1)));
//                branch.setBranchName(getStringCellValue(row.getCell(2)));
//                branch.setBranchContact(getStringCellValue(row.getCell(3)));
//                branch.setSystemIntegrator(getStringCellValue(row.getCell(4)));
//                branch.set
//               // product.setId(getIntegerCellValue(row.getCell(5)));
//                product.setProduct(getStringCellValue(row.getCell(5)));
//                product.setMake(getStringCellValue(row.getCell(6)));
//                product.setModel(getStringCellValue(row.getCell(7)));
//                product.setDateOfInstallation(getStringCellValue(row.getCell(8)));
//
//                product.setVendorBranch(branch);
//                branch.setProduct(Collections.singletonList(product));
//
//                branchList.add(branch);
//            }
//
//            vendorBranchRepo.saveAll(branchList);
//            workbook.close();
//        } catch (Exception e) {
//            throw new RuntimeException("Fail to parse Excel file: " + e.getMessage(), e);
//        }
//    }
//
//    private Integer getIntegerCellValue(Cell cell) {
//        if (cell == null) return null;
//        switch (cell.getCellTypeEnum()) {
//            case NUMERIC:
//                return (int) cell.getNumericCellValue();
//            case STRING:
//                String value = cell.getStringCellValue().trim();
//                return value.isEmpty() ? null : Integer.parseInt(value);
//            default:
//                return null;
//        }
//    }
//
//    private String getStringCellValue(Cell cell) {
//        if (cell == null) {
//            return "";
//        }
//        switch (cell.getCellTypeEnum()) {
//            case STRING:
//                return cell.getStringCellValue().trim();
//            case NUMERIC:
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    return cell.getDateCellValue().toString();
//                } else {
//                    return String.valueOf((int) cell.getNumericCellValue());
//                }
//            case BOOLEAN:
//                return String.valueOf(cell.getBooleanCellValue());
//            case FORMULA:
//                return cell.getCellFormula();
//            case BLANK:
//                return "";
//            default:
//                return cell.toString();
//        }
//    }
//}
