package com.f1rsters.tech_challenge_mecanica.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Servico {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String descricao;
    @NotNull
    private BigDecimal valor;
}
