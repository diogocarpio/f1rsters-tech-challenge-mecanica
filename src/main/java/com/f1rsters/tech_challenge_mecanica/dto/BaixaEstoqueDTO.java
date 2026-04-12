package com.f1rsters.tech_challenge_mecanica.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BaixaEstoqueDTO {

    @NotNull
    public Long pecaId;
    @Min(1)
    public Integer quantidade;
}