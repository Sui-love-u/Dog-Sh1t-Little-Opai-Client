package net.minecraft.client;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.prefs.Preferences;

public class SecurityUtil {
    private static final String CONFIG_KEY = "aes.key";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static String aesKey = "!MySecretKey123!";

    static {
        Preferences prefs = Preferences.userRoot().node(SecurityUtil.class.getName());
        String savedKey = prefs.get(CONFIG_KEY, null);
        if (savedKey != null) {
            aesKey = savedKey;
        }
    }

    public static void setAesKey(String key) {
        if (key != null && key.length() >= 16) {
            aesKey = key;
            Preferences prefs = Preferences.userRoot().node(SecurityUtil.class.getName());
            prefs.put(CONFIG_KEY, key);
        }
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    public static String hashWithSalt(String data, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
    public static String encryptAES(String data) {
        try {
            byte[] keyBytes = new byte[16];
            byte[] keyData = aesKey.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(keyData, 0, keyBytes, 0, Math.min(keyData.length, keyBytes.length));

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decryptAES(String encryptedData) {
        try {
            byte[] keyBytes = new byte[16];
            byte[] keyData = aesKey.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(keyData, 0, keyBytes, 0, Math.min(keyData.length, keyBytes.length));

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}