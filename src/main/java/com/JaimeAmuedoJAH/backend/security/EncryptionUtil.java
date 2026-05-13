package com.JaimeAmuedoJAH.backend.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utility class for encrypting and decrypting sensitive data like card numbers and CVV.
 * Uses AES-256 encryption with a configurable secret key.
 */
@Component
@Slf4j
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    private final SecretKey secretKey;

    /**
     * Constructor that initializes the encryption key from application.properties
     * The key should be a 32-character string (256-bit key in Base64)
     *
     * @param encryptionKey The encryption key from configuration
     */
    public EncryptionUtil(@Value("${encryption.key}") String encryptionKey) {
        try {
            // Decode the Base64 key from configuration
            byte[] decodedKey = Base64.getDecoder().decode(encryptionKey);
            
            // Validate key size (must be 32 bytes for AES-256)
            if (decodedKey.length != 32) {
                throw new IllegalArgumentException("Encryption key must be 256-bit (32 bytes)");
            }
            
            // Create the secret key
            this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
            log.info("Encryption key initialized successfully");
        } catch (IllegalArgumentException e) {
            log.error("Failed to initialize encryption key: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Encrypt a plaintext string
     *
     * @param plaintext The data to encrypt
     * @return Encrypted data encoded in Base64
     */
    public String encrypt(String plaintext) {
        try {
            if (plaintext == null || plaintext.isEmpty()) {
                return plaintext;
            }

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedData = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            log.error("Error encrypting data: {}", e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt an encrypted Base64-encoded string
     *
     * @param encryptedData The Base64-encoded encrypted data
     * @return Decrypted plaintext
     */
    public String decrypt(String encryptedData) {
        try {
            if (encryptedData == null || encryptedData.isEmpty()) {
                return encryptedData;
            }

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData);
        } catch (Exception e) {
            log.error("Error decrypting data: {}", e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Generate a random AES-256 key for configuration
     * This can be used to generate the initial key for application.properties
     *
     * @return Base64-encoded 256-bit key
     */
    public static String generateEncryptionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            SecretKey key = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            log.error("Error generating encryption key: {}", e.getMessage());
            throw new RuntimeException("Key generation failed", e);
        }
    }
}
