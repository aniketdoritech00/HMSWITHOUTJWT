package com.doritech.api.Service;

import com.doritech.api.Entity.BOIMasterDataEntity;
import com.doritech.api.Repository.BOIMasterDataRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelUploadService {

    @Autowired
    private BOIMasterDataRepository repository;

    public void saveExcelData(MultipartFile file) {
        try {
            List<BOIMasterDataEntity> dataList = new ArrayList<>();

            InputStream is = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); 

            while (rows.hasNext()) {
                Row row = rows.next();
                BOIMasterDataEntity entity = new BOIMasterDataEntity();

                entity.setBranchCode(getStringCellValue(row.getCell(0)));
                entity.setBranchName(getStringCellValue(row.getCell(1)));
                entity.setCategory(getStringCellValue(row.getCell(2)));
                entity.setState(getStringCellValue(row.getCell(3)));
                entity.setZoneName(getStringCellValue(row.getCell(4)));
                entity.setFgmo(getStringCellValue(row.getCell(5)));
                entity.setDistrict(getStringCellValue(row.getCell(6)));
                entity.setCity(getStringCellValue(row.getCell(7)));
                entity.setAddress(getStringCellValue(row.getCell(8)));
                entity.setPincode(getStringCellValue(row.getCell(9)));
                entity.setLatitude(getFloatCellValue(row.getCell(10)));
                entity.setLongitude(getFloatCellValue(row.getCell(11)));
                entity.setBankName(getStringCellValue(row.getCell(12)));
                dataList.add(entity);
            }

            repository.saveAll(dataList);
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse Excel file: " + e.getMessage());
        }
    }
    private Float getFloatCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            switch (cell.getCellTypeEnum()) {
                case NUMERIC:
                    return (float) cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    return value.isEmpty() ? null : Float.parseFloat(value);
                default:
                    return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid float value in cell: " + cell.toString());
        }
    }

    private String getStringCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellTypeEnum()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				return String.valueOf((int) cell.getNumericCellValue());
			}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return "";
		default:
			return cell.toString();
		}
	}
}
