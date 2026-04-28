package com.f1rsters.tech_challenge_mecanica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.domain.Servico;
import com.f1rsters.tech_challenge_mecanica.dto.ServicoDTO;
import com.f1rsters.tech_challenge_mecanica.security.AccessDeniedHandlerImpl;
import com.f1rsters.tech_challenge_mecanica.security.AuthEntryPoint;
import com.f1rsters.tech_challenge_mecanica.security.CustomUserDetailsService;
import com.f1rsters.tech_challenge_mecanica.security.JwtAuthenticationFilter;
import com.f1rsters.tech_challenge_mecanica.service.ServicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServicoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicoService servicoService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthEntryPoint authEntryPoint;

    @MockitoBean
    private AccessDeniedHandlerImpl accessDeniedHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Servico createServico() {
        Servico servico = new Servico();
        servico.setId(1L);
        servico.setDescricao("Troca de óleo");
        return servico;
    }

    private ServicoDTO createDTO() {
        ServicoDTO dto = new ServicoDTO();
        dto.descricao = "Troca de óleo";
        dto.valor = new BigDecimal("150.00");
        return dto;
    }

    @Test
    void shouldCreateServico() throws Exception {

        when(servicoService.save(any())).thenReturn(createServico());

        mockMvc.perform(
                post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO()))
        ).andExpect(status().isOk());
    }

    @Test
    void shouldListServicos() throws Exception {

        when(servicoService.listAll()).thenReturn(List.of(createServico()));

        mockMvc.perform(get("/api/admin/servicos"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetServicoById() throws Exception {

        when(servicoService.get(1L)).thenReturn(createServico());

        mockMvc.perform(get("/api/admin/servicos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateServico() throws Exception {

        when(servicoService.update(eq(1L), any())).thenReturn(createServico());

        mockMvc.perform(
                put("/api/admin/servicos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO()))
        ).andExpect(status().isOk());
    }

    @Test
    void shouldDeleteServico() throws Exception {

        doNothing().when(servicoService).delete(1L);

        mockMvc.perform(delete("/api/admin/servicos/1"))
                .andExpect(status().isOk());
    }
}