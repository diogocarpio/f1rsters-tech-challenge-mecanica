package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;

import java.time.LocalDateTime;

public record StatusOrdemServicoDTO(
        Long id,
        StatusOrdemServico status,
        LocalDateTime atualizadoEm
) {
    public static StatusOrdemServicoDTO from(Long id, StatusOrdemServico status, LocalDateTime atualizadoEm) {
        return new StatusOrdemServicoDTO(id, status, atualizadoEm);
    }
}
