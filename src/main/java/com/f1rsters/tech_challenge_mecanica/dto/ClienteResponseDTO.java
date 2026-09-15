package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.domain.StatusCliente;

public class ClienteResponseDTO {
    public Long id;
    public String nome;
    public String cpfCnpjMascarado;
    public StatusCliente status;

    public ClienteResponseDTO(Long id, String nome, String cpfCnpjMascarado, StatusCliente status) {
        this.id = id;
        this.nome = nome;
        this.cpfCnpjMascarado = cpfCnpjMascarado;
        this.status = status;
    }
}

