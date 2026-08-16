package com.f1rsters.tech_challenge_mecanica.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "test.context=servico-api")
@ActiveProfiles("test")
@ResourceLock("integration-db")
class ServicoApiIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarServicoComTokenAdmin() throws Exception {
        String body = """
                {
                  "descricao": "Troca de óleo",
                  "valor": 150.00
                }
                """;

        mockMvc.perform(post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.descricao").value("Troca de óleo"))
                .andExpect(jsonPath("$.valor").value(150.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar400QuandoDescricaoNula() throws Exception {
        String body = """
                {
                  "valor": 150.00
                }
                """;

        mockMvc.perform(post("/api/admin/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarServicosComToken() throws Exception {
        mockMvc.perform(get("/api/admin/servicos"))
                .andExpect(status().isOk());
    }
}
