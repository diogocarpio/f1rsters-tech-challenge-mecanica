package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.validation.PlacaValida;
import jakarta.validation.constraints.NotBlank;

public class VeiculoDTO {
    public Long clienteId;
    @NotBlank
    @PlacaValida
    public String placa;
    public String marca;
    public String modelo;
    public int ano;
}