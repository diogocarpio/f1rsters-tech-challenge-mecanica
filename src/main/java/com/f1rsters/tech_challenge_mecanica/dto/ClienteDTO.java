package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.validation.CpfCnpjValido;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteDTO {
    @NotBlank
    public String nome;
    @NotBlank
    @CpfCnpjValido
    public String cpfCnpj;
}