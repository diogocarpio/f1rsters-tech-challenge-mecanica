package com.f1rsters.tech_challenge_mecanica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoDTO;
import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.service.VeiculoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VeiculoController.class)
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private VeiculoService veiculoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Veiculo createVeiculo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2022);
        
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        veiculo.setCliente(cliente);
        
        return veiculo;
    }

    private VeiculoDTO createDTO() {
        VeiculoDTO dto = new VeiculoDTO();
        dto.clienteId = 1L;
        dto.placa = "ABC1234";
        dto.marca = "Toyota";
        dto.modelo = "Corolla";
        dto.ano = 2022;
        return dto;
    }

    @Test
    void shouldCreateVeiculo() throws Exception {
        Veiculo veiculo = createVeiculo();

        when(veiculoService.save(any())).thenReturn(veiculo);

        mockMvc.perform(
                        post("/api/admin/veiculos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDTO()))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldListVeiculos() throws Exception {
        when(veiculoService.listAll()).thenReturn(List.of(createVeiculo()));

        mockMvc.perform(get("/api/admin/veiculos"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetVeiculoById() throws Exception {
        when(veiculoService.get(1L)).thenReturn(createVeiculo());

        mockMvc.perform(get("/api/admin/veiculos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateVeiculo() throws Exception {
        when(veiculoService.update(eq(1L), any())).thenReturn(createVeiculo());

        mockMvc.perform(
                        put("/api/admin/veiculos/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDTO()))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteVeiculo() throws Exception {
        doNothing().when(veiculoService).delete(1L);

        mockMvc.perform(delete("/api/admin/veiculos/1"))
                .andExpect(status().isOk());
    }
}
