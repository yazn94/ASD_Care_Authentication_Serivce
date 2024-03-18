package com.example.authenticationserivce.model;

import com.example.authenticationserivce.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class DoctorRegistrationData {
    @Email
    @NotNull
    @NotEmpty
    String email;
    @NotNull
    @NotEmpty
    String password;
    @NotNull
    @NotEmpty
    String username;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
