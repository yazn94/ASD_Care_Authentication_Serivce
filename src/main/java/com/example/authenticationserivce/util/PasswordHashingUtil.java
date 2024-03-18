package com.example.authenticationserivce.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHashingUtil {
    public static String hashPassword(String password, String salt) {
        int iterations = 10000; // Number of iterations
        int keyLength = 256; // Key length in bits
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = Base64.getDecoder().decode(salt); // Decode salt from Base64 string
        KeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, keyLength);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedBytes = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace(); // Handle error appropriately
            return null;
        }
    }
}
