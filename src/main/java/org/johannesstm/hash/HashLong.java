package org.johannesstm.hash;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class HashLong {

    public HashLong() {
    }

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_MODE = "AES/ECB/PKCS5Padding";
    private static final int AES_KEY_SIZE = 128;

    public static String encryptLong(long value, String encryptionKey) {
        try {
            // Convert the long value to a string
            String valueStr = String.valueOf(value);

            // Create a SecretKeySpec object with the encryption key
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);

            // Create a Cipher object with the AES algorithm and ECB mode
            Cipher cipher = Cipher.getInstance(AES_MODE);

            // Initialize the cipher in encryption mode with the key
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            // Encrypt the value string
            byte[] encryptedBytes = cipher.doFinal(valueStr.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted bytes as a Base64 string
            String encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes);

            return encryptedValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long decryptLong(String encryptedValue, String encryptionKey) {
        try {
            // Decode the encrypted value from Base64 string
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedValue);

            // Create a SecretKeySpec object with the encryption key
            SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);

            // Create a Cipher object with the AES algorithm and ECB mode
            Cipher cipher = Cipher.getInstance(AES_MODE);

            // Initialize the cipher in decryption mode with the key
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // Decrypt the encrypted bytes
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert the decrypted bytes to a long value
            String decryptedValueStr = new String(decryptedBytes, StandardCharsets.UTF_8);
            long decryptedValue = Long.parseLong(decryptedValueStr);

            return decryptedValue;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return a default value to indicate decryption failure
        }
    }


}