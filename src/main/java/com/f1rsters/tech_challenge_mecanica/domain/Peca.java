package com.f1rsters.tech_challenge_mecanica.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Peca {

    @Id
    @GeneratedValue
    private Long id;
    private String descricao;
    private int quantidadeEstoque;
    private BigDecimal valorUnitario;
}
