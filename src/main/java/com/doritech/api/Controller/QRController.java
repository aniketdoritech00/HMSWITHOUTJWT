package com.doritech.api.Controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.api.Common.EncryptionDecryption;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
@RestController
public class QRController {

private final String secretKey = "doritech_consultancy@12";
	
	
	@GetMapping(value = "/generateQRCode", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> generateQRCode(
	        @RequestParam String url,
	        @RequestParam String branchCode 
	) throws WriterException, IOException {
	    int width = 300;
	    int height = 300;
	    String format = "PNG";
	    Map<EncodeHintType, Object> hints = new HashMap<>();
	    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	    hints.put(EncodeHintType.MARGIN, 0); 
	    String branchCodeEncode =  EncryptionDecryption.encrypt(branchCode, secretKey);
	    System.out.println("Encode---"+branchCodeEncode +"And Secret Key -: "+secretKey);
	    String fullUrl = url + "?data=" + branchCodeEncode;

	    QRCodeWriter writer = new QRCodeWriter();
	    BitMatrix bitMatrix = writer.encode(fullUrl, BarcodeFormat.QR_CODE, width, height, hints);

	    BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    qrImage.createGraphics();

	    Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
	    graphics.setColor(java.awt.Color.WHITE);
	    graphics.fillRect(0, 0, width, height);
	    graphics.setColor(java.awt.Color.BLACK);

	    for (int i = 0; i < width; i++) {
	        for (int j = 0; j < height; j++) {
	            if (bitMatrix.get(i, j)) {
	                graphics.fillRect(i, j, 1, 1);
	            }
	        }
	    }

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(qrImage, format, baos);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_PNG);
	    headers.setContentLength(baos.size());

	    return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
	}
	
	
	

//	 @GetMapping("/generateSecretKey")
//	    public String generateSecretKey() {
//	        return generateRandomSecretKey();
//	    }

	    // Generate a random secret key
//	    private String generateRandomSecretKey() {
//	        byte[] key = new byte[16];
//	        new SecureRandom().nextBytes(key);
//	        return Base64.getEncoder().encodeToString(key);
//	    }

	    // Endpoint for encrypting data (optional)
	    @GetMapping("encrypt")
	    public ResponseEntity<String> encryptData(@RequestParam String data, @RequestParam String secretKey) {
	        try {
	            String encryptedData = EncryptionDecryption.encrypt(data, secretKey);
	            return ResponseEntity.ok(encryptedData);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error encrypting data");
	        }
	    }

	    @GetMapping("decrypt")
	    public com.doritech.api.Entity.ResponseEntity decryptData(@RequestParam String encryptedData, @RequestParam String secretKey) {
	    com.doritech.api.Entity.ResponseEntity responseEntity=new com.doritech.api.Entity.ResponseEntity();
	    	try {
	            String decryptedData = EncryptionDecryption.decrypt(encryptedData, secretKey);
	            responseEntity.setPayload(decryptedData);
	            responseEntity.setMessage("Decrypted data"+decryptedData);
	            responseEntity.setStatusCode(HttpStatus.OK.value());
	           
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	 responseEntity.setMessage("Error Occured "+e.getLocalizedMessage());
		            responseEntity.setStatusCode(HttpStatus.OK.value());
	        }
			return responseEntity;
	    }
}
