package com.f1rsters.tech_challenge_mecanica.mapper;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.dto.ClienteResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClienteMapperTest {

    @Test
    void shouldMapClienteToResponseDTO() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpfCnpj("12345678901");

        ClienteResponseDTO responseDTO = ClienteMapper.toResponse(cliente);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id);
        assertEquals("João Silva", responseDTO.nome);
        assertNotNull(responseDTO.cpfCnpjMascarado);
    }

    @Test
    void shouldMapClienteWithNullCpfCnpj() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setCpfCnpj(null);

        ClienteResponseDTO responseDTO = ClienteMapper.toResponse(cliente);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id);
        assertEquals("João Silva", responseDTO.nome);
    }
}
