package com.f1rsters.tech_challenge_mecanica.mapper;

import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoResponseDTO;
import com.f1rsters.tech_challenge_mecanica.util.SensitiveDataMasker;

public final class VeiculoMapper {

    private VeiculoMapper() {
    }

    public static VeiculoResponseDTO toResponse(Veiculo veiculo) {
        Long clienteId = veiculo.getCliente() != null ? veiculo.getCliente().getId() : null;

        return new VeiculoResponseDTO(
                veiculo.getId(),
                clienteId,
                SensitiveDataMasker.maskPlaca(veiculo.getPlaca()),
                veiculo.getMarca(),
                veiculo.getModelo(),
                veiculo.getAno()
        );
    }
}

