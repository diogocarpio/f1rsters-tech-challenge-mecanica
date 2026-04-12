package com.f1rsters.tech_challenge_mecanica.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class OrdemServico {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Cliente cliente;
    @ManyToOne
    private Veiculo veiculo;

    @ManyToMany
    private List<Servico> servicos; // Serviços requisitados
    @ManyToMany
    private List<Peca> pecas;
    private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    private StatusOrdemServico status;
    private LocalDateTime criadoEm;
}
