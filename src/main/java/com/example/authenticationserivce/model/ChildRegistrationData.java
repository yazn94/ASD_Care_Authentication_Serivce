package com.example.authenticationserivce.model;

import com.example.authenticationserivce.enums.UserType;
import jakarta.validation.constraints.*;

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
    String username;
    @Email
    @NotNull
    @NotEmpty
    String parentEmail;
    @NotNull
    @Min(1)
    int age;

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

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
