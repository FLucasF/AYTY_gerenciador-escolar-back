package br.com.ufpb.GerenciadorEscolar.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Converter
@Component
public class CryptoConverter implements AttributeConverter<String, String> {

    @Value("${crypto.key}")
    private String key;

    @Value("${crypto.algorithm}")
    private String ALGORITHM;

    /**
     * Ajusta a chave para garantir que tenha exatamente 16 bytes (AES-128).
     */
    private String getValidKey() {
        if (key == null || key.isEmpty()) {
            throw new RuntimeException("A chave de criptografia não pode ser nula ou vazia.");
        }
        if (key.length() != 16) {
            throw new RuntimeException("A chave de criptografia está indevida.");
        }
        return key;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(getValidKey().getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar os dados", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(getValidKey().getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM + "/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(dbData);
            return new String(cipher.doFinal(decodedBytes));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar os dados", e);
        }
    }
}
