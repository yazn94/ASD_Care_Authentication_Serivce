package com.example.authenticationserivce.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ChildParentEmailRequest {
    @Email
    @NotNull
    @NotEmpty
    private String childEmail;

    @Email
    @NotNull
    @NotEmpty
    private String parentEmail;

    public String getChildEmail() {
        return childEmail;
    }

    public void setChildEmail(String childEmail) {
        this.childEmail = childEmail;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }
}
