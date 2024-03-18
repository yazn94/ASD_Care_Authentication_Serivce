package com.example.authenticationserivce.util;

import java.util.Random;

public class PinGenerator {
    private static final String PIN_CHARS = "0123456789";
    private static final int PIN_LENGTH = 6; // You can adjust the length of the PIN as needed

    public static String generatePin() {
        Random random = new Random();
        StringBuilder pinBuilder = new StringBuilder(PIN_LENGTH);

        for (int i = 0; i < PIN_LENGTH; i++) {
            int index = random.nextInt(PIN_CHARS.length());
            char randomChar = PIN_CHARS.charAt(index);
            pinBuilder.append(randomChar);
        }

        return pinBuilder.toString();
    }
}
