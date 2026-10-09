package com.doritech.api.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpSession;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.DynamicConfigurationEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.DynamicConfigurationRepository;

@Service
public class ExcelService {

	@Autowired
	private DeviceMasterRepository deviceMasterRepository;

	@Autowired
	private DeviceZoneDetailsRepository deviceZoneDetailsRepository;

	@Autowired
	private DynamicConfigurationRepository dynamicConfigurationRepository;

	@Autowired
	private ParamService paramService;

	/************************************************************************/
	/* Save Data From Excel To Database - @Author = Aniket Mishra */
	/************************************************************************/
	public ResponseEntity<ByteArrayResource> saveDataFromExcel(MultipartFile file, HttpSession session)
			throws IOException, EncryptedDocumentException, InvalidFormatException {

		List<String[]> failedData = new ArrayList<>();
		List<String[]> successfulData = new ArrayList<>();
		List<String> columnHeaders = new ArrayList<>();
		Map<String, Integer> headerMap = new LinkedHashMap<>();

		try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rows = sheet.iterator();

			List<DeviceMasterEntity> devices = new ArrayList<>();
			List<DeviceZoneDetailsEntity> deviceZones = new ArrayList<>();
			Set<String> deviceIds = new HashSet<>();
			Set<String> deviceSerialNos = new HashSet<>();
			Set<String> zoneIds = new HashSet<>();

			Long maxId = dynamicConfigurationRepository.findMaxId();
			AtomicLong dynamicIdCounter = new AtomicLong(maxId != null ? maxId + 1 : 1);

			if (rows.hasNext()) {
				Row headerRow = rows.next();
				for (Cell cell : headerRow) {
					String originalHeader = cell.getStringCellValue().trim();
					String normalized = normalizeHeader(originalHeader);
					headerMap.put(normalized, cell.getColumnIndex());
					columnHeaders.add(originalHeader);
				}
			}

			List<String> requiredHeaders = List.of("device name", "device serialno", "device type", "device location",
					"phone no", "protection type", "user no", "user password", "email", "address", "support no",
					"branch code", "vendor name", "vendor email", "passcode", "product type", "vendor mobile", "status",
					"id", "zone original name", "zone desired name", "alert type", "inputtype", "primary flag",
					"message");

			for (String header : requiredHeaders) {
				if (!headerMap.containsKey(header)) {
					throw new IllegalArgumentException("Missing required header: " + header);
				}
			}

			int totalColumns = columnHeaders.size();

			while (rows.hasNext()) {
				Row row = rows.next();
				String[] rowData = new String[totalColumns + 1];

				for (int i = 0; i < columnHeaders.size(); i++) {
					rowData[i] = getStringCellValue(row.getCell(i));
				}

				String deviceId = paramService.getDeviceId();
				ParamEntity paramRequest = new ParamEntity();
				paramRequest.setCode("DEVICE_ID");
				paramService.updateDeviceDescp2Value(paramRequest);

				DeviceMasterEntity device = new DeviceMasterEntity();
				device.setDeviceId(deviceId);
				String serialNo = getStringCellValue(row.getCell(headerMap.get("device serialno")));

				if (deviceMasterRepository.existsByDeviceSerialNo(serialNo)) {
					rowData[totalColumns] = "Serial number already exists in database";
					failedData.add(rowData);
					continue;
				}

				device.setDeviceName(getStringCellValue(row.getCell(headerMap.get("device name"))));
				device.setDeviceSerialNo(serialNo);
				device.setDeviceType(getStringCellValue(row.getCell(headerMap.get("device type"))));
				device.setDeviceLocation(getStringCellValue(row.getCell(headerMap.get("device location"))));
				device.setPhoneNumber(getStringCellValue(row.getCell(headerMap.get("phone no"))));
				String protectionType = getStringCellValue(row.getCell(headerMap.get("protection type")));
				device.setProtectionType(protectionType);
				device.setUserNo(getStringCellValue(row.getCell(headerMap.get("user no"))));
				device.setUserPassword(getStringCellValue(row.getCell(headerMap.get("user password"))));
				device.setEmail1(getStringCellValue(row.getCell(headerMap.get("email"))));
				device.setAddress(getStringCellValue(row.getCell(headerMap.get("address"))));
				device.setSupportNo(getStringCellValue(row.getCell(headerMap.get("support no"))));
				device.setBranchCode(getStringCellValue(row.getCell(headerMap.get("branch code"))));
				device.setVendorName(getStringCellValue(row.getCell(headerMap.get("vendor name"))));
				device.setVendorEmail(getStringCellValue(row.getCell(headerMap.get("vendor email"))));
				device.setPasscode(getStringCellValue(row.getCell(headerMap.get("passcode"))));
				device.setProductType(getStringCellValue(row.getCell(headerMap.get("product type"))));
				device.setVendorMobile(getStringCellValue(row.getCell(headerMap.get("vendor mobile"))));
				device.setStatus(getStringCellValue(row.getCell(headerMap.get("status"))));
				device.setCreatedDate(new Date().toString());
				device.setCreatedBy(String.valueOf(session.getAttribute("empId")));

				DeviceZoneDetailsEntity deviceZone = new DeviceZoneDetailsEntity();
				String zoneId = getStringCellValue(row.getCell(headerMap.get("id")));
				deviceZone.setId(zoneId);
				deviceZone.setDeviceId(deviceId);
				deviceZone.setZoneOriginalName(getStringCellValue(row.getCell(headerMap.get("zone original name"))));
				deviceZone.setZoneDesiredName(getStringCellValue(row.getCell(headerMap.get("zone desired name"))));
				String alertType = getStringCellValue(row.getCell(headerMap.get("alert type")));
				deviceZone.setAlertType(alertType);
				deviceZone.setInputType(getStringCellValue(row.getCell(headerMap.get("inputtype"))));
				deviceZone.setPrimaryFlag(getStringCellValue(row.getCell(headerMap.get("primary flag"))));
				String message = getStringCellValue(row.getCell(headerMap.get("message")));

				if ("O".equalsIgnoreCase(protectionType)) {
					DynamicConfigurationEntity dynamicEntity = new DynamicConfigurationEntity();
					dynamicEntity.setId(dynamicIdCounter.getAndIncrement());
					dynamicEntity.setAlertType(alertType);
					dynamicEntity.setDeviceId(deviceId);
					dynamicEntity.setMessage(message);
					dynamicEntity.setZone(deviceZone.getZoneOriginalName());
					dynamicEntity.setDeviceZoneId(zoneId);
					dynamicConfigurationRepository.save(dynamicEntity);
				}

				logDeviceDetails(row.getRowNum(), device, deviceZone);

				if (deviceIds.contains(deviceId) || deviceSerialNos.contains(serialNo) || zoneIds.contains(zoneId)) {
					rowData[totalColumns] = "Duplicate in Excel: deviceId, serialNo or zoneId";
					failedData.add(rowData);
				} else {
					deviceIds.add(deviceId);
					deviceSerialNos.add(serialNo);
					zoneIds.add(zoneId);
					devices.add(device);
					deviceZones.add(deviceZone);
					rowData[totalColumns] = "Success";
					successfulData.add(rowData);
				}
			}

			deviceMasterRepository.saveAll(devices);
			deviceZoneDetailsRepository.saveAll(deviceZones);
		}

		List<String[]> allRows = new ArrayList<>();
		allRows.addAll(successfulData);
		allRows.addAll(failedData);

		if (!allRows.isEmpty()) {
			Workbook outputWorkbook = new XSSFWorkbook();
			Sheet outputSheet = outputWorkbook.createSheet("Import Result");

			int rowNum = 0;
			Row header = outputSheet.createRow(rowNum++);
			for (int i = 0; i < columnHeaders.size(); i++) {
				header.createCell(i).setCellValue(columnHeaders.get(i));
			}
			header.createCell(columnHeaders.size()).setCellValue("Result");

			for (String[] outputRow : allRows) {
				Row row = outputSheet.createRow(rowNum++);
				for (int i = 0; i < outputRow.length; i++) {
					row.createCell(i).setCellValue(outputRow[i] != null ? outputRow[i] : "");
				}
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			outputWorkbook.write(out);
			outputWorkbook.close();

			ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_result.xlsx");

			return ResponseEntity.ok().headers(headers).contentLength(resource.contentLength())
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(resource);
		}

		return ResponseEntity.noContent().build();
	}

	private String normalizeHeader(String header) {
		return header.trim().toLowerCase().replaceAll("\\s+", " ");
	}

	/************************************************************************/
	/* Convert numeric cell value to String if any- @Author = Aniket Mishra */
	/************************************************************************/
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

	/************************************************************************/
	/* Logging device details to console - @Author = Aniket */
	/************************************************************************/
	private void logDeviceDetails(int rowNum, DeviceMasterEntity device, DeviceZoneDetailsEntity deviceZone) {
		System.out.println("Row " + (rowNum + 1) + ": " + deviceToString(device) + deviceZoneToString(deviceZone));
	}

	/************************************************************************/
	/* Formatting device details to string representation - @Author = Aniket */
	/************************************************************************/
	private String deviceToString(DeviceMasterEntity device) {
		return "DeviceMasterDetails>>>DeviceId=" + device.getDeviceId() + ", DeviceName=" + device.getDeviceName()
				+ ", DeviceSerialNo=" + device.getDeviceSerialNo() + ", DeviceType=" + device.getDeviceType()
				+ ", DeviceLocation=" + device.getDeviceLocation() + ", PhoneNumber=" + device.getPhoneNumber()
				+ ", ProtectionType=" + device.getProtectionType() + ", UserNo=" + device.getUserNo()
				+ ", UserPassword=" + device.getUserPassword() + ", Email1=" + device.getEmail1() + ", Email2="
				+ device.getProductType() + ", Email3=" + device.getPasscode() + ", Address=" + device.getAddress()
				+ ", SupportNo=" + device.getSupportNo() + ", BranchCode=" + device.getBranchCode() + ", VendorName="
				+ device.getVendorName() + ", VendorEmail=" + device.getVendorEmail();
	}

	/************************************************************************/
	/* Formatting DeviceZone details to string representation - @Author = Aniket */
	/************************************************************************/
	private String deviceZoneToString(DeviceZoneDetailsEntity deviceZone) {
		String id = deviceZone.getId();
		return ",&DeviceZoneDetails>>>Id=" + id + ", DeviceId=" + deviceZone.getDeviceId() + ", ZoneOriginalName="
				+ deviceZone.getZoneOriginalName() + ", ZoneDesiredName=" + deviceZone.getZoneDesiredName()
				+ ", AlertType=" + deviceZone.getAlertType() + ", InputType=" + deviceZone.getInputType()
				+ ", PrimaryFlag=" + deviceZone.getPrimaryFlag();
	}

	/************************************************************************/
	/* Export Data From Database To Excel - @Author = Your Name */
	/************************************************************************/

//    public void getAllRecordsFromDatabaseToExcel(HttpServletResponse response) throws IOException {
//        List<DeviceMasterEntity> devices = deviceMasterRepository.findAll();
//
//        try (Workbook workbook = new XSSFWorkbook()) {
//            Sheet sheet = workbook.createSheet("Device Master Data");
//
//            // <==================>Freeze the header row<================>
//            sheet.createFreezePane(0, 1);
//
//           // <=======================================>Create header row<=====================================>
//            String[] headers = {
//                "Device ID", "Device Name", "Device Serial No", "Device Type", "Device Location", 
//                "Phone Number", "Protection Type", "User No", "User Password", "Email1", "Email2", 
//                "Email3", "Address", "Support No", "Branch Code", "Vendor Name", "Vendor Email", 
//                "id","Zone Original Name", "Zone Desired Name", "AlertType", "InputType"
//            };
//
//            Row headerRow = sheet.createRow(0);
//
//            // <=============>Define cell style for header<===============>            
//            CellStyle headerStyle = workbook.createCellStyle();
//            headerStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
//            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            headerStyle.setAlignment(HorizontalAlignment.CENTER);
//            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//            Font headerFont = workbook.createFont();
//            headerFont.setBold(true);
//            headerStyle.setFont(headerFont);
//
//            // <========>Create header cells with style<========>
//            for (int i = 0; i < headers.length; i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers[i]);
//                cell.setCellStyle(headerStyle);
//            }
//
//           // <========>Define cell style for data<========>
//            CellStyle dataStyle = workbook.createCellStyle();
//            dataStyle.setAlignment(HorizontalAlignment.CENTER);
//            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//
//           // <========>Populate data rows<========>
//            int rowNum = 1;
//            for (DeviceMasterEntity device : devices) {
//            	// <==============>Fetch only the relevant device zone details for the current device<======================>
//                List<DeviceZoneDetailsEntity> deviceZones = deviceZoneDetailsRepository.findByDeviceId(device.getDeviceId());
//                for (DeviceZoneDetailsEntity deviceZone : deviceZones) {
//                    Row row = sheet.createRow(rowNum++);
//                    row.createCell(0).setCellValue(device.getDeviceId());
//                    row.createCell(1).setCellValue(device.getDeviceName());
//                    row.createCell(2).setCellValue(device.getDeviceSerialNo());
//                    row.createCell(3).setCellValue(device.getDeviceType());
//                    row.createCell(4).setCellValue(device.getDeviceLocation());
//                    row.createCell(5).setCellValue(device.getPhoneNumber());
//                    row.createCell(6).setCellValue(device.getProtectionType());
//                    row.createCell(7).setCellValue(device.getUserNo());
//                    row.createCell(8).setCellValue(device.getUserPassword());
//                    row.createCell(9).setCellValue(device.getEmail1());
//                    row.createCell(10).setCellValue(device.getEmail2());
//                    row.createCell(11).setCellValue(device.getEmail3());
//                    row.createCell(12).setCellValue(device.getAddress());
//                    row.createCell(13).setCellValue(device.getSupportNo());
//                    row.createCell(14).setCellValue(device.getBranchCode());
//                    row.createCell(15).setCellValue(device.getVendorName());
//                    row.createCell(16).setCellValue(device.getVendorEmail());
//                     
//                    row.createCell(17).setCellValue(deviceZone.getId());
////                    row.createCell(18).setCellValue(deviceZone.getDeviceId());
//                    row.createCell(18).setCellValue(deviceZone.getZoneOriginalName());
//                    row.createCell(19).setCellValue(deviceZone.getZoneDesiredName());
//                    row.createCell(20).setCellValue(deviceZone.getAlertType());
//                    row.createCell(21).setCellValue(deviceZone.getInputType());
//
//                    // <===>Apply style to data cells <=====>
//                    for (int i = 0; i < headers.length; i++) {
//                        row.getCell(i).setCellStyle(dataStyle);
//                    }
//                }
//            }
//
//            // <======>Auto-size all columns<========>
//            for (int i = 0; i < headers.length; i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//           // <========================>Set response headers<=======================================>
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.setHeader("Content-Disposition", "attachment; filename=DeviceMasterData.xlsx");
//
//            try (ServletOutputStream outputStream = response.getOutputStream()) {
//                workbook.write(outputStream);
//            }
//        }
//    }

}
