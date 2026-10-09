package com.doritech.api.Service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.api.Common.CommunicationService;
import com.doritech.api.Common.General;
import com.doritech.api.DTO.LogoDTO;
import com.doritech.api.Entity.BankMasterEntity;
import com.doritech.api.Entity.BranchMasterEntity;
import com.doritech.api.Entity.DeviceMasterEntity;
import com.doritech.api.Entity.LogoEntity;
import com.doritech.api.Entity.ManualSyncLog;
import com.doritech.api.Repository.BankMasterRepository;
import com.doritech.api.Repository.BranchMasterRepository;
import com.doritech.api.Repository.DeviceMasterRepository;
import com.doritech.api.Repository.EmailLogRepo;
import com.doritech.api.Repository.LogoRepository;
import com.doritech.api.Repository.MailParamRepo;
import com.doritech.api.Repository.ManualSyncLogRepository;
import com.doritech.api.Repository.ReadMailRepository;
import com.doritech.api.Repository.StaticDataRepo;
import com.doritech.api.Repository.WhatsappLogsRepository;

@Service
public class LogoService {

	@Autowired
	private LogoRepository logoRepository;

	@Value("${apiUrlForHealth}")
	private String apiUrlHealth;
	@Autowired
	private General general;

	@Autowired
	BranchMasterRepository branchMasterRepository;
	@Autowired
	ManualSyncLogRepository manualSyncLogRepository;
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	MailParamRepo mailParamRepo;

	@Autowired
	ServletContext context;

	@Autowired
	ParamService paramService;

	@Autowired
	ReadMailRepository readMailrepo;

	@Autowired
	DeviceMasterRepository deviceMasterrepository;

	@Autowired
	StaticDataRepo staticDataRepo;

	@Autowired
	WhatsappLogsRepository whatappLogsRepo;
	@Autowired
	EmailLogRepo emailLogRepo;

	@Value("${apiUrl}")
	private String apiUrl;

	@Value("${WhatsappKey}")
	private String WhatsappKey;

	@Autowired
	CommunicationService communicationService;

	@Autowired
	SocialMediaService socialMediaService;
	
	@Autowired
	private BankMasterRepository bankMasterRepository;

	private static final Logger logger = LoggerFactory.getLogger(ReadMail.class);

	public ResponseEntity<Object> saveLogoImage(String branchCode, MultipartFile file) {

		Optional<LogoEntity> optional = logoRepository.findByBranchCode(branchCode);

		LogoEntity logoEntity;

		if (optional.isEmpty()) {
			logoEntity = new LogoEntity();
		} else {
			logoEntity = optional.get();
		}

		logoEntity.setBranchCode(branchCode);

		try {

			// Read uploaded image
			BufferedImage originalImage = ImageIO.read(file.getInputStream());

			if (originalImage == null) {
				return ResponseEntity.badRequest().body("Invalid image file. Please upload a valid image.");
			}

			// Fixed dimensions
			int width = 647;
			int height = 235;
 
			// Create new image with exact size
			BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			// Draw resized image
			Graphics2D graphics = resizedImage.createGraphics();

			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			graphics.drawImage(originalImage, 0, 0, width, height, null);

			graphics.dispose();

			// Convert resized image to PNG bytes
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			ImageIO.write(resizedImage, "png", outputStream);

			logoEntity.setImage(outputStream.toByteArray());

		} catch (Exception e) {

			return ResponseEntity.badRequest().body("Invalid image file: " + e.getMessage());
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String formattedTimestamp = sdf.format(general.getCurrentTimeStamp());

		logoEntity.setTimeStamp(formattedTimestamp);

		LogoEntity savedLogoEntity = logoRepository.save(logoEntity);

		return ResponseEntity.ok(savedLogoEntity);
	}

	/*
	 * public ResponseEntity<Object> saveLogoImage(String branchCode, MultipartFile
	 * file) {
	 * 
	 * Optional<LogoEntity> optional = logoRepository.findByBranchCode(branchCode);
	 * 
	 * LogoEntity logoEntity=null; if(optional.isEmpty()) { logoEntity = new
	 * LogoEntity(); }else { logoEntity = optional.get(); }
	 * 
	 * 
	 * 
	 * 
	 * logoEntity.setBranchCode(branchCode);
	 * 
	 * try { logoEntity.setImage(file.getBytes()); } catch (Exception e) { return
	 * ResponseEntity.badRequest().body("Invalid image file: " + e.getMessage()); }
	 * 
	 * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); String
	 * formattedTimestamp = sdf.format(general.getCurrentTimeStamp());
	 * logoEntity.setTimeStamp(formattedTimestamp);
	 * 
	 * LogoEntity savedLogoEntity = logoRepository.save(logoEntity); return
	 * ResponseEntity.ok(savedLogoEntity); }
	 */

//    public ResponseEntity<Object> getLogoByBranchCode(String branchCode) {
//    	Optional<BranchMasterEntity> branchMaster=Optional.ofNullable(branchMasterRepository.findBybranchCode(branchCode));
//         System.out.println(branchMaster.get());
//         BranchMasterEntity branchMasterEntity = null;
//         if(branchMaster != null) {
//        	 branchMasterEntity= branchMaster.get();
//        }
//        Optional<LogoEntity> logoEntityOpt = Optional.ofNullable(logoRepository.findByBranchCode(branchMasterEntity.getParentCode()));
//        
//        if (logoEntityOpt.isPresent()) {
//            LogoEntity logoEntity = logoEntityOpt.get();
//            LogoDTO logoDTO = new LogoDTO();
//            BeanUtils.copyProperties(logoEntity, logoDTO);
//            return ResponseEntity.ok(logoDTO);
//        } else {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body("Logo not present for branchCode : "+branchCode);
//        }
//    }
//
//    public LogoEntity getImageById(Long id) {
//        Optional<LogoEntity> logoOptional = logoRepository.findById(id);
//        return logoOptional.orElse(null);
//    }
//	public Optional<LogoEntity> getImageById(Long id) {
//		return logoRepository.findById(id);
//	}
//	public Optional<LogoEntity> getImageById(String branchCode) {
//        if (branchCode == null || branchCode.trim().isEmpty()) {
//            return Optional.empty();
//        }
//        BranchMasterEntity branchDetails = branchMasterRepository.findBybranchCode(branchCode);
//        if (branchDetails != null) {
//            return logoRepository.findByBranchCode(branchDetails.getParentCode());
//        } else {
//            return Optional.empty();
//        }
//    }
	public Optional<LogoDTO> getImageById(Long id) {
		Optional<LogoEntity> logoEntityOpt = logoRepository.findById(id);
		if (logoEntityOpt.isPresent()) {
			LogoEntity logoEntity = logoEntityOpt.get();
			LogoDTO logoDTO = new LogoDTO();
			logoDTO.setId(logoEntity.getId());
			logoDTO.setImage(logoEntity.getImage());
			logoDTO.setBranchCode(logoEntity.getBranchCode());
			logoDTO.setTimeStamp(logoEntity.getTimeStamp());

			return Optional.of(logoDTO);
		} else {
			return Optional.empty();
		}
	}

	public Optional<LogoDTO> getImageById(String branchCode) {
		if (branchCode == null || branchCode.trim().isEmpty()) {
			return Optional.empty();
		}

		BranchMasterEntity branchDetails = branchMasterRepository.findBybranchCode(branchCode);

		if (branchDetails == null) {
			return Optional.empty();
		}

		Optional<LogoEntity> logoEntityOpt = findLogoByBranchCode(branchDetails.getParentCode());

		if (!logoEntityOpt.isPresent()) {
			logoEntityOpt = findLogoByBranchCode(branchDetails.getBranchCode());
		}

		return logoEntityOpt.map(logoEntity -> {
			LogoDTO logoDTO = new LogoDTO();
			logoDTO.setId(logoEntity.getId());
			logoDTO.setImage(logoEntity.getImage());
			logoDTO.setBranchCode(logoEntity.getBranchCode());
			logoDTO.setTimeStamp(logoEntity.getTimeStamp());
			return logoDTO;
		});
	}

	private Optional<LogoEntity> findLogoByBranchCode(String branchCode) {
		if (branchCode == null || branchCode.trim().isEmpty() || "0".equals(branchCode)) {
			return Optional.empty();
		}
		return logoRepository.findByBranchCode(branchCode);
	}

	public void hitUrlForStatusSBI(String deviceSerialNumber) {
		ManualSyncLog logEntry = new ManualSyncLog();
		logEntry.setDeviceSerialNumber(deviceSerialNumber);
		logEntry.setApiUrl("http://localhost:9091/sendManualSyncSMS");
		logEntry.setRequestPayload("{\"PanelPhoneNo\":\"" + deviceSerialNumber + "\",\"MessageType\":\"SBI\"}");
		logEntry.setRequestTimestamp(LocalDateTime.now());

		try {
			DeviceMasterEntity deviceEntity = deviceMasterrepository.getByDeviceSerialNo(deviceSerialNumber);
			if (deviceEntity == null) {
				logger.error("No device found with serial number: {}", deviceSerialNumber);
				logEntry.setStatus("FAILED");
				logEntry.setResponseMessage("No device found");
				logEntry.setResponseTimestamp(LocalDateTime.now());
				manualSyncLogRepository.save(logEntry);
				return;
			}

			String panelPhoneNumber = deviceEntity.getPhoneNumber();
			String jsonInputString = "{\"PanelPhoneNo\":\"" + panelPhoneNumber + "\",\"MessageType\":\"S\"}";

			logger.info("Sending request to URL: {}", apiUrl);
			logger.info("Request payload: {}", jsonInputString);

			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(10000);
			connection.setDoOutput(true);

			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			int responseCode = connection.getResponseCode();
			logEntry.setResponseCode(responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					logEntry.setResponseMessage(response.toString());
					logEntry.setStatus("SUCCESS");
					logger.info("Response: {}", response.toString());
				}
			} else {
				logEntry.setStatus("FAILED");
				logEntry.setResponseMessage("HTTP request failed with response code " + responseCode);
				logger.error("HTTP request failed with response code {}", responseCode);
			}
			logEntry.setResponseTimestamp(LocalDateTime.now());
			manualSyncLogRepository.save(logEntry);

		} catch (SocketException e) {
			logEntry.setStatus("FAILED");
			logEntry.setResponseMessage("SocketException: Unexpected end of file from server");
			logEntry.setResponseTimestamp(LocalDateTime.now());
			manualSyncLogRepository.save(logEntry);
			logger.error("SocketException: Unexpected end of file from server", e);
		} catch (IOException e) {
			logEntry.setStatus("FAILED");
			logEntry.setResponseMessage("IOException occurred while sending request: " + e.getMessage());
			logEntry.setResponseTimestamp(LocalDateTime.now());
			manualSyncLogRepository.save(logEntry);
			logger.error("IOException occurred while sending request: {}", e.getMessage(), e);
		}
	}

	public String hitUrlForHealthCommunicatorHealth() {
		try {
			URL url = new URL(apiUrlHealth);
			System.out.println(apiUrlHealth);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(10000);
			connection.setDoOutput(true);
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					System.out.println(response.toString());
					if (!response.toString().isEmpty()) {
						logger.info("Response: {}", response.toString());
						String value = response.toString();
						int networkStatus = parseSignalStrength(value);
						String signal;
						if (networkStatus <= 9) {
							System.out.println("Network status is very poor.");
							return "POOR";
						} else if (networkStatus <= 14) {
							System.out.println("Network status is weak.");
							return "WEAK";
						} else if (networkStatus <= 19) {
							System.out.println("Network status is moderate.");
							return "MODERATE";
						} else if (networkStatus <= 31) {
							System.out.println("Network status is strong.");
							return "STRONG";
						} else {
							System.out.println("Invalid network status.");
							return "Invalid network status.";
						}
					} else {
						// logger.info("Response Not Found");
						return "STRONG";
					}

				}
			} else {

				// logger.error("HTTP request failed with response code {}", responseCode);
				return "STRONG";
			}

		} catch (SocketException e) {

			logger.error("SocketException: Unexpected end of file from server", e);
		} catch (IOException e) {

			logger.error("IOException occurred while sending request: {}", e.getMessage(), e);
		}
		return "STRONG";
	}

	private static int parseSignalStrength(String response) {
		try {
			String cleanedResponse = response.replace("AT+CSQ+CSQ: ", "").replace("OK", "").trim();
			String[] parts = cleanedResponse.split(",");
			if (parts.length > 0) {
				return Integer.parseInt(parts[0].trim());
			}
		} catch (Exception e) {
			System.err.println("Error parsing response: " + e.getMessage());
		}

		return 0;
	}
	
	public com.doritech.api.Entity.ResponseEntity getBranchBankDetails(String branchCode) {

		com.doritech.api.Entity.ResponseEntity response = new com.doritech.api.Entity.ResponseEntity();

		try {

			if (branchCode == null || branchCode.trim().isEmpty()) {
				response.setMessage("Branch code is required");
				response.setStatusCode(400);
				response.setPayload(null);
				return response;
			}

			BranchMasterEntity branch = branchMasterRepository.findBybranchCode(branchCode);

			if (branch == null) {
				response.setMessage("Branch not found");
				response.setStatusCode(404);
				response.setPayload(null);
				return response;
			}

			// Bank code from branch_master
			String bankCode = branch.getBank();

			// Bank name from bank_master
			Optional<BankMasterEntity> bankOptional = bankMasterRepository.findByBankCode(bankCode);

			String bankName = null;

			if (bankOptional.isPresent()) {
				bankName = bankOptional.get().getBankName();
			}

			Map<String, Object> result = new HashMap<>();

			result.put("branchCode", branch.getBranchCode());
			result.put("branchDesc", branch.getBranchDesc());
			result.put("bankName", bankName);

			response.setMessage("Data fetched successfully");
			response.setStatusCode(200);
			response.setPayload(result);

		} catch (Exception e) {

			e.printStackTrace();

			response.setMessage("Something went wrong");
			response.setStatusCode(500);
			response.setPayload(e.getMessage());
		}

		return response;
	}

}