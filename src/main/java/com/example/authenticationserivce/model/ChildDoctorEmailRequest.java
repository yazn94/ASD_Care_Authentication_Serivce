package com.example.authenticationserivce.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ChildDoctorEmailRequest {
    @Email
    @NotNull
    @NotEmpty
    private String childEmail;
    @Email
    @NotNull
    @NotEmpty
    private String doctorEmail;

    public String getChildEmail() {
        return childEmail;
    }

    public void setChildEmail(String childEmail) {
        this.childEmail = childEmail;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }
}
