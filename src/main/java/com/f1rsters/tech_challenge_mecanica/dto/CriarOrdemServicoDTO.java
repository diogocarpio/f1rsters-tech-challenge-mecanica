package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.validation.CpfCnpjValido;
import com.f1rsters.tech_challenge_mecanica.validation.PlacaValida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class CriarOrdemServicoDTO {
    @NotBlank
    @CpfCnpjValido
    public String cpfCnpjCliente;
    @NotBlank
    @PlacaValida
    public String placaVeiculo;

    @NotEmpty
    public List<Long> servicos; // IDs dos serviços solicitados
    public List<Long> pecas;    // IDs das peças usadas (opcional)
}