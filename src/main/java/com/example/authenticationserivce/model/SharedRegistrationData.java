package com.example.authenticationserivce.model;

import com.example.authenticationserivce.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// This model is used for login process for all user types.
@Setter
@Getter
public class SharedRegistrationData {
    @NotNull
    @NotEmpty
    @Email
    String email;
    @NotNull
    @NotEmpty
    String password;
    @NotNull
    UserType userType;
}
