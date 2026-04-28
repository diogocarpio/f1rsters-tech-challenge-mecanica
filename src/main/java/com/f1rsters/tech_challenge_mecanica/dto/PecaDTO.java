package com.f1rsters.tech_challenge_mecanica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PecaDTO {
    @NotBlank
    public String descricao;
    @NotNull
    public Integer quantidadeEstoque;
    @NotNull
    public BigDecimal valorUnitario;
}