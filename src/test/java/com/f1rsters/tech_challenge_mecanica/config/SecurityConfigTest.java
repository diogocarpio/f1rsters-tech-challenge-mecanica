package com.f1rsters.tech_challenge_mecanica.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // =========================
    // BEANS
    // =========================

    @Test
    void shouldCreatePasswordEncoder() {
        assertThat(passwordEncoder).isNotNull();

        String encoded = passwordEncoder.encode("123456");

        assertThat(encoded).isNotEqualTo("123456");
        assertThat(passwordEncoder.matches("123456", encoded)).isTrue();
    }

    @Test
    void shouldCreateAuthenticationManager() {
        assertThat(authenticationManager).isNotNull();
    }

    @Test
    void shouldCreateAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(null);
        provider.setPasswordEncoder(passwordEncoder);
        assertThat(provider).isNotNull();
    }

    // =========================
    // PUBLIC ENDPOINTS
    // =========================

    @Test
    void shouldAllowPublicEndpoints() throws Exception {

        mockMvc.perform(get("/api/public/test"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAllowAuthEndpoints() throws Exception {

        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isNotFound());
    }

    // =========================
    // AUTH REQUIRED
    // =========================

    @Test
    void shouldBlockAdminEndpointsWithoutAuthentication() throws Exception {

        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isUnauthorized());
    }

    // =========================
    // ADMIN ROLE
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldAccessAdminEndpoints() throws Exception {

        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isNotFound());
    }

    // =========================
    // MECANICO ROLE
    // =========================

    @Test
    @WithMockUser(roles = "MECANICO")
    void mecanicoShouldUpdateOrdemStatus() throws Exception {

        mockMvc.perform(
                patch("/api/admin/ordens-servico/1/status")
                        .with(csrf())
        ).andExpect(status().isNotFound());
    }

    // =========================
    // ESTOQUISTA ROLE
    // =========================

    @Test
    @WithMockUser(roles = "ESTOQUISTA")
    void estoquistaShouldAccessPecas() throws Exception {

        mockMvc.perform(get("/api/admin/pecas"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ESTOQUISTA")
    void estoquistaShouldCreatePeca() throws Exception {

        mockMvc.perform(
                post("/api/admin/pecas")
                        .with(csrf())
        ).andExpect(status().isNotFound());
    }

    // =========================
    // ATENDENTE ROLE
    // =========================

    @Test
    @WithMockUser(roles = "ATENDENTE")
    void atendenteShouldAccessClientes() throws Exception {

        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    void atendenteShouldCreateCliente() throws Exception {

        mockMvc.perform(
                post("/api/admin/clientes")
                        .with(csrf())
        ).andExpect(status().isNotFound());
    }

    // =========================
    // ACCESS DENIED
    // =========================

    @Test
    @WithMockUser(roles = "ATENDENTE")
    void atendenteShouldNotDeletePeca() throws Exception {

        mockMvc.perform(
                delete("/api/admin/pecas/1")
                        .with(csrf())
        ).andExpect(status().isForbidden());
    }

}