package com.doritech.api.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.api.DTO.AlarmAuditLogDTO;
import com.doritech.api.Entity.AlarmAuditLogEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.DeviceZoneDetailsEntity;
import com.doritech.api.Entity.ResponseEntity;
import com.doritech.api.Repository.AlarmAuditLogRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.DeviceZoneDetailsRepository;
import com.doritech.api.Repository.EmployeeMasterRepository;

@Service
public class AlarmAuditLogService {
	@Autowired
	AlarmAuditLogRepository alarmRepo;
	@Autowired
	EmployeeMasterRepository employeeMasterRepository;
	@Autowired
	BranchMasterRepository branchRepo;
	@Autowired
	BranchMasterService branchMasterService;
	@Autowired
	DeviceMasterRepository deviceMasterRepository;
	@Autowired
	DeviceZoneDetailsRepository deviceZoneDetailsRepository;
	@Autowired
	BranchMasterRepository branchMasterRepository;

	public ResponseEntity saveAudit(AlarmAuditLogDTO alarmAuditRequest) {
		ResponseEntity response = new ResponseEntity();
		try {
			AlarmAuditLogEntity alarmEntity = new AlarmAuditLogEntity();
			BeanUtils.copyProperties(alarmEntity, alarmAuditRequest);
			LocalDateTime now = LocalDateTime.now();
			String dateTimeString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Format as desired

			alarmEntity.setDateTime(dateTimeString);
			alarmEntity.setAlertDateTime(alarmAuditRequest.getAlertDateTime());
			// alarmEntity.setDateTime(LocalDate.now());
			alarmRepo.save(alarmEntity);
			response.setPayload(alarmEntity);
			response.setMessage("Data saved successfully");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			response.setMessage("Error occured while saving data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllAlarmAuditLogs(String branchCode) throws Exception {
		ResponseEntity response = new ResponseEntity();
		try {
			List<AlarmAuditLogDTO> responseList = new LinkedList<>();
			ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
			if (responseEntity != null) {
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

				branchList.forEach(branchMaster -> {
					List<AlarmAuditLogEntity> alarmAuditList = alarmRepo.findByBranchCode(branchMaster.getBranchCode());
					;

					for (AlarmAuditLogEntity alarm : alarmAuditList) {
						AlarmAuditLogDTO alarmAuditLogDTO = new AlarmAuditLogDTO();
						try {
							BeanUtils.copyProperties(alarmAuditLogDTO, alarm);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						alarmAuditLogDTO.setUserName(employeeMasterRepository.getById(alarm.getUser()).getEmpName());

						BranchMasterEntity branch = branchRepo.findBybranchCode(alarm.getBranchCode());
						if (branch != null) {
							alarmAuditLogDTO.setBranchCode(branch.getBranchDesc());
						}

						responseList.add(alarmAuditLogDTO);
					}

					Collections.sort(responseList, Comparator.comparing(AlarmAuditLogDTO::getDateTime).reversed());

				});
			}

			response.setPayload(responseList);
			response.setMessage("Data fetched successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}

		return response;
	}

	public ResponseEntity getStatusCountofOneDay() {
		ResponseEntity response = new ResponseEntity();
		try {
			// Assuming these methods return the count of records for each status
			int acknowledgedCount = alarmRepo.getByRemarksCount("ACKNOWLEDGED");
			int testingCount = alarmRepo.getByRemarksCount("TESTING");
			int falseAlarmCount = alarmRepo.getByRemarksCount("FALSE ALARM");
			int securityVendorInformedCount = alarmRepo.getByRemarksCount("SECURITY VENDOR INFORMED");

			Map<String, Integer> responseList = new HashMap<>();
			responseList.put("ACKNOWLEDGED", acknowledgedCount);
			responseList.put("SECURITY_VENDOR_INFORMED", securityVendorInformedCount);
			responseList.put("TESTING", testingCount);
			responseList.put("FALSE_ALARM", falseAlarmCount);

			response.setPayload(responseList);
			response.setMessage("Count fetched successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getStatusCountForAll() {
		ResponseEntity response = new ResponseEntity();
		try {
			// Assuming these methods return the count of records for each status
			int acknowledgedCount = alarmRepo.getAllLatestAuditCount("ACKNOWLEDGED");
			int testingCount = alarmRepo.getAllLatestAuditCount("TESTING");
			int falseAlarmCount = alarmRepo.getAllLatestAuditCount("FALSE ALARM");
			int securityVendorInformedCount = alarmRepo.getAllLatestAuditCount("SECURITY VENDOR INFORMED");

			Map<String, Integer> responseList = new HashMap<>();
			responseList.put("ACKNOWLEDGED", acknowledgedCount);
			responseList.put("SECURITY_VENDOR_INFORMED", securityVendorInformedCount);
			responseList.put("TESTING", testingCount);
			responseList.put("FALSE_ALARM", falseAlarmCount);

			response.setPayload(responseList);
			response.setMessage("Count fetched successfully.");
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Error fetching data.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getStatusCountByDate(String dateTime) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<Object[]> statusCounts = alarmRepo.getStatusCountByDate(dateTime);
			List<Object> result = new ArrayList<>();
			for (Object[] row : statusCounts) {
				String remarks = (String) row[0];
				int count = ((Number) row[1]).intValue();
				Map<String, Object> item = new HashMap<>();
				item.put("remarks", remarks);
				item.put("count", count);
				result.add(item);
			}
			response.setPayload(result);
			response.setMessage("Status count fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getStatusCountByMonth(String month, String year) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<Object[]> statusCounts = alarmRepo.getStatusCountByMonth(month, year);
			List<Object> result = new ArrayList<>();
			for (Object[] row : statusCounts) {
				String remarks = (String) row[0];
				int count = ((Number) row[1]).intValue();
				Map<String, Object> item = new HashMap<>();
				item.put("remarks", remarks);
				item.put("count", count);
				result.add(item);
			}
			response.setPayload(result);
			response.setMessage("Status count fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getStatusCountByYear(String year) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<Object[]> statusCounts = alarmRepo.getStatusCountByYear(year);
			List<Object> result = new ArrayList<>();
			for (Object[] row : statusCounts) {
				String remarks = (String) row[0];
				int count = ((Number) row[1]).intValue();
				Map<String, Object> item = new HashMap<>();
				item.put("remarks", remarks);
				item.put("count", count);
				result.add(item);
			}
			response.setPayload(result);
			response.setMessage("Status count fetched successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			response.setMessage("Error fetching data");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllRecordsByDate(String dateTime) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<AlarmAuditLogEntity> list = alarmRepo.getAllRecordsByDate(dateTime);
			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setPayload(list);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Data not found");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllRecordsByMonth(String month, String year) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<AlarmAuditLogEntity> list = alarmRepo.getAllRecordsByMonth(month, year);
			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setPayload(list);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Data not found");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllRecordsByYear(String year) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<AlarmAuditLogEntity> list = alarmRepo.getAllRecordsByYear(year);
			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setPayload(list);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Data not found");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

//	public ResponseEntity getStatusCountByActiveZones(String activeZone) {
//		ResponseEntity response = new ResponseEntity();
//		try {
//			List<Object[]> statusCounts = alarmRepo.getStatusCountByActiveZones(activeZone);
//			List<Object> result = new ArrayList<>();
//			for (Object[] row : statusCounts) {
//				String remarks = (String) row[0];
//				int count = ((Number) row[1]).intValue();
//				Map<String, Object> item = new HashMap<>();
//				item.put("remarks", remarks);
//				item.put("count", count);
//				result.add(item);
//			}
//			response.setPayload(result);
//			response.setMessage("Status count fetched successfully");
//			response.setStatusCode(HttpStatus.OK.value());
//		} catch (Exception e) {
//			response.setMessage("Error fetching data");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		}
//		return response;
//	}
//
//	public ResponseEntity getAllRecordsByActiveZone(String activeZone) {
//		ResponseEntity response = new ResponseEntity();
//		try {
//			List<AlarmAuditLogEntity> list = alarmRepo.getAllRecordsByActiveZone(activeZone);
//			if(list.isEmpty()) {
//				response.setMessage("Data not found");
//				response.setStatusCode(HttpStatus.NOT_FOUND.value());
//			}else {
//				response.setPayload(list);
//				response.setMessage("Data fetched successfully");
//				response.setStatusCode(HttpStatus.OK.value());
//			}
//		}catch (Exception e) {
//			response.setMessage("Data not found");
//			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//		}
//		return response;
//	}
//	
	public ResponseEntity getLatestAuditRecord() {
		ResponseEntity response = new ResponseEntity();
		try {
			List<AlarmAuditLogEntity> list = alarmRepo.findAllLatestRecords();
			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				response.setPayload(list);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Data not found");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	

	public ResponseEntity getStatusCountByDateRange(String startDate, String endDate, String branchCode) {
	    ResponseEntity response = new ResponseEntity();
	    try {
	        // Reuse the first API to get all records
	        ResponseEntity allRecordsResponse = getAllRecordsByDateRange(startDate, endDate, branchCode);

	        if (allRecordsResponse.getPayload() == null || 
	            !(allRecordsResponse.getPayload() instanceof List)) {
	            response.setMessage("No records found");
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            return response;
	        }

	        List<AlarmAuditLogDTO> allRecords = (List<AlarmAuditLogDTO>) allRecordsResponse.getPayload();

	        if (allRecords.isEmpty()) {
	            response.setMessage("No records found");
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            return response;
	        }

	        // Count by remarks (or status field you want)
	        Map<String, Long> statusCountMap = allRecords.stream()
	                .collect(Collectors.groupingBy(
	                        AlarmAuditLogDTO::getRemarks, // change to the correct field
	                        Collectors.counting()
	                ));

	        // Format the response list
	        List<Map<String, Object>> result = new ArrayList<>();
	        statusCountMap.forEach((remarks, count) -> {
	            Map<String, Object> item = new HashMap<>();
	            item.put("remarks", remarks);
	            item.put("count", count.intValue());
	            result.add(item);
	        });

	        response.setPayload(result);
	        response.setMessage("Status count fetched successfully");
	        response.setStatusCode(HttpStatus.OK.value());

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.setMessage("Error fetching data");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}


	/*
	 * public ResponseEntity getAllRecordsByDateRange(String startDate, String
	 * endDate) { ResponseEntity response = new ResponseEntity(); try {
	 * List<AlarmAuditLogEntity> list =
	 * alarmRepo.getAllRecordsByDateRange(startDate, endDate); if (list.isEmpty()) {
	 * response.setMessage("Data not found");
	 * response.setStatusCode(HttpStatus.NOT_FOUND.value()); } else {
	 * response.setPayload(list); response.setMessage("Data fetched successfully");
	 * response.setStatusCode(HttpStatus.OK.value()); } } catch (Exception e) {
	 * response.setMessage("Data not found");
	 * response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()); } return
	 * response; }
	 */

	public ResponseEntity getAllRecordsByDateRange(String startDate, String endDate,String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			
			List<String> branchCodeList=new ArrayList<>();
			ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
			if (responseEntity != null) {
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

				branchList.forEach(x->{
					branchCodeList.add(x.getBranchCode());
				});
			
			}
			
			
			
			List<AlarmAuditLogEntity> list = alarmRepo.getAllRecordsByDateRange(startDate, endDate,branchCodeList);
			List<AlarmAuditLogDTO> responseList = new ArrayList<>();
			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {

				list.forEach(alarmLog -> {
					AlarmAuditLogDTO alarmResponse = new AlarmAuditLogDTO();
					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
							.findBybranchCode(alarmLog.getBranchCode());

					if (deviceMasterEntity != null) {
						DeviceZoneDetailsEntity deviceZoneDetailsEntity = deviceZoneDetailsRepository
								.findByDeviceIdAndAlertType(deviceMasterEntity.getDeviceId(), alarmLog.getActiveZone());

						try {
							BeanUtils.copyProperties(alarmResponse, alarmLog);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (deviceZoneDetailsEntity != null) {

							alarmResponse.setAlarmedDevice(deviceZoneDetailsEntity.getZoneDesiredName());
							alarmResponse.setVendorName(deviceZoneDetailsEntity.getVendors());
						}
						
						 BranchMasterEntity branchEntity = branchMasterRepository.findBybranchCode(alarmLog.getBranchCode());
						 
						 BranchMasterEntity parentBranchEntity =
			        		        (branchEntity.getParentCode() != null)
			        		                ? branchMasterRepository.findBybranchCode(branchEntity.getParentCode())
			        		                : null;

			        		if (branchEntity != null && parentBranchEntity != null && parentBranchEntity.getBranchDesc() != null) {
			        			alarmResponse.setDeviceId(branchEntity.getBranchDesc()+" - "+parentBranchEntity.getBranchDesc());
			        		}else {
			        			alarmResponse.setDeviceId(branchEntity.getBranchDesc());
			        		}
						 
//						    if (branchEntity != null) {
//						        alarmResponse.setDeviceId(branchEntity.getBranchDesc());
//						    } else {
//						        alarmResponse.setDeviceId("Unknown Branch");
//						    }

						responseList.add(alarmResponse);

					} else {
						response.setMessage("No device found by branch code" + alarmLog.getBranchCode());
						response.setStatusCode(HttpStatus.OK.value());
					}

				});
				
				responseList.sort((a, b) -> b.getDateTime().compareTo(a.getDateTime()));

				response.setPayload(responseList);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Data not found");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getStatusCountByActiveZones(String activeZone) {
		ResponseEntity response = new ResponseEntity();
		try {
			List<AlarmAuditLogEntity> list = alarmRepo.findAll();
			List<AlarmAuditLogDTO> responseList = new ArrayList<>();
			List<Map<String, Object>> countsList = new ArrayList<>();

			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				for (AlarmAuditLogEntity alarmLog : list) {
					AlarmAuditLogDTO alarmResponse = new AlarmAuditLogDTO();
					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
							.findBybranchCode(alarmLog.getBranchCode());

					if (deviceMasterEntity != null) {
						DeviceZoneDetailsEntity deviceZoneDetailsEntity = deviceZoneDetailsRepository
								.findByDeviceIdAndAlertType(deviceMasterEntity.getDeviceId(), alarmLog.getActiveZone());

						BeanUtils.copyProperties(alarmResponse, alarmLog);

						if (deviceZoneDetailsEntity != null) {
							alarmResponse.setAlarmedDevice(deviceZoneDetailsEntity.getZoneDesiredName());
						}

						responseList.add(alarmResponse);
					} else {
						response.setMessage("No device found by branch code " + alarmLog.getBranchCode());
						response.setStatusCode(HttpStatus.OK.value());
					}
				}

				int acknowledged = 0, falseAlarm = 0, testing = 0, securityVendorInformed = 0;

				for (AlarmAuditLogDTO item : responseList) {
					if (activeZone.equals(item.getAlarmedDevice())) {
						switch (item.getRemarks()) {
						case "ACKNOWLEDGED":
							acknowledged++;
							break;
						case "SECURITY VENDOR INFORMED":
							securityVendorInformed++;
							break;
						case "FALSE ALARM":
							falseAlarm++;
							break;
						case "TESTING":
							testing++;
							break;
						}
					}
				}

				Map<String, Object> acknowledgedMap = new HashMap<>();
				acknowledgedMap.put("remarks", "ACKNOWLEDGED");
				acknowledgedMap.put("count", acknowledged);
				countsList.add(acknowledgedMap);

				Map<String, Object> securityVendorInformedMap = new HashMap<>();
				securityVendorInformedMap.put("remarks", "SECURITY VENDOR INFORMED");
				securityVendorInformedMap.put("count", securityVendorInformed);
				countsList.add(securityVendorInformedMap);

				Map<String, Object> falseAlarmMap = new HashMap<>();
				falseAlarmMap.put("remarks", "FALSE ALARM");
				falseAlarmMap.put("count", falseAlarm);
				countsList.add(falseAlarmMap);

				Map<String, Object> testingMap = new HashMap<>();
				testingMap.put("remarks", "TESTING");
				testingMap.put("count", testing);
				countsList.add(testingMap);

				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
				response.setPayload(countsList);
			}
		} catch (Exception e) {
			response.setMessage("Data not found");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			e.printStackTrace();
		}
		return response;
	}

	public ResponseEntity getAllRecordsByActiveZone(String activeZone) {
		
		ResponseEntity response = new ResponseEntity();
		try {
			
//			List<String> branchCodeList=new ArrayList<>();
//			ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
//			if (responseEntity != null) {
//				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
//
//				branchList.forEach(x->{
//					branchCodeList.add(x.getBranchCode());
//				});
//			
//			}
			
			
			
			List<AlarmAuditLogEntity> list = alarmRepo.findAll();
			List<AlarmAuditLogDTO> responseList = new ArrayList<>();

			if (list.isEmpty()) {
				response.setMessage("Data not found");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {

				list.forEach(alarmLog -> {
					AlarmAuditLogDTO alarmResponse = new AlarmAuditLogDTO();
					DeviceMasterEntity deviceMasterEntity = deviceMasterRepository
							.findBybranchCode(alarmLog.getBranchCode());

					if (deviceMasterEntity != null) {
						DeviceZoneDetailsEntity deviceZoneDetailsEntity = deviceZoneDetailsRepository
								.findByDeviceIdAndAlertType(deviceMasterEntity.getDeviceId(), alarmLog.getActiveZone());

						try {
							BeanUtils.copyProperties(alarmResponse, alarmLog);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (deviceZoneDetailsEntity != null) {

							alarmResponse.setAlarmedDevice(deviceZoneDetailsEntity.getZoneDesiredName());
						}

						if (alarmResponse.getAlarmedDevice().equalsIgnoreCase(activeZone)) {
							responseList.add(alarmResponse);
						}

					} else {
						response.setMessage("No device found by branch code" + alarmLog.getBranchCode());
						response.setStatusCode(HttpStatus.OK.value());
					}

				});

				response.setPayload(responseList);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			}
		} catch (Exception e) {
			response.setMessage("Data not found");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getRecordsByTimeFrame(String timeFrame , String branchCode) {
		ResponseEntity response = new ResponseEntity();
		try {
			
			List<String> branchCodeList=new ArrayList<>();
			ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
			if (responseEntity != null) {
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

				branchList.forEach(x->{
					branchCodeList.add(x.getBranchCode());
				});
			
			}
			
			
			
			List<AlarmAuditLogEntity> records;

			switch (timeFrame.toLowerCase()) {
			case "today":
				records = alarmRepo.getRecordsOfToday(branchCodeList);
				break;
			case "yesterday":
				records = alarmRepo.getRecordsOfYesterday(branchCodeList);
				break;
			case "last 7 days":
				records = alarmRepo.getRecordsOfLast7Days(branchCodeList);
				break;
			case "last 30 days":
				records = alarmRepo.getRecordsOfLast30Days(branchCodeList);
				break;
			case "this month":
				records = alarmRepo.getRecordsOfCurrentMonth(branchCodeList);
				break;
			case "last month":
				records = alarmRepo.getAllRecordsOfPreviousMonth(branchCodeList);
				break;
			default:
				response.setMessage("Invalid time frame provided.");
				response.setStatusCode(HttpStatus.BAD_REQUEST.value());
				return response;
			}

			if (records != null && !records.isEmpty()) {
				List<AlarmAuditLogDTO> responseList = new ArrayList<>();
				for (AlarmAuditLogEntity alarmEntity : records) {
					AlarmAuditLogDTO alarmDTO = new AlarmAuditLogDTO();
					BeanUtils.copyProperties(alarmDTO, alarmEntity);
					responseList.add(alarmDTO);
				}
				response.setPayload(responseList);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data found for the specified time frame.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Internal error occurred");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

	public ResponseEntity getAllRecordsByTimeRange(String startTime, String endTime , String branchCode) {
		ResponseEntity response = new ResponseEntity();
		
		
		
		List<String> branchCodeList=new ArrayList<>();
		ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
		if (responseEntity != null) {
			List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();

			branchList.forEach(x->{
				branchCodeList.add(x.getBranchCode());
			});
		
		}
		

		if (startTime.isEmpty() || endTime.isEmpty()) {
			response.setMessage("Start time and end time are mandatory.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			return response;
		}

		try {
			List<Object[]> records = alarmRepo.getAllRecordsByTimeRange(startTime, endTime,branchCodeList);

			if (records != null && !records.isEmpty()) {
				List<AlarmAuditLogDTO> responseList = new ArrayList<>();
				for (Object[] record : records) {
					AlarmAuditLogDTO alarmDTO = new AlarmAuditLogDTO();
					alarmDTO.setId((int) record[0]);
					alarmDTO.setActiveZone((String) record[1]);
					alarmDTO.setDateTime((String) record[2]);
					alarmDTO.setDeviceId((String) record[3]);
					alarmDTO.setRemarks((String) record[4]);
					alarmDTO.setUser((String) record[5]);
					alarmDTO.setAddress((String) record[6]);
					alarmDTO.setBranchCode((String) record[7]);
					alarmDTO.setMobile((String) record[8]);
					alarmDTO.setVendorName((String) record[9]);
					alarmDTO.setAlarmedDevice((String) record[10]);
					responseList.add(alarmDTO);
				}
				response.setPayload(responseList);
				response.setMessage("Data fetched successfully");
				response.setStatusCode(HttpStatus.OK.value());
			} else {
				response.setMessage("No data found for the specified time range.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage("Internal error occurred");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}
	public ResponseEntity getAllAcknowledgedAlerts(String branchCode) {
		ResponseEntity response = new ResponseEntity();
		List<String> branchCodeList = new ArrayList<>();

		try {
			ResponseEntity responseEntity = branchMasterService.getAllChildren(branchCode);
			if (responseEntity != null) {
				List<BranchMasterEntity> branchList = (List<BranchMasterEntity>) responseEntity.getPayload();
				branchList.forEach(x -> {
					branchCodeList.add(x.getBranchCode());
				});
			}

			List<AlarmAuditLogEntity> acknowledgedAlerts = alarmRepo.findAcknowledgedAlerts(branchCodeList);

			if (acknowledgedAlerts.isEmpty()) {
				response.setMessage("No Acknowledged Alerts present in the database.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());

			}

			List<AlarmAuditLogDTO> alarmAuditLogDTOs = new ArrayList<>();
			for (AlarmAuditLogEntity acknowledgedAlert : acknowledgedAlerts) {
				
				AlarmAuditLogDTO alarmAuditLogDTO = new AlarmAuditLogDTO();
				alarmAuditLogDTO.setId(acknowledgedAlert.getId());
				alarmAuditLogDTO.setActiveZone(acknowledgedAlert.getActiveZone());
				alarmAuditLogDTO.setBranchCode(acknowledgedAlert.getBranchCode());
				alarmAuditLogDTO.setDateTime(acknowledgedAlert.getDateTime());
				alarmAuditLogDTO.setDeviceId(acknowledgedAlert.getDeviceId());
				alarmAuditLogDTO.setMobile(acknowledgedAlert.getMobile());
				alarmAuditLogDTO.setRemarks(acknowledgedAlert.getRemarks());
				alarmAuditLogDTO.setUser(acknowledgedAlert.getUser());
				alarmAuditLogDTO.setAddress(acknowledgedAlert.getAddress());
				alarmAuditLogDTO.setVendorName(acknowledgedAlert.getVendorName());
				alarmAuditLogDTOs.add(alarmAuditLogDTO);
			}

			response.setMessage("Acknowledged alerts retrieved successfully.");
			response.setPayload(alarmAuditLogDTOs);
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			response.setMessage("An error occurred while processing the request.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}
	
	
	
	
		public org.springframework.http.ResponseEntity<Object> getAlaramAuditLogcount(String remark, String branchCode)
		 {
		ResponseEntity response = new ResponseEntity();

		List<String> allChildBranch = branchRepo.findAll().stream()
				.filter(prCode -> prCode.getParentCode().equalsIgnoreCase(branchCode))
				.map(BranchMasterEntity::getBranchCode)

				.collect(Collectors.toList());

		List<AlarmAuditLogEntity> allAlaram = alarmRepo.findAll().stream()
				.filter(alarm -> allChildBranch.contains(alarm.getBranchCode())).collect(Collectors.toList());

		int toatalCount = allAlaram.size();

		Long Acknowladege = allAlaram.stream().filter(remarks -> "ACKNOWLEDGED".equalsIgnoreCase(remarks.getRemarks()))
				.count();
		Long venderInfo = allAlaram.stream()
				.filter(remarks -> "SECURITY VENDOR INFORMED".equalsIgnoreCase(remarks.getRemarks())).count();
		Long testing = allAlaram.stream().filter(remarks -> "TESTING".equalsIgnoreCase(remarks.getRemarks())).count();
		Long falseAlarams = allAlaram.stream().filter(remarks -> "FALSE ALARM".equalsIgnoreCase(remarks.getRemarks()))
				.count();
		Map<String, Object> jsonObject = new HashMap<>();
		jsonObject.put("totalCount", toatalCount);
		jsonObject.put("venderInfo", venderInfo);
		jsonObject.put("testing", testing);
		jsonObject.put("falseAlarms", falseAlarams);
		jsonObject.put("acknowledge", Acknowladege);

		response.setPayload(jsonObject);
		response.setMessage("Data fetched successfully");
		response.setStatusCode(HttpStatus.OK.value());

		return new org.springframework.http.ResponseEntity<Object>(response, HttpStatus.OK);

	}


}