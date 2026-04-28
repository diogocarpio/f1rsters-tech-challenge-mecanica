package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.dto.OrdemServicoPublicDTO;
import com.f1rsters.tech_challenge_mecanica.service.OrdemServicoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoPublicController.class)
class OrdemServicoPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private OrdemServicoService ordemServicoService;

    @Autowired
    private ObjectMapper objectMapper;

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