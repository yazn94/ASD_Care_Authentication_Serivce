package com.example.authenticationserivce.custom_exceptions;

public class DoctorDoesNotExistException extends Exception {
    public DoctorDoesNotExistException(String message) {
        super(message);
    }

    public DoctorDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
