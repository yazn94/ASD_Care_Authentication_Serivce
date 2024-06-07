package com.example.authenticationserivce.model;

import com.example.authenticationserivce.custom_exceptions.DateOfBirthInFutureException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChildRegistrationData {
    @Email
    @NotNull
    @NotEmpty
    String email;
    @NotNull
    @NotEmpty
    String password;
    @NotNull
    @NotEmpty
    String firstName;
    @NotNull
    @NotEmpty
    String lastName;
    @Email
    @NotNull
    @NotEmpty
    String parentEmail;
    @NotNull
    LocalDate birthDate;

    public ChildRegistrationData(String email, String password, String firstName, String lastName, String parentEmail, LocalDate birthDate) throws DateOfBirthInFutureException {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.parentEmail = parentEmail;
        setBirthDate(birthDate);
    }

    public void setBirthDate(LocalDate birthDate) throws DateOfBirthInFutureException {
        // if the birthdate is after the current date, throw an exception
        if (birthDate.isAfter(LocalDate.now())) {
            throw new DateOfBirthInFutureException("Birth date cannot be after the current date");
        }
        this.birthDate = birthDate;
    }
}
