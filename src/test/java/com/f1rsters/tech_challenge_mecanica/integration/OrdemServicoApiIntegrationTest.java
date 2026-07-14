package com.f1rsters.tech_challenge_mecanica.integration;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.domain.OrdemServico;
import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import com.f1rsters.tech_challenge_mecanica.domain.Servico;
import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import com.f1rsters.tech_challenge_mecanica.repository.OrdemServicoRepository;
import com.f1rsters.tech_challenge_mecanica.repository.PecaRepository;
import com.f1rsters.tech_challenge_mecanica.repository.ServicoRepository;
import com.f1rsters.tech_challenge_mecanica.repository.VeiculoRepository;
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
import jakarta.servlet.ServletException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "test.context=ordem-servico-api")
@ActiveProfiles("test")
@ResourceLock("integration-db")
class OrdemServicoApiIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        ordemServicoRepository.deleteAll();
        veiculoRepository.deleteAll();
        pecaRepository.deleteAll();
        servicoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveCriarOrdemServicoComSucesso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente OS");
        cliente.setCpfCnpj("52998224725");
        cliente = clienteRepository.save(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setCliente(cliente);
        veiculo.setPlaca("ABC1234");
        veiculo.setMarca("VW");
        veiculo.setModelo("Gol");
        veiculo.setAno(2018);
        veiculoRepository.save(veiculo);

        Servico servico = new Servico();
        servico.setDescricao("Troca de oleo");
        servico.setValor(new BigDecimal("100.00"));
        servico = servicoRepository.save(servico);

        Peca peca = new Peca();
        peca.setDescricao("Filtro de oleo");
        peca.setQuantidadeEstoque(2);
        peca.setValorUnitario(new BigDecimal("50.00"));
        peca = pecaRepository.save(peca);

        String body = """
                {
                  "cpfCnpjCliente": "529.982.247-25",
                  "placaVeiculo": "abc-1234",
                  "servicos": [%d],
                  "pecas": [%d]
                }
                """.formatted(servico.getId(), peca.getId());

        mockMvc.perform(post("/api/admin/ordens-servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        Peca atualizada = pecaRepository.findById(peca.getId()).orElseThrow();
        assertEquals(1, atualizada.getQuantidadeEstoque());
        assertEquals(1, ordemServicoRepository.count());
    }

    @Test
    void deveRetornarErroQuandoSemEstoqueNaCriacaoDaOs() throws Exception {
        String token = login("admin@oficina.local", "admin123");

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Sem Estoque");
        cliente.setCpfCnpj("52998224725");
        cliente = clienteRepository.save(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setCliente(cliente);
        veiculo.setPlaca("AAA1A11");
        veiculoRepository.save(veiculo);

        Servico servico = new Servico();
        servico.setDescricao("Diagnostico");
        servico.setValor(new BigDecimal("80.00"));
        servico = servicoRepository.save(servico);

        Peca peca = new Peca();
        peca.setDescricao("Peca Z");
        peca.setQuantidadeEstoque(0);
        peca.setValorUnitario(new BigDecimal("20.00"));
        peca = pecaRepository.save(peca);

        String body = """
                {
                  "cpfCnpjCliente": "52998224725",
                  "placaVeiculo": "AAA1A11",
                  "servicos": [%d],
                  "pecas": [%d]
                }
                """.formatted(servico.getId(), peca.getId());

        assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/admin/ordens-servico")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .content(body))
                        .andReturn()
        );
    }

    @Test
    void endpointPublicoDeveRetornarOsMascarandoPlaca() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Publico");
        cliente.setCpfCnpj("52998224725");
        cliente = clienteRepository.save(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setCliente(cliente);
        veiculo.setPlaca("XYZ9876");
        veiculo = veiculoRepository.save(veiculo);

        Servico servico = new Servico();
        servico.setDescricao("Servico Publico");
        servico.setValor(new BigDecimal("70.00"));
        servico = servicoRepository.save(servico);

        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setServicos(List.of(servico));
        os.setPecas(List.of());
        os.setValorTotal(new BigDecimal("70.00"));
        os.setStatus(StatusOrdemServico.RECEBIDA);
        os.setCriadoEm(LocalDateTime.now());
        os = ordemServicoRepository.save(os);

        mockMvc.perform(get("/api/public/ordens-servico/" + os.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(os.getId()))
                .andExpect(jsonPath("$.placaVeiculo").value("XYZ****"));
    }

    @Test
    void endpointPublicoDeveRetornar404QuandoOsNaoExiste() throws Exception {
        mockMvc.perform(get("/api/public/ordens-servico/999999"))
                .andExpect(status().isNotFound());
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
