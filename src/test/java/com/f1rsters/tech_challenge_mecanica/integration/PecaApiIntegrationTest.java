package com.f1rsters.tech_challenge_mecanica.integration;

import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import com.f1rsters.tech_challenge_mecanica.repository.PecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "test.context=peca-api")
@ActiveProfiles("test")
@ResourceLock("integration-db")
class PecaApiIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PecaRepository pecaRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        pecaRepository.deleteAll();
    }

    @Test
    void deveCriarPecaComTokenAdmin() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        String body = """
                {
                  "descricao": "Filtro de óleo",
                  "quantidadeEstoque": 20,
                  "valorUnitario": 35.90
                }
                """;

        mockMvc.perform(post("/api/admin/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.descricao").value("Filtro de óleo"))
                .andExpect(jsonPath("$.quantidadeEstoque").value(20))
                .andExpect(jsonPath("$.valorUnitario").value(35.90));
    }

    @Test
    void deveDarBaixaNoEstoqueComSucesso() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        Peca peca = new Peca();
        peca.setDescricao("Filtro de ar");
        peca.setQuantidadeEstoque(10);
        peca.setValorUnitario(new BigDecimal("49.90"));
        peca = pecaRepository.save(peca);

        String body = """
                {
                  "pecaId": %d,
                  "quantidade": 2
                }
                """.formatted(peca.getId());

        mockMvc.perform(post("/api/admin/pecas/baixa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isOk());

        Peca atualizada = pecaRepository.findById(peca.getId()).orElseThrow();
        assertEquals(8, atualizada.getQuantidadeEstoque());
    }

    @Test
    void deveRetornarErroQuandoEstoqueInsuficiente() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        Peca peca = new Peca();
        peca.setDescricao("Peca rara");
        peca.setQuantidadeEstoque(1);
        peca.setValorUnitario(new BigDecimal("100.00"));
        peca = pecaRepository.save(peca);

        String body = """
                {
                  "pecaId": %d,
                  "quantidade": 5
                }
                """.formatted(peca.getId());

        mockMvc.perform(post("/api/admin/pecas/baixa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveConsultarEstoqueComToken() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        mockMvc.perform(get("/api/admin/pecas/estoque")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/api/admin/pecas"))
                .andExpect(status().isUnauthorized());
    }

    private String login(String email, String senha) throws Exception {
        String body = """
                {
                  "email": "%s",
                  "senha": "%s"
                }
                """.formatted(email, senha);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response.split("\"accessToken\":\"")[1].split("\"")[0];
    }
}
