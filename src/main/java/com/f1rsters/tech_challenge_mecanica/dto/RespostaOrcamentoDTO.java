package com.f1rsters.tech_challenge_mecanica.dto;

import jakarta.validation.constraints.NotNull;

public record RespostaOrcamentoDTO(
        @NotNull Boolean aprovado,
        String origem,
        String observacao
) {
}
