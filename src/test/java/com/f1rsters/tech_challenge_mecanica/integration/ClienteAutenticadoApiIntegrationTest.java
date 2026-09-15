package com.f1rsters.tech_challenge_mecanica.integration;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "test.context=cliente-autenticado-api")
@ActiveProfiles("test")
@ResourceLock("integration-db")
class ClienteAutenticadoApiIntegrationTest {

    private static final String JWT_SECRET =
            "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo0NTY3ODkwQUJDREVG";
    private static final String CPF = "52998224725";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        clienteRepository.deleteAll();
    }

    @Test
    void deveConsultarClienteComJwtEmitidoPelaLambda() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria Silva");
        cliente.setCpfCnpj(CPF);
        cliente = clienteRepository.save(cliente);

        mockMvc.perform(get("/api/clientes/me")
                        .header("Authorization", "Bearer " + generateClientToken(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cliente.getId()))
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.cpfCnpjMascarado").value("***.98.***-25"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveRetornar401SemJwtDeCliente() throws Exception {
        mockMvc.perform(get("/api/clientes/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRetornar403ParaUsuarioAdministrativo() throws Exception {
        mockMvc.perform(get("/api/clientes/me"))
                .andExpect(status().isForbidden());
    }

    private String generateClientToken(Cliente cliente) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(cliente.getCpfCnpj())
                .issuer("tech-challenge-auth-lambda")
                .audience().add("tech-challenge-api").and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(15, ChronoUnit.MINUTES)))
                .claim("clientId", cliente.getId())
                .claim("cpf", cliente.getCpfCnpj())
                .claim("nome", cliente.getNome())
                .claim("status", cliente.getStatus().name())
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET)))
                .compact();
    }
}
