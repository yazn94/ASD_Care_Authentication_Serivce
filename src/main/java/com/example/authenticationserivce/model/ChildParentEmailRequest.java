package com.example.authenticationserivce.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChildParentEmailRequest {
    @Email
    @NotNull
    @NotEmpty
    private String childEmail;

    @Email
    @NotNull
    @NotEmpty
    private String parentEmail;

}
