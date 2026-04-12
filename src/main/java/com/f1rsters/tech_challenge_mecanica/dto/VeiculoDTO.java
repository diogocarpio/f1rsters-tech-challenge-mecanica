package com.f1rsters.tech_challenge_mecanica.dto;

import jakarta.validation.constraints.NotBlank;

public class VeiculoDTO {
    public Long clienteId;
    @NotBlank
    public String placa;
    public String marca;
    public String modelo;
    public int ano;
}