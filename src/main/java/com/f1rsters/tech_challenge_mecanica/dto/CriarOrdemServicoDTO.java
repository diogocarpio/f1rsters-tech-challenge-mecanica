package com.f1rsters.tech_challenge_mecanica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CriarOrdemServicoDTO {
    @NotBlank
    public String cpfCnpjCliente;
    @NotBlank
    public String placaVeiculo;

    @NotEmpty
    public List<Long> servicos; // IDs dos serviços solicitados
    public List<Long> pecas;    // IDs das peças usadas (opcional)
}