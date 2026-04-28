package com.f1rsters.tech_challenge_mecanica.mapper;

import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class VeiculoMapperTest {

    @Test
    void shouldMapVeiculoToResponseDTO() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2022);
        veiculo.setCliente(cliente);

        VeiculoResponseDTO responseDTO = VeiculoMapper.toResponse(veiculo);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id);
        assertEquals(1L, responseDTO.clienteId);
        assertNotNull(responseDTO.placaMascarada);
        assertEquals("Toyota", responseDTO.marca);
        assertEquals("Corolla", responseDTO.modelo);
        assertEquals(2022, responseDTO.ano);
    }

    @Test
    void shouldMapVeiculoWithNullCliente() {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2022);
        veiculo.setCliente(null);

        VeiculoResponseDTO responseDTO = VeiculoMapper.toResponse(veiculo);

        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id);
        assertNull(responseDTO.clienteId);
    }
}
