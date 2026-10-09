package com.doritech.api.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
@Configuration
public class EncryptDecrypt {

    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String FIXED_SECRET = "S3cureK3y123456789012345678901234";

    private static void setKey(String myKey) {
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); 
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error setting key: " + e.getMessage());
        }
    }

    public static String encrypt(String strToEncrypt) {
        try {
            setKey(FIXED_SECRET);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.err.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt) {
        try {
            setKey(FIXED_SECRET);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

//    public static void main(String[] args) {
//        String originalString = "password123";
//        String encryptedString = encrypt(originalString);
//        String decryptedString = decrypt(encryptedString);
//
//        System.out.println("Original: " + originalString);
//        System.out.println("Encrypted: " + encryptedString);
//        System.out.println("Decrypted: " + decryptedString);
//
//        // Test to ensure encryption and decryption match
//        if (originalString.equals(decryptedString)) {
//            System.out.println("Encryption and decryption are successful.");
//        } else {
//            System.out.println("Encryption and decryption failed.");
//        }
//    }
}
