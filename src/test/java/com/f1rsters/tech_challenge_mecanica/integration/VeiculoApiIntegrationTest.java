package com.f1rsters.tech_challenge_mecanica.integration;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import com.f1rsters.tech_challenge_mecanica.repository.UsuarioRepository;
import com.f1rsters.tech_challenge_mecanica.repository.VeiculoRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "test.context=veiculo-api")
@ActiveProfiles("test")
@ResourceLock("integration-db")
class VeiculoApiIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private VeiculoRepository veiculoRepository;

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
        veiculoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    void deveCriarVeiculoComTokenAdmin() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setCpfCnpj("52998224725");
        cliente = clienteRepository.save(cliente);

        String body = """
                {
                  "clienteId": %d,
                  "placa": "ABC1D23",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "ano": 2020
                }
                """.formatted(cliente.getId());

        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.placaMascarada").exists())
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"));
    }

    @Test
    void deveRetornar400QuandoPlacaInvalida() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setCpfCnpj("52998224725");
        cliente = clienteRepository.save(cliente);

        String body = """
                {
                  "clienteId": %d,
                  "placa": "AB",
                  "marca": "Fiat",
                  "modelo": "Uno",
                  "ano": 2020
                }
                """.formatted(cliente.getId());

        mockMvc.perform(post("/api/admin/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarVeiculosComToken() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        mockMvc.perform(get("/api/admin/veiculos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/api/admin/veiculos"))
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
