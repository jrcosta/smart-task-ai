package com.smarttask.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utilitario para criptografia e descriptografia de dados sensiveis usando AES.
 * Utiliza o JWT secret como base para a chave de criptografia.
 */
@Component
@Slf4j
public class EncryptionUtil {

    /** Algoritmo simetrico utilizado nas operacoes de criptografia. */
    private static final String ALGORITHM = "AES";

    /** Quantidade de bytes utilizados na chave AES (128 bits). */
    private static final int AES_KEY_LENGTH = 16;

    /** Chave simetrica derivada do segredo JWT para operacoes AES. */
    private final SecretKey secretKey;

    /**
     * Inicializa o utilitario gerando uma chave simetrica baseada no segredo
     * JWT configurado para os tokens.
     *
     * @param jwtSecret segredo configurado para geracao de tokens
     */
    public EncryptionUtil(
            final @Value("${jwt.secret}") String jwtSecret) {
        this.secretKey = generateKey(jwtSecret);
    }

    /**
     * Gera uma chave AES a partir do JWT secret.
     *
     * @param secret valor base utilizado para derivar a chave
     * @return chave simetrica aplicavel ao algoritmo AES
     */
    private SecretKey generateKey(final String secret) {
        try {
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            final MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, AES_KEY_LENGTH);
            return new SecretKeySpec(key, ALGORITHM);
        } catch (Exception exception) {
            log.error("Erro ao gerar chave de criptografia", exception);
            throw new RuntimeException(
                    "Falha ao inicializar criptografia",
                    exception);
        }
    }

    /**
     * Criptografa uma string.
     *
     * @param data dados em texto plano
    * @return dados criptografados em Base64, ou {@code null} para entradas
    *         vazias
     */
    public String encrypt(final String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final byte[] encryptedBytes = cipher.doFinal(
                    data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception exception) {
            log.error("Erro ao criptografar dados", exception);
            throw new RuntimeException("Falha na criptografia", exception);
        }
    }

    /**
     * Descriptografa uma string.
     *
     * @param encryptedData dados criptografados em Base64
     * @return dados em texto plano, ou {@code null} para entradas vazias
     */
    public String decrypt(final String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return null;
        }

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final byte[] decodedBytes = Base64.getDecoder()
                    .decode(encryptedData);
            final byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            log.error("Erro ao descriptografar dados", exception);
            throw new RuntimeException("Falha na descriptografia", exception);
        }
    }
}
