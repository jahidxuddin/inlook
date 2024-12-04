package de.ju.client.auth.token;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SecureTokenStorage {
    private static final String AES = "AES";
    private static final byte[] SECRET_KEY = "1234567890123456".getBytes();

    public static Map<String, String> loadTokens(Path filePath) throws Exception {
        byte[] encryptedData = Files.readAllBytes(filePath);

        // Decrypt data
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);

        String json = new String(decryptedData);
        return TokenSerializer.deserialize(json);
    }

    public static void saveTokens(Map<String, String> tokenMap, Path filePath) throws Exception {
        String json = TokenSerializer.serialize(tokenMap);

        // Encrypt JSON
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY, AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(json.getBytes());

        Files.write(filePath, encryptedData);
    }
}
