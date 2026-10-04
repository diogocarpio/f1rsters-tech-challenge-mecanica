package com.f1rsters.tech_challenge_mecanica.dto;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrdemServicoPublicDTOTest {

    @Test
    void shouldCreateWithEmptyConstructor() {
        OrdemServicoPublicDTO dto = new OrdemServicoPublicDTO();

        assertNotNull(dto);
        assertNull(dto.id);
        assertNull(dto.status);
        assertNull(dto.criadoEm);
        assertNull(dto.nomeCliente);
        assertNull(dto.placaVeiculo);
        assertNull(dto.servicos);
        assertNull(dto.pecas);
        assertNull(dto.valorTotal);
    }

    @Test
    void shouldCreateWithFullConstructor() {
        LocalDateTime now = LocalDateTime.now();
        OrdemServicoPublicDTO dto = new OrdemServicoPublicDTO(
                1L,
                StatusOrdemServico.RECEBIDA,
                now,
                "Cliente Teste",
                "ABC1234",
                List.of("Troca de óleo"),
                List.of("Filtro"),
                new BigDecimal("150.00")
        );

        assertEquals(1L, dto.id);
        assertEquals(StatusOrdemServico.RECEBIDA, dto.status);
        assertEquals(now, dto.criadoEm);
        assertEquals("Cliente Teste", dto.nomeCliente);
        assertEquals("ABC1234", dto.placaVeiculo);
        assertEquals(List.of("Troca de óleo"), dto.servicos);
        assertEquals(List.of("Filtro"), dto.pecas);
        assertEquals(new BigDecimal("150.00"), dto.valorTotal);
    }
}
