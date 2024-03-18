package com.example.authenticationserivce.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SaltGenerator {
    private static final int SALT_LENGTH = 16; // Length of the salt in bytes

    public static String generateSalt() {
        // Create a byte array to hold the salt value
        byte[] salt = new byte[SALT_LENGTH];

        // Generate random bytes for the salt
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        // Encode the salt bytes to Base64 string for storage
        return Base64.getEncoder().encodeToString(salt);
    }
}
