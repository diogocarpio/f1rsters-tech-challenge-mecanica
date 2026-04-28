package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.domain.OrdemServico;
import com.f1rsters.tech_challenge_mecanica.dto.AtualizarStatusOSDTO;
import com.f1rsters.tech_challenge_mecanica.dto.CriarOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.security.AccessDeniedHandlerImpl;
import com.f1rsters.tech_challenge_mecanica.security.AuthEntryPoint;
import com.f1rsters.tech_challenge_mecanica.security.CustomUserDetailsService;
import com.f1rsters.tech_challenge_mecanica.security.JwtAuthenticationFilter;
import com.f1rsters.tech_challenge_mecanica.service.OrdemServicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoController.class)
@WithMockUser(roles = "ADMIN")
class OrdemServicoControllerTest {

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

    private OrdemServico createOS() {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        return os;
    }

    private CriarOrdemServicoDTO createCriarDTO() {
        CriarOrdemServicoDTO dto = new CriarOrdemServicoDTO();
        return dto;
    }

    private AtualizarStatusOSDTO createStatusDTO() {
        AtualizarStatusOSDTO dto = new AtualizarStatusOSDTO();
        dto.novoStatus = StatusOrdemServico.valueOf("EM_ANDAMENTO");
        return dto;
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldCreateOrdemServico() throws Exception {

        when(ordemServicoService.criarOrdem(any())).thenReturn(createOS());

        mockMvc.perform(
                post("/api/admin/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCriarDTO()))
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN","MECANICO"})
    void shouldUpdateStatus() throws Exception {

        when(ordemServicoService.atualizarStatus(eq(1L), any()))
                .thenReturn(createOS());

        mockMvc.perform(
                patch("/api/admin/ordens-servico/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStatusDTO()))
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldListOrdensServico() throws Exception {

        when(ordemServicoService.listarTodas())
                .thenReturn(List.of(createOS()));

        mockMvc.perform(get("/api/admin/ordens-servico"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void shouldGetOrdemServicoById() throws Exception {

        when(ordemServicoService.detalhar(1L))
                .thenReturn(createOS());

        mockMvc.perform(get("/api/admin/ordens-servico/1"))
                .andExpect(status().isOk());
    }
}