package com.example.authenticationserivce.model;

import com.example.authenticationserivce.util.StringOperations;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class EmailRequest {
    @Email
    @NotNull
    @NotEmpty
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
