package com.example.authenticationserivce.model;

import com.example.authenticationserivce.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class SharedRegistrationData {
    @NotNull
    @NotEmpty
    @Email
    String email;
    @NotNull
    @NotEmpty
    String password;
    @NotNull
    @NotEmpty
    UserType userType;


    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

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
}
