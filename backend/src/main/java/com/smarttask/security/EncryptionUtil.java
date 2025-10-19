package com.smarttask.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utilitário para criptografia e descriptografia de dados sensíveis usando AES.
 * Utiliza o JWT secret como base para a chave de criptografia.
 */
@Component
@Slf4j
public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private final SecretKey secretKey;

    public EncryptionUtil(@Value("${jwt.secret}") String jwtSecret) {
        this.secretKey = generateKey(jwtSecret);
    }

    /**
     * Gera uma chave AES a partir do JWT secret.
     */
    private SecretKey generateKey(String secret) {
        try {
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // Usa apenas os primeiros 128 bits
            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            log.error("Erro ao gerar chave de criptografia", e);
            throw new RuntimeException("Falha ao inicializar criptografia", e);
        }
    }

    /**
     * Criptografa uma string.
     * 
     * @param data Dados em texto plano
     * @return Dados criptografados em Base64, ou null se data for null/vazio
     */
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Erro ao criptografar dados", e);
            throw new RuntimeException("Falha na criptografia", e);
        }
    }

    /**
     * Descriptografa uma string.
     * 
     * @param encryptedData Dados criptografados em Base64
     * @return Dados em texto plano, ou null se encryptedData for null/vazio
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return null;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Erro ao descriptografar dados", e);
            throw new RuntimeException("Falha na descriptografia", e);
        }
    }
}
