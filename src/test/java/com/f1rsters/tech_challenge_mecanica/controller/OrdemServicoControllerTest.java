package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.domain.OrdemServico;
import com.f1rsters.tech_challenge_mecanica.dto.AtualizarStatusOSDTO;
import com.f1rsters.tech_challenge_mecanica.dto.CriarOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.NotificacaoStatusDTO;
import com.f1rsters.tech_challenge_mecanica.dto.RespostaOrcamentoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.StatusOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.security.AccessDeniedHandlerImpl;
import com.f1rsters.tech_challenge_mecanica.security.AuthEntryPoint;
import com.f1rsters.tech_challenge_mecanica.security.CustomUserDetailsService;
import com.f1rsters.tech_challenge_mecanica.security.JwtAuthenticationFilter;
import com.f1rsters.tech_challenge_mecanica.service.OrdemServicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdemServicoController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        dto.cpfCnpjCliente = "52998224725";
        dto.placaVeiculo = "ABC1234";
        dto.servicos = List.of(1L);
        return dto;
    }

    private AtualizarStatusOSDTO createStatusDTO() {
        AtualizarStatusOSDTO dto = new AtualizarStatusOSDTO();
        dto.novoStatus = StatusOrdemServico.EM_EXECUCAO;
        return dto;
    }

    @Test
    void shouldCreateOrdemServico() throws Exception {

        when(ordemServicoService.criarOrdem(any())).thenReturn(createOS());

        mockMvc.perform(
                post("/api/admin/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCriarDTO()))
        ).andExpect(status().isOk());
    }

    @Test
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
    void shouldListOrdensServico() throws Exception {

        when(ordemServicoService.listarTodas())
                .thenReturn(List.of(createOS()));

        mockMvc.perform(get("/api/admin/ordens-servico"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetOrdemServicoById() throws Exception {

        when(ordemServicoService.detalhar(1L))
                .thenReturn(createOS());

        mockMvc.perform(get("/api/admin/ordens-servico/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetOrdemServicoStatus() throws Exception {
        StatusOrdemServicoDTO statusDTO = new StatusOrdemServicoDTO(1L, StatusOrdemServico.DIAGNOSTICO, LocalDateTime.now());

        when(ordemServicoService.consultarStatus(1L))
                .thenReturn(statusDTO);

        mockMvc.perform(get("/api/admin/ordens-servico/1/status"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldResponderOrcamento() throws Exception {
        RespostaOrcamentoDTO respostaDTO = new RespostaOrcamentoDTO(true, "SISTEMA_EXTERNO", "Cliente aprovou");

        when(ordemServicoService.processarRespostaOrcamento(eq(1L), any()))
                .thenReturn(createOS());

        mockMvc.perform(
                post("/api/admin/ordens-servico/1/orcamento/resposta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(respostaDTO))
        ).andExpect(status().isOk());
    }

    @Test
    void shouldNotificarStatus() throws Exception {
        NotificacaoStatusDTO notificacaoDTO = new NotificacaoStatusDTO(StatusOrdemServico.DIAGNOSTICO, "EMAIL", "Diagnóstico concluído");

        when(ordemServicoService.processarNotificacaoStatus(eq(1L), any()))
                .thenReturn(createOS());

        mockMvc.perform(
                post("/api/admin/ordens-servico/1/status/notificacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacaoDTO))
        ).andExpect(status().isOk());
    }
}