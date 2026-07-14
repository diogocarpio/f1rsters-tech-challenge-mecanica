package com.f1rsters.tech_challenge_mecanica;

import com.f1rsters.tech_challenge_mecanica.domain.Role;
import com.f1rsters.tech_challenge_mecanica.domain.Usuario;
import com.f1rsters.tech_challenge_mecanica.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "test.context=security-integration")
@ActiveProfiles("test")
@ResourceLock("integration-db")
class SecurityIntegrationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void publicEndpointSemTokenDeveResponder404Ou200MasNao401() throws Exception {
        mockMvc.perform(get("/api/public/ordens-servico/999999"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 401 || status == 403) {
                        throw new AssertionError("Endpoint publico nao deveria exigir autenticacao");
                    }
                });
    }

    @Test
    void adminEndpointSemTokenDeveRetornar401() throws Exception {
        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginComCredenciaisValidasDeveRetornarToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@oficina.local\",\"senha\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void usuarioSemPermissaoDeveRetornar403NoAdminClientes() throws Exception {
        Usuario usuario = new Usuario();
        String email = "estoquista+" + System.nanoTime() + "@oficina.local";
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode("123456"));
        usuario.setAtivo(true);
        usuario.setRoles(Set.of(Role.ESTOQUISTA));
        usuarioRepository.save(usuario);

        String tokenResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = tokenResponse.split("\"accessToken\":\"")[1].split("\"")[0];

        mockMvc.perform(get("/api/admin/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
