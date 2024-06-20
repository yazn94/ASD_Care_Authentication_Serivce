package com.example.authenticationserivce.model;

import com.example.authenticationserivce.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetUsernameRequestInput {
    @NotNull
    @NotEmpty
    @Email
    private String email;
    @NotNull
    private UserType userType;
}
