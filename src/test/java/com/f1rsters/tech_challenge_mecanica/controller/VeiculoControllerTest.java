package com.f1rsters.tech_challenge_mecanica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoDTO;
import com.f1rsters.tech_challenge_mecanica.security.AccessDeniedHandlerImpl;
import com.f1rsters.tech_challenge_mecanica.security.AuthEntryPoint;
import com.f1rsters.tech_challenge_mecanica.security.CustomUserDetailsService;
import com.f1rsters.tech_challenge_mecanica.security.JwtAuthenticationFilter;
import com.f1rsters.tech_challenge_mecanica.security.JwtService;
import com.f1rsters.tech_challenge_mecanica.service.VeiculoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VeiculoController.class)
@AutoConfigureMockMvc(addFilters = false)
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VeiculoService veiculoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthEntryPoint authEntryPoint;

    @MockitoBean
    private AccessDeniedHandlerImpl accessDeniedHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Veiculo createVeiculo() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setAno(2022);
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

        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placaMascarada").exists())
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    void shouldListVeiculos() throws Exception {
        when(veiculoService.listAll()).thenReturn(List.of(createVeiculo()));

        mockMvc.perform(get("/api/admin/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placaMascarada").exists());
    }

    @Test
    void shouldGetVeiculoById() throws Exception {
        when(veiculoService.get(1L)).thenReturn(createVeiculo());

        mockMvc.perform(get("/api/admin/veiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placaMascarada").exists());
    }

    @Test
    void shouldUpdateVeiculo() throws Exception {
        when(veiculoService.update(eq(1L), any())).thenReturn(createVeiculo());

        mockMvc.perform(put("/api/admin/veiculos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("Corolla"));
    }

    @Test
    void shouldDeleteVeiculo() throws Exception {
        doNothing().when(veiculoService).delete(1L);

        mockMvc.perform(delete("/api/admin/veiculos/1"))
                .andExpect(status().isOk());
    }
}