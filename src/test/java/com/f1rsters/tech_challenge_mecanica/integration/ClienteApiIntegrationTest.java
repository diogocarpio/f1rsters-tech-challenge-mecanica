package com.f1rsters.tech_challenge_mecanica.integration;

import com.f1rsters.tech_challenge_mecanica.domain.Role;
import com.f1rsters.tech_challenge_mecanica.domain.Usuario;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import com.f1rsters.tech_challenge_mecanica.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClienteApiIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        clienteRepository.deleteAll();
    }

    @Test
    void deveCriarClienteComTokenAdmin() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        String body = """
                {
                  "nome": "Maria Silva",
                  "cpfCnpj": "529.982.247-25"
                }
                """;

        mockMvc.perform(post("/api/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.cpfCnpjMascarado").value("***.98.***-25"));
    }

    @Test
    void deveRetornar400QuandoCpfInvalido() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        String body = """
                {
                  "nome": "Cliente Invalido",
                  "cpfCnpj": "123"
                }
                """;

        mockMvc.perform(post("/api/admin/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403ParaUsuarioSemPermissao() throws Exception {
        ensureEstoquistaUser();
        String token = login("estoquista@oficina.local", "123456");

        mockMvc.perform(get("/api/admin/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private void ensureEstoquistaUser() {
        String email = "estoquista@oficina.local";
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode("123456"));
        usuario.setAtivo(true);
        usuario.setRoles(Set.of(Role.ESTOQUISTA));
        usuarioRepository.save(usuario);
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

