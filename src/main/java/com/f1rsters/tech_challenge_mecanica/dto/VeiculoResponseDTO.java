package com.f1rsters.tech_challenge_mecanica.dto;

public class VeiculoResponseDTO {
    public Long id;
    public Long clienteId;
    public String placaMascarada;
    public String marca;
    public String modelo;
    public int ano;

    public VeiculoResponseDTO(Long id, Long clienteId, String placaMascarada, String marca, String modelo, int ano) {
        this.id = id;
        this.clienteId = clienteId;
        this.placaMascarada = placaMascarada;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
    }
}

