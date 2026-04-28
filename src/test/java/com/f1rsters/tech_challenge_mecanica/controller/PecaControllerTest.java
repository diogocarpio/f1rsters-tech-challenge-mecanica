package com.f1rsters.tech_challenge_mecanica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import com.f1rsters.tech_challenge_mecanica.dto.BaixaEstoqueDTO;
import com.f1rsters.tech_challenge_mecanica.dto.PecaDTO;
import com.f1rsters.tech_challenge_mecanica.security.AccessDeniedHandlerImpl;
import com.f1rsters.tech_challenge_mecanica.security.AuthEntryPoint;
import com.f1rsters.tech_challenge_mecanica.security.CustomUserDetailsService;
import com.f1rsters.tech_challenge_mecanica.security.JwtAuthenticationFilter;
import com.f1rsters.tech_challenge_mecanica.service.PecaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PecaController.class)
@WithMockUser(roles = "ADMIN")
class PecaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PecaService service;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AuthEntryPoint authEntryPoint;

    @MockitoBean
    private AccessDeniedHandlerImpl accessDeniedHandler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveCriarPeca() throws Exception {
        Peca peca = novaPeca(1L, "Filtro", 10, new BigDecimal("35.00"));
        when(service.save(any(PecaDTO.class))).thenReturn(peca);

        mockMvc.perform(post("/api/admin/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Filtro\",\"quantidadeEstoque\":10,\"valorUnitario\":35.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Filtro"));

        verify(service).save(any(PecaDTO.class));
    }

    @Test
    void deveListarPecas() throws Exception {
        when(service.listAll()).thenReturn(List.of(
                novaPeca(1L, "Filtro", 10, new BigDecimal("35.00")),
                novaPeca(2L, "Correia", 5, new BigDecimal("59.90"))
        ));

        mockMvc.perform(get("/api/admin/pecas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].descricao").value("Correia"));

        verify(service).listAll();
    }

    @Test
    void deveObterPecaPorId() throws Exception {
        when(service.get(9L)).thenReturn(novaPeca(9L, "Bateria", 3, new BigDecimal("399.00")));

        mockMvc.perform(get("/api/admin/pecas/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(9))
                .andExpect(jsonPath("$.descricao").value("Bateria"));

        verify(service).get(9L);
    }

    @Test
    void deveAtualizarPeca() throws Exception {
        when(service.update(eq(4L), any(PecaDTO.class)))
                .thenReturn(novaPeca(4L, "Pastilha", 8, new BigDecimal("89.00")));

        mockMvc.perform(put("/api/admin/pecas/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\":\"Pastilha\",\"quantidadeEstoque\":8,\"valorUnitario\":89.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.descricao").value("Pastilha"));

        verify(service).update(eq(4L), any(PecaDTO.class));
    }

    @Test
    void deveExcluirPeca() throws Exception {
        doNothing().when(service).delete(7L);

        mockMvc.perform(delete("/api/admin/pecas/7"))
                .andExpect(status().isOk());

        verify(service).delete(7L);
    }

    @Test
    void deveListarEstoque() throws Exception {
        when(service.listAll()).thenReturn(List.of(novaPeca(3L, "Oleo", 20, new BigDecimal("29.90"))));

        mockMvc.perform(get("/api/admin/pecas/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].descricao").value("Oleo"));

        verify(service).listAll();
    }

    @Test
    void deveBaixarEstoque() throws Exception {
        Peca peca = novaPeca(5L, "Filtro", 4, new BigDecimal("35.00"));
        when(service.baixarEstoque(5L, 1)).thenReturn(peca);

        BaixaEstoqueDTO dto = new BaixaEstoqueDTO();
        dto.pecaId = 5L;
        dto.quantidade = 1;

        mockMvc.perform(post("/api/admin/pecas/baixa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.quantidadeEstoque").value(4));

        verify(service).baixarEstoque(5L, 1);
    }

    private static Peca novaPeca(Long id, String descricao, int quantidade, BigDecimal valor) {
        Peca peca = new Peca();
        peca.setId(id);
        peca.setDescricao(descricao);
        peca.setQuantidadeEstoque(quantidade);
        peca.setValorUnitario(valor);
        return peca;
    }
}

