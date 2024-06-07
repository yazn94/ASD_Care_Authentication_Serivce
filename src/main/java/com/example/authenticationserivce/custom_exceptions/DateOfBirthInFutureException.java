package com.example.authenticationserivce.custom_exceptions;

public class DateOfBirthInFutureException extends Exception {
    public DateOfBirthInFutureException(String message) {
        super(message);
    }

    public DateOfBirthInFutureException(String message, Throwable cause) {
        super(message, cause);
    }
}
