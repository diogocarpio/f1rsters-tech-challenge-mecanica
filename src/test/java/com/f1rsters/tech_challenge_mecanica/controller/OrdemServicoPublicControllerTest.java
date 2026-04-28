package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.dto.OrdemServicoPublicDTO;
import com.f1rsters.tech_challenge_mecanica.security.AccessDeniedHandlerImpl;
import com.f1rsters.tech_challenge_mecanica.security.AuthEntryPoint;
import com.f1rsters.tech_challenge_mecanica.security.CustomUserDetailsService;
import com.f1rsters.tech_challenge_mecanica.security.JwtAuthenticationFilter;
import com.f1rsters.tech_challenge_mecanica.service.OrdemServicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoPublicController.class)
class OrdemServicoPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrdemServicoService ordemServicoService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthEntryPoint authEntryPoint;

    @MockitoBean
    private AccessDeniedHandlerImpl accessDeniedHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OrdemServicoPublicDTO createDTO() {
        OrdemServicoPublicDTO dto = new OrdemServicoPublicDTO();
        dto.id = 1L;
        dto.status = StatusOrdemServico.valueOf("EM_ANDAMENTO");
        return dto;
    }

    @Test
    void shouldReturnPublicOrdemServicoInfo() throws Exception {

        when(ordemServicoService.getPublicInfo(1L))
                .thenReturn(createDTO());

        mockMvc.perform(get("/api/public/ordens-servico/1"))
                .andExpect(status().isOk());
    }
}