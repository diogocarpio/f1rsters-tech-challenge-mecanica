package com.f1rsters.tech_challenge_mecanica.mapper;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.dto.ClienteResponseDTO;
import com.f1rsters.tech_challenge_mecanica.util.SensitiveDataMasker;

public final class ClienteMapper {

    private ClienteMapper() {
    }

    public static ClienteResponseDTO toResponse(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                SensitiveDataMasker.maskCpfCnpj(cliente.getCpfCnpj())
        );
    }
}

