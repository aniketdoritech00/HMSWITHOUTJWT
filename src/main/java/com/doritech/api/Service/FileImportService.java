package com.doritech.api.Service;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.Common.General;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.EmployeeMasterEntity;
import com.doritech.api.Entity.ParamEntity;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.EmployeeMasterRepository;
import com.doritech.api.Repository.ParamRepository;


@Service
@Transactional(rollbackOn = Exception.class)
public class FileImportService {

	@Autowired
	private EmployeeMasterRepository employeeMasterRepo;


	@Autowired
	BranchMasterRepository branchMasterRepo;
	@Autowired
	ParamRepository paramRepository;
	
	@Autowired
	General general;

	public String importEmployeeMasterCSV(BufferedReader br) throws IOException {
		try {
			int line_no = 0;
			String line = "";
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] result = line.split("\\|");
					if (result.length == 16) {
						EmployeeMasterEntity entity = new EmployeeMasterEntity();
						entity.setEmpId(general.checkNull(result[0]));
						entity.setEmpRole(general.checkNull(result[1]));
						entity.setEmpName(general.checkNull(result[2]));
					//	entity.setEmpPan(general.checkNull(result[3]));
						entity.setAddress(general.checkNull(result[4]));
						entity.setCity(general.checkNull(result[5]));
						entity.setPin(general.checkNull(result[6]));
						entity.setState(general.checkNull(result[7]));
						entity.setCountry(general.checkNull(result[8]));
						entity.setMobile(general.checkNull(result[9]));
						entity.setEmail(general.checkNull(result[10]));
						entity.setStatus(general.checkNull(result[11]));
						entity.setBranch(general.checkNull(result[12]));
						entity.setLoginFlag(general.checkNull(result[13]));
						entity.setPassword(general.checkNull(result[14]));
						entity.setGroupId(general.checkNull(result[15]));
						employeeMasterRepo.save(entity);
					} else {
						System.out.println("Invalid line format at line number " + line_no + ": " + line);
					}
				}
				line_no++;
			}
			return "saved";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public String importEmployeeMasterHSSF(HSSFWorkbook workbook) {
		try {
			HSSFSheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.rowIterator();
			int i = 0;
			while (rowIterator.hasNext()) {
				EmployeeMasterEntity employeeMaster = new EmployeeMasterEntity();
				Row nextRow = rowIterator.next();
				if (i > 0) {
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					String val = "";
					String celldate = "";
					while (cellIterator.hasNext()) {
						Cell nextCell = cellIterator.next();
						int columnIndex = nextCell.getColumnIndex();
						switch (columnIndex) {
						case 0:
							val = general.getCellValue(nextCell);
							employeeMaster.setEmpId(general.checkNull(val.trim()));
							break;
						case 1:
							val = general.getCellValue(nextCell);
							employeeMaster.setEmpRole(general.checkNull(val.trim()));
							break;
						case 2:
							val = general.getCellValue(nextCell);
							employeeMaster.setEmpName(general.checkNull(val.trim()));
							break;
//						case 3:
//							val = general.getCellValue(nextCell);
//							employeeMaster.setEmpPan(general.checkNull(val.trim()));
//							break;
						case 4:
							val = general.getCellValue(nextCell);
							employeeMaster.setAddress(general.checkNull(val.trim()));
							break;
						case 5:
							val = general.getCellValue(nextCell);
							employeeMaster.setCity(general.checkNull(val.trim()));
							break;
						case 6:
							val = general.getCellValue(nextCell);
							employeeMaster.setPin(general.checkNull(val.trim()));
							break;
						case 7:
							val = general.getCellValue(nextCell);
							employeeMaster.setState(general.checkNull(val.trim()));
							break;
						case 8:
							val = general.getCellValue(nextCell);
							employeeMaster.setCountry(general.checkNull(val.trim()));
							break;
						case 9:
							val = general.getCellValue(nextCell);
							employeeMaster.setMobile(general.checkNull(val.trim()));
							break;
						case 10:
							val = general.getCellValue(nextCell);
							employeeMaster.setEmail(general.checkNull(val.trim()));
							break;
						case 11:
							val = general.getCellValue(nextCell);
							employeeMaster.setStatus(general.checkNull(val.trim()));
							break;
						case 12:
							val = general.getCellValue(nextCell);
							employeeMaster.setBranch(general.checkNull(val.trim()));
							break;
						case 13:
							val = general.getCellValue(nextCell);
							employeeMaster.setLoginFlag(general.checkNull(val.trim()));
							break;
						case 14:
							val = general.getCellValue(nextCell);
							employeeMaster.setPassword(general.checkNull(val.trim()));
							break;
						case 15:
							val = general.getCellValue(nextCell);
							employeeMaster.setGroupId(general.checkNull(val.trim()));
							break;
						}
					}
					if (employeeMasterRepo.findById(employeeMaster.getEmpId()).isPresent()) {
						employeeMasterRepo.deleteById(employeeMaster.getEmpId());
						employeeMasterRepo.save(employeeMaster);
					} else {
						System.out.println("save employee");
						employeeMasterRepo.save(employeeMaster);
					}
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
		return "saved";
	}

	public String importEmployeeMasterXSSF(XSSFWorkbook workbook) throws Exception {
		try {
			XSSFSheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.rowIterator();
			int i = 0;
			while (rowIterator.hasNext()) {
				EmployeeMasterEntity employeeMaster = new EmployeeMasterEntity();
				Row nextRow = rowIterator.next();
				if (i > 0) {
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					String val = "";
					String celldate = "";
					while (cellIterator.hasNext()) {
						Cell nextCell = cellIterator.next();
						int columnIndex = nextCell.getColumnIndex();
						switch (columnIndex) {
						case 0:
							try {
								System.out.println("EMP ID");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setEmpId(val.trim());
							break;
						case 1:
							try {
								System.out.println("EMP ROLE");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setEmpRole(val.trim());
							break;
						case 2:
							try {
								System.out.println("EMP NAME");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setEmpName(val.trim());
							break;
//						case 3:
//							try {
//								System.out.println("EMP PAN");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							employeeMaster.setEmpPan(val.trim());
//							break;
						case 4:
							try {
								System.out.println("Address");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setAddress(val.trim());
							break;
						case 5:
							try {
								System.out.println("City");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setCity(val.trim());
							break;
						case 6:
							try {
								System.out.println("Pin");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setPin(val.trim());
							break;
						case 7:
							try {
								System.out.println("State");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setState(val.trim());
							break;
						case 8:
							try {
								System.out.println("Country");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setCountry(val.trim());
							break;
						case 9:
							try {
								System.out.println("Mobile");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setMobile(val.trim());
							break;
						case 10:
							try {
								System.out.println("Email");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setEmail(val.trim());
							break;
						case 11:
							try {
								System.out.println("Status");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setStatus(val.trim());
							break;
						case 12:
							try {
								System.out.println("Branch");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setBranch(val.trim());
							break;
						case 13:
							try {
								System.out.println("Login Flag");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setLoginFlag(val.trim());
							break;
						case 14:
							try {
								System.out.println("Password");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setPassword(val.trim());
							break;
						case 15:
							try {
								System.out.println("Group Id");
								val = general.getCellValue(nextCell);
							} catch (Exception c) {
								val = "";
							}
							employeeMaster.setGroupId(val.trim());
							break;
						}
					}
					if (employeeMasterRepo.findById(employeeMaster.getEmpId()).isPresent()) {
						employeeMasterRepo.deleteById(employeeMaster.getEmpId());
						employeeMasterRepo.save(employeeMaster);
					} else {
						System.out.println("save employee");
						employeeMasterRepo.save(employeeMaster);
					}
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "saved";
	}

	public String importEmployeeMasterSXSSF(SXSSFWorkbook workbook) {
		try {
			SXSSFSheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.rowIterator();
			int i = 0;

			while (rowIterator.hasNext()) {
				SXSSFRow nextRow = (SXSSFRow) rowIterator.next();
				if (i > 0) {
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					EmployeeMasterEntity employeeMaster = new EmployeeMasterEntity();

					while (cellIterator.hasNext()) {
						SXSSFCell nextCell = (SXSSFCell) cellIterator.next();
						int columnIndex = nextCell.getColumnIndex();
						String val = general.getCellValue(nextCell);
						switch (columnIndex) {
						case 0:
							employeeMaster.setEmpId(general.checkNull(val.trim()));
							break;
						case 1:
							employeeMaster.setEmpRole(general.checkNull(val.trim()));
							break;
						case 2:
							employeeMaster.setEmpName(general.checkNull(val.trim()));
							break;
//						case 3:
//							employeeMaster.setEmpPan(general.checkNull(val.trim()));
//							break;
						case 4:
							employeeMaster.setAddress(general.checkNull(val.trim()));
							break;
						case 5:
							employeeMaster.setCity(general.checkNull(val.trim()));
							break;
						case 6:
							employeeMaster.setPin(general.checkNull(val.trim()));
							break;
						case 7:
							employeeMaster.setState(general.checkNull(val.trim()));
							break;
						case 8:
							employeeMaster.setCountry(general.checkNull(val.trim()));
							break;
						case 9:
							employeeMaster.setMobile(general.checkNull(val.trim()));
							break;
						case 10:
							employeeMaster.setEmail(general.checkNull(val.trim()));
							break;
						case 11:
							employeeMaster.setStatus(general.checkNull(val.trim()));
							break;
						case 12:
							employeeMaster.setBranch(general.checkNull(val.trim()));
							break;
						case 13:
							employeeMaster.setLoginFlag(general.checkNull(val.trim()));
							break;
						case 14:
							employeeMaster.setPassword(general.checkNull(val.trim()));
							break;
						case 15:
							employeeMaster.setGroupId(general.checkNull(val.trim()));
							break;
						}
					}
					if (employeeMasterRepo.findById(employeeMaster.getEmpId()).isPresent()) {
						employeeMasterRepo.deleteById(employeeMaster.getEmpId());
						employeeMasterRepo.save(employeeMaster);
					} else {
						System.out.println("save employee");
						employeeMasterRepo.save(employeeMaster);
					}
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
		return "saved";
	}
	
	//FOR BRANCH MASTER
	public String importBranchMasterCSV(BufferedReader br) throws IOException {
		try {
			int line_no = 0;
			String line = "";
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					String[] result = line.split("\\|");
					if (result.length == 14) {
						BranchMasterEntity entity = new BranchMasterEntity();
						entity.setBranchCode(general.checkNull(result[0]));
						entity.setParentCode(general.checkNull(result[1]));
						entity.setBranchType(general.checkNull(result[2]));
						entity.setBranchDesc(general.checkNull(result[3]));
						entity.setContractPerson(general.checkNull(result[4]));
						entity.setAddress1(general.checkNull(result[5]));
						entity.setAddress2(general.checkNull(result[6]));
						entity.setAddress3(general.checkNull(result[7]));
						entity.setAddress4(general.checkNull(result[8]));
						entity.setMobile(general.checkNull(result[9]));
						entity.setTelephone(general.checkNull(result[11]));
						entity.setFaxNo(general.checkNull(result[12]));
						entity.setStatus(general.checkNull(result[13]));
						entity.setEmail(general.checkNull(result[14]));
						branchMasterRepo.save(entity);
					} else {
						System.out.println("Invalid line format at line number " + line_no + ": " + line);
					}
				}
				line_no++;
			}
			return "saved";
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
	}

	public String importBranchMasterHSSF(HSSFWorkbook workbook) {
		try {
			HSSFSheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.rowIterator();
			int i = 0;
			while (rowIterator.hasNext()) {
				BranchMasterEntity branchMaster = new BranchMasterEntity();
				Row nextRow = rowIterator.next();
				if (i > 0) {
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					String val = "";
					String celldate = "";
					while (cellIterator.hasNext()) {
						Cell nextCell = cellIterator.next();
						int columnIndex = nextCell.getColumnIndex();
						switch (columnIndex) {
						case 0:
							val = general.getCellValue(nextCell);
							branchMaster.setBranchCode(general.checkNull(val.trim()));
							break;
						case 1:
							val = general.getCellValue(nextCell);
							branchMaster.setParentCode(general.checkNull(val.trim()));
							break;
						case 2:
							val = general.getCellValue(nextCell);
							branchMaster.setBranchType(general.checkNull(val.trim()));
							break;
						case 3:
							val = general.getCellValue(nextCell);
							branchMaster.setBranchDesc(general.checkNull(val.trim()));
							break;
						case 4:
							val = general.getCellValue(nextCell);
							branchMaster.setContractPerson(general.checkNull(val.trim()));
							break;
						case 5:
							val = general.getCellValue(nextCell);
							branchMaster.setAddress1(general.checkNull(val.trim()));
							break;
						case 6:
							val = general.getCellValue(nextCell);
							branchMaster.setAddress2(general.checkNull(val.trim()));
							break;
						case 7:
							val = general.getCellValue(nextCell);
							branchMaster.setAddress3(general.checkNull(val.trim()));
							break;
						case 8:
							val = general.getCellValue(nextCell);
							branchMaster.setAddress4(general.checkNull(val.trim()));
							break;
						case 9:
							val = general.getCellValue(nextCell);
							branchMaster.setMobile(general.checkNull(val.trim()));
							break;
						case 10:
							val = general.getCellValue(nextCell);
							branchMaster.setTelephone(general.checkNull(val.trim()));
							break;
						case 11:
							val = general.getCellValue(nextCell);
							branchMaster.setFaxNo(general.checkNull(val.trim()));
							break;
						case 12:
							val = general.getCellValue(nextCell);
							branchMaster.setStatus(general.checkNull(val.trim()));
							break;
						case 13:
							val = general.getCellValue(nextCell);
							branchMaster.setEmail(general.checkNull(val.trim()));
							break;
						}						
					}
					if (branchMasterRepo.findById(branchMaster.getBranchCode()).isPresent()) {
						branchMasterRepo.deleteById(branchMaster.getBranchCode());
						branchMasterRepo.save(branchMaster);
					} else {
						System.out.println("save branch");
						branchMasterRepo.save(branchMaster);
					}
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
		return "saved";
	}

//	public String importBranchMasterXSSF(XSSFWorkbook workbook) throws Exception {
//		try {
//			XSSFSheet firstSheet = workbook.getSheetAt(0);
//			Iterator<Row> rowIterator = firstSheet.rowIterator();
//			int i = 0;
//			while (rowIterator.hasNext()) {
//				BranchMasterEntity branchMaster = new BranchMasterEntity();
//				Row nextRow = rowIterator.next();
//				if (i > 0) {
//					Iterator<Cell> cellIterator = nextRow.cellIterator();
//					String val = "";
//					String celldate = "";
//					while (cellIterator.hasNext()) {
//						Cell nextCell = cellIterator.next();
//						int columnIndex = nextCell.getColumnIndex();
//						switch (columnIndex) {
//						case 0:
//							try {
//								System.out.println("BRANCH CODE");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setBranchCode(val.trim());
//							break;
//						case 1:
//							try {
//								System.out.println("PARENT CODE");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setParentCode(val.trim());
//							break;
//						case 2:
//							try {
//								System.out.println("BRANCH TYPE");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setBranchType(val.trim());
//							break;
//						case 3:
//							try {
//								System.out.println("BRANCH DESC");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setBranchDesc(val.trim());
//							break;
//						case 4:
//							try {
//								System.out.println("CONTACT PERSON");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setContractPerson(val.trim());
//							break;
//						case 5:
//							try {
//								System.out.println("ADDRESS1");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setAddress1(val.trim());
//							break;
//						case 6:
//							try {
//								System.out.println("ADDRESS2");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setAddress2(val.trim());
//							break;
//						case 7:
//							try {
//								System.out.println("ADDRESS3");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setAddress3(val.trim());
//							break;
//						case 8:
//							try {
//								System.out.println("ADDRESS4");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setAddress4(val.trim());
//							break;
//						case 9:
//							try {
//								System.out.println("MOBILE");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setMobile(val);
//							break;
//						case 10:
//							try {
//								System.out.println("TELEPHONE");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setTelephone(val.trim());
//							break;
//						case 11:
//							try {
//								System.out.println("FAX NO");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setFaxNo(val.trim());
//							break;
//						case 12:
//							try {
//								System.out.println("STATUS");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setStatus(val.trim());
//							break;
//						case 13:
//							try {
//								System.out.println("EMAIL");
//								val = general.getCellValue(nextCell);
//							} catch (Exception c) {
//								val = "";
//							}
//							branchMaster.setEmail(val.trim());
//							break;
//						
//						}
//					}
//					if (branchMasterRepo.findById(branchMaster.getBranchCode()).isPresent()) {
//						branchMasterRepo.deleteById(branchMaster.getBranchCode());
//						branchMasterRepo.save(branchMaster);
//					} else {
//						System.out.println("save Branch");
//						branchMasterRepo.save(branchMaster);
//					}
//				}
//				i++;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "saved";
//	}

	public String importBranchMasterXSSF(XSSFWorkbook workbook) throws Exception {
	    try {
	        DataFormatter dataFormatter = new DataFormatter();
	        XSSFSheet firstSheet = workbook.getSheetAt(0);
	        Iterator<Row> rowIterator = firstSheet.iterator();
	        int i = 0;
	        while (rowIterator.hasNext()) {
	            BranchMasterEntity branchMaster = new BranchMasterEntity();
	            Row nextRow = rowIterator.next();
	            if (i > 0) {
	                Iterator<Cell> cellIterator = nextRow.cellIterator();
	                while (cellIterator.hasNext()) {
	                    Cell nextCell = cellIterator.next();
	                    int columnIndex = nextCell.getColumnIndex();
	                    String val = "";
	                    try {
	                        // Force treating cell data as text
	                        val = dataFormatter.formatCellValue(nextCell);
	                    } catch (Exception e) {
	                        System.err.println("Error retrieving cell value: " + e.getMessage());
	                    }
	                    switch (columnIndex) {
	                        case 0:
	                            System.out.println("BRANCH CODE: " + val);
	                            branchMaster.setBranchCode(val.trim());
	                            break;
	                        case 1:
	                            System.out.println("PARENT CODE: " + val);
	                            branchMaster.setParentCode(val.trim());
	                            break;
	                        case 2:
	                            System.out.println("BRANCH TYPE: " + val);
	                            branchMaster.setBranchType(val.trim());
	                            break;
	                        case 3:
	                            System.out.println("BRANCH DESC: " + val);
	                            branchMaster.setBranchDesc(val.trim());
	                            break;
	                        case 4:
	                            System.out.println("CONTACT PERSON: " + val);
	                            branchMaster.setContractPerson(val.trim());
	                            break;
	                        case 5:
	                            System.out.println("ADDRESS1: " + val);
	                            branchMaster.setAddress1(val.trim());
	                            break;
	                        case 6:
	                            System.out.println("ADDRESS2: " + val);
	                            branchMaster.setAddress2(val.trim());
	                            break;
	                        case 7:
	                            System.out.println("ADDRESS3: " + val);
	                            branchMaster.setAddress3(val.trim());
	                            break;
	                        case 8:
	                            System.out.println("ADDRESS4: " + val);
	                            branchMaster.setAddress4(val.trim());
	                            break;
	                        case 9:
	                            System.out.println("MOBILE: " + val);
	                            branchMaster.setMobile(val.trim());
	                            break;
	                        case 10:
	                            System.out.println("TELEPHONE: " + val);
	                            branchMaster.setTelephone(val.trim());
	                            break;
	                        case 11:
	                            System.out.println("FAX NO: " + val);
	                            branchMaster.setFaxNo(val.trim());
	                            break;
	                        case 12:
	                            System.out.println("STATUS: " + val);
	                            branchMaster.setStatus(val.trim());
	                            break;
	                        case 13:
	                            System.out.println("EMAIL: " + val);
	                            branchMaster.setEmail(val.trim());
	                            break;
	                        default:
	                            System.out.println("Unknown column index: " + columnIndex);
	                            break;
	                    }
	                }
	                try {
	                    if (branchMasterRepo.findById(branchMaster.getBranchCode()).isPresent()) {
	                        branchMasterRepo.deleteById(branchMaster.getBranchCode());
	                        branchMasterRepo.save(branchMaster);
	                    } else {
	                        System.out.println("Saving Branch");
	                        branchMasterRepo.save(branchMaster);
	                    }
	                } catch (Exception e) {
	                    System.err.println("Error saving branch master: " + e.getMessage());
	                }
	            }
	            i++;
	        }
	    } catch (Exception e) {
	        System.err.println("Error processing Excel sheet: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return "saved";
	}

	public String importBranchMasterSXSSF(SXSSFWorkbook workbook) {
		try {
			SXSSFSheet firstSheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = firstSheet.rowIterator();
			int i = 0;

			while (rowIterator.hasNext()) {
				SXSSFRow nextRow = (SXSSFRow) rowIterator.next();
				if (i > 0) {
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					BranchMasterEntity branchMaster = new BranchMasterEntity();

					while (cellIterator.hasNext()) {
						SXSSFCell nextCell = (SXSSFCell) cellIterator.next();
						int columnIndex = nextCell.getColumnIndex();
						String val = general.getCellValue(nextCell);
						switch (columnIndex) {
						case 0:
							branchMaster.setBranchCode(general.checkNull(val.trim()));
							break;
						case 1:
							branchMaster.setParentCode(general.checkNull(val.trim()));
							break;
						case 2:
							branchMaster.setBranchType(general.checkNull(val.trim()));
							break;
						case 3:
							branchMaster.setBranchDesc(general.checkNull(val.trim()));
							break;
						case 4:
							branchMaster.setContractPerson(general.checkNull(val.trim()));
							break;
						case 5:
							branchMaster.setAddress1(general.checkNull(val.trim()));
							break;
						case 6:
							branchMaster.setAddress2(general.checkNull(val.trim()));
							break;
						case 7:
							branchMaster.setAddress3(general.checkNull(val.trim()));
							break;
						case 8:
							branchMaster.setAddress4(general.checkNull(val.trim()));
							break;
						case 9:
							branchMaster.setMobile(general.checkNull(val.trim()));
							break;
						case 10:
							branchMaster.setTelephone(general.checkNull(val.trim()));
							break;
						case 11:
							branchMaster.setFaxNo(general.checkNull(val.trim()));
							break;
						case 12:
							branchMaster.setStatus(general.checkNull(val.trim()));
							break;
						case 13:
							branchMaster.setEmail(general.checkNull(val.trim()));
							break;
						}
					}
					if (branchMasterRepo.findById(branchMaster.getBranchCode()).isPresent()) {
						branchMasterRepo.deleteById(branchMaster.getBranchCode());
						branchMasterRepo.save(branchMaster);
					} else {
						System.out.println("save Branch");
						branchMasterRepo.save(branchMaster);
					}
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "failed";
		}
		return "saved";
	}
	public ResponseEntity<byte[]> processExcelFile(MultipartFile file,HttpSession session) {
	    if (file == null || file.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Please select a valid Excel file.".getBytes());
	    }

	    try {
	        List<BranchMasterEntity> branches = parseExcelFile(file,session);
	        int savedCount = 0, skippedCount = 0;

	        for (BranchMasterEntity branch : branches) {
	            if (branch.getIfsc() != null && branchMasterRepo.existsByIfsc(branch.getIfsc())) {
	                branch.setExcelStatus("IFSC Code Already Present");
	                skippedCount++;
	            } else {
	                String generatedBranchCode = generateBranchCode();
	                branch.setBranchCode(generatedBranchCode);
	                branch.setExcelStatus("SAVED");
	                branchMasterRepo.save(branch);
	                savedCount++;
	            }
	        }

	        ByteArrayInputStream stream = generateStatusExcel(branches);
	        byte[] excelBytes = stream.readAllBytes();

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("upload_status.xlsx").build());

	        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

	    } catch (IllegalArgumentException ex) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(("Header validation failed: " + ex.getMessage()).getBytes());
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(("Upload failed: " + ex.getMessage()).getBytes());
	    }
	}

	private String generateBranchCode() throws Exception {
	    ParamEntity param = paramRepository.findByCode("BRANCH_ID");
	    if (param == null) throw new Exception("ParamEntity with code 'BRANCH_ID' not found");

	    String branchCode = param.getDescp1() + param.getDescp2();
	    try {
	        int next = Integer.parseInt(param.getDescp2()) + 1;
	        param.setDescp2(String.valueOf(next));
	        paramRepository.save(param);
	    } catch (NumberFormatException ex) {
	        throw new Exception("Invalid numeric format for branch code increment.");
	    }
	    return branchCode;
	}

	private List<BranchMasterEntity> parseExcelFile(MultipartFile file, HttpSession session) throws Exception {
	    List<BranchMasterEntity> branchList = new ArrayList<>();
	    InputStream inputStream = file.getInputStream();
	    Workbook workbook = null;

	    try {
	        if (file.getOriginalFilename().endsWith(".xlsx")) {
	            workbook = new XSSFWorkbook(inputStream);
	        } else if (file.getOriginalFilename().endsWith(".xls")) {
	            workbook = new HSSFWorkbook(inputStream);
	        } else {
	            throw new IllegalArgumentException("Invalid file format. Only .xls and .xlsx files are supported.");
	        }

	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rows = sheet.iterator();
	        Map<String, Integer> headerMap = new HashMap<>();

	        if (rows.hasNext()) {
	            Row headerRow = rows.next();
	            for (Cell cell : headerRow) {
	                headerMap.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
	            }
	        }

	        List<String> expectedHeaders = Arrays.asList(
	        	    "parent code", "branch type", "branch desc", "contract person", "address1", "address2", "address3", "address4",
	        	    "mobile", "telephone", "fax no", "status", "email", "bank", "ifsc", "latitude", "longitude",
	        	    "email2", "email3", "email4", "email5", "email6",
	        	    "mobile quaternary", "mobile quinary", "mobile secondary", "mobile senary", "mobile tertiary"
	        	);

	        for (String header : expectedHeaders) {
	            if (!headerMap.containsKey(header)) {
	                throw new IllegalArgumentException("Missing header: " + header);
	            }
	        }

	        while (rows.hasNext()) {
	            Row row = rows.next();
	            BranchMasterEntity branch = new BranchMasterEntity();

	            branch.setParentCode(getCellValue(row, headerMap.get("parent code")));
	            branch.setBranchType(getCellValue(row, headerMap.get("branch type")));
	            branch.setBranchDesc(getCellValue(row, headerMap.get("branch desc")));
	            branch.setContractPerson(getCellValue(row, headerMap.get("contract person")));
	            branch.setAddress1(getCellValue(row, headerMap.get("address1")));
	            branch.setAddress2(getCellValue(row, headerMap.get("address2")));
	            branch.setAddress3(getCellValue(row, headerMap.get("address3")));
	            branch.setAddress4(getCellValue(row, headerMap.get("address4")));
	            branch.setMobile(getCellValue(row, headerMap.get("mobile")));
	            branch.setTelephone(getCellValue(row, headerMap.get("telephone")));
	            branch.setFaxNo(getCellValue(row, headerMap.get("fax no")));
	            branch.setStatus(getCellValue(row, headerMap.get("status")));
	            branch.setEmail(getCellValue(row, headerMap.get("email")));
	            branch.setBank(getCellValue(row, headerMap.get("bank")));
	            branch.setIfsc(getCellValue(row, headerMap.get("ifsc")));
	            branch.setLatitude(getCellValue(row, headerMap.get("latitude")));
	            branch.setLongitude(getCellValue(row, headerMap.get("longitude")));
	            branch.setEmail2(getCellValue(row, headerMap.get("email2")));
	            branch.setEmail3(getCellValue(row, headerMap.get("email3")));
	            branch.setEmail4(getCellValue(row, headerMap.get("email4")));
	            branch.setEmail5(getCellValue(row, headerMap.get("email5")));
	            branch.setEmail6(getCellValue(row, headerMap.get("email6")));
	            branch.setMobileQuaternary(getCellValue(row, headerMap.get("mobile quaternary")));
	            branch.setMobileQuinary(getCellValue(row, headerMap.get("mobile quinary")));
	            branch.setMobileSecondary(getCellValue(row, headerMap.get("mobile secondary")));
	            branch.setMobileSenary(getCellValue(row, headerMap.get("mobile senary")));
	            branch.setMobileTertiary(getCellValue(row, headerMap.get("mobile tertiary")));
	            branch.setCreatedBy(String.valueOf(session.getAttribute("empId")));
	            branch.setCreatedDate(new Date().toString());

	            int rowNum = row.getRowNum() + 1;
	            if (isEmpty(branch.getParentCode())) throw new IllegalArgumentException("Field 'Parent Code' is required at row: " + rowNum);
	            if (isEmpty(branch.getBranchType())) throw new IllegalArgumentException("Field 'Branch Type' is required at row: " + rowNum);
	            if (isEmpty(branch.getBranchDesc())) throw new IllegalArgumentException("Field 'Branch Desc' is required at row: " + rowNum);
	            if (isEmpty(branch.getContractPerson())) throw new IllegalArgumentException("Field 'Contract Person' is required at row: " + rowNum);
	            if (isEmpty(branch.getAddress1())) throw new IllegalArgumentException("Field 'Address1' is required at row: " + rowNum);
	            if (isEmpty(branch.getAddress2())) throw new IllegalArgumentException("Field 'Address2' is required at row: " + rowNum);
	            if (isEmpty(branch.getAddress3())) throw new IllegalArgumentException("Field 'Address3' is required at row: " + rowNum);
	            if (isEmpty(branch.getAddress4())) throw new IllegalArgumentException("Field 'Address4' is required at row: " + rowNum);
	            if (isEmpty(branch.getMobile())) throw new IllegalArgumentException("Field 'Mobile' is required at row: " + rowNum);
	            if (isEmpty(branch.getStatus())) throw new IllegalArgumentException("Field 'Status' is required at row: " + rowNum);
	            if (isEmpty(branch.getEmail())) throw new IllegalArgumentException("Field 'Email' is required at row: " + rowNum);
	            if (isEmpty(branch.getIfsc())) throw new IllegalArgumentException("Field 'Ifsc' is required at row: " + rowNum);
	            if (isEmpty(branch.getBank())) throw new IllegalArgumentException("Field 'Bank' is required at row: " + rowNum);
	            if (isEmpty(branch.getLatitude())) throw new IllegalArgumentException("Field 'Latitude' is required at row: " + rowNum);
	            if (isEmpty(branch.getLongitude())) throw new IllegalArgumentException("Field 'Longitude' is required at row: " + rowNum);

	            branchList.add(branch);
	        }

	    } finally {
	        if (workbook != null) workbook.close();
	        inputStream.close();
	    }

	    return branchList;
	}
	private boolean isEmpty(String value) {
	    return value == null || value.trim().isEmpty();
	}

	private String getCellValue(Row row, int colIndex) {
	    Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
	    switch (cell.getCellTypeEnum()) {
	        case STRING:
	            return cell.getStringCellValue().trim();
	        case NUMERIC:
	            return String.valueOf(cell.getNumericCellValue());
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case FORMULA:
	            return cell.getCellFormula();
	        default:
	            return "";
	    }
	}

	private ByteArrayInputStream generateStatusExcel(List<BranchMasterEntity> branches) throws IOException {
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("UploadStatus");

	    Row header = sheet.createRow(0);
	    String[] headers = {
	            "Parent Code", "Branch Type", "Branch Desc", "Contract Person", "Address1", "Address2", "Address3", "Address4",
	            "Mobile", "Telephone", "Fax No", "Status", "Email", "Bank", "Ifsc", "Latitude", "Longitude",
	            "Email2", "Email3", "Email4", "Email5", "Email6",
	            "Mobile Quaternary", "Mobile Quinary", "Mobile Secondary", "Mobile Senary", "Mobile Tertiary", "Excel Status"
	    };

	    for (int i = 0; i < headers.length; i++) {
	        header.createCell(i).setCellValue(headers[i]);
	    }

	    int rowIdx = 1;
	    for (BranchMasterEntity b : branches) {
	        Row row = sheet.createRow(rowIdx++);
	        int c = 0;
	        row.createCell(c++).setCellValue(b.getParentCode());
	        row.createCell(c++).setCellValue(b.getBranchType());
	        row.createCell(c++).setCellValue(b.getBranchDesc());
	        row.createCell(c++).setCellValue(b.getContractPerson());
	        row.createCell(c++).setCellValue(b.getAddress1());
	        row.createCell(c++).setCellValue(b.getAddress2());
	        row.createCell(c++).setCellValue(b.getAddress3());
	        row.createCell(c++).setCellValue(b.getAddress4());
	        row.createCell(c++).setCellValue(b.getMobile());
	        row.createCell(c++).setCellValue(b.getTelephone());
	        row.createCell(c++).setCellValue(b.getFaxNo());
	        row.createCell(c++).setCellValue(b.getStatus());
	        row.createCell(c++).setCellValue(b.getEmail());
	        row.createCell(c++).setCellValue(b.getBank());
	        row.createCell(c++).setCellValue(b.getIfsc());
	        row.createCell(c++).setCellValue(b.getLatitude());
	        row.createCell(c++).setCellValue(b.getLongitude());
	        row.createCell(c++).setCellValue(b.getEmail2());
	        row.createCell(c++).setCellValue(b.getEmail3());
	        row.createCell(c++).setCellValue(b.getEmail4());
	        row.createCell(c++).setCellValue(b.getEmail5());
	        row.createCell(c++).setCellValue(b.getEmail6());
	        row.createCell(c++).setCellValue(b.getMobileQuaternary());
	        row.createCell(c++).setCellValue(b.getMobileQuinary());
	        row.createCell(c++).setCellValue(b.getMobileSecondary());
	        row.createCell(c++).setCellValue(b.getMobileSenary());
	        row.createCell(c++).setCellValue(b.getMobileTertiary());
	        row.createCell(c++).setCellValue(b.getExcelStatus());
	    }

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    workbook.write(out);
	    workbook.close();
	    return new ByteArrayInputStream(out.toByteArray());
	}

}
