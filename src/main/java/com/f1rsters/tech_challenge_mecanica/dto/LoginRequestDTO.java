package com.f1rsters.tech_challenge_mecanica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @Email(message = "email invalido")
    @NotBlank
    public String email;

    @NotBlank
    public String senha;
}

