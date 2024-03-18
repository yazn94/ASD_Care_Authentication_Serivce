package com.example.authenticationserivce.model;

import com.example.authenticationserivce.util.StringOperations;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class TokenRequest {
    @NotNull
    @NotEmpty
    String token;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }
}
