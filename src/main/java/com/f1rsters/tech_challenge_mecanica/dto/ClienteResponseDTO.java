package com.f1rsters.tech_challenge_mecanica.dto;

public class ClienteResponseDTO {
    public Long id;
    public String nome;
    public String cpfCnpjMascarado;

    public ClienteResponseDTO(Long id, String nome, String cpfCnpjMascarado) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpjMascarado = cpfCnpjMascarado;
    }
}

