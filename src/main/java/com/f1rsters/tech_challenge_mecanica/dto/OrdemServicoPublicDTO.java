package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrdemServicoPublicDTO {
    public Long id;
    public StatusOrdemServico status;
    public LocalDateTime criadoEm;
    public String nomeCliente;
    public String placaVeiculo;
    public List<String> servicos;
    public List<String> pecas;
    public BigDecimal valorTotal;

    public OrdemServicoPublicDTO(Long id, StatusOrdemServico status, LocalDateTime criadoEm, String nomeCliente,
                                 String placaVeiculo, List<String> servicos, List<String> pecas, BigDecimal valorTotal) {
        this.id = id;
        this.status = status;
        this.criadoEm = criadoEm;
        this.nomeCliente = nomeCliente;
        this.placaVeiculo = placaVeiculo;
        this.servicos = servicos;
        this.pecas = pecas;
        this.valorTotal = valorTotal;
    }

    public OrdemServicoPublicDTO() {

    }
}