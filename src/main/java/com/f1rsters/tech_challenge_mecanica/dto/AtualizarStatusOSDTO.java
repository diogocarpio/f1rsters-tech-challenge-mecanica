package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import jakarta.validation.constraints.NotNull;

public class AtualizarStatusOSDTO {
    @NotNull
    public StatusOrdemServico novoStatus;
}