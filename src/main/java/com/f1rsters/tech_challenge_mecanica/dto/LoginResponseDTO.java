package com.f1rsters.tech_challenge_mecanica.dto;

import java.util.List;

public class LoginResponseDTO {
    public String accessToken;
    public String tokenType;
    public Long expiresInSeconds;
    public List<String> roles;

    public LoginResponseDTO(String accessToken, Long expiresInSeconds, List<String> roles) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresInSeconds = expiresInSeconds;
        this.roles = roles;
    }
}

