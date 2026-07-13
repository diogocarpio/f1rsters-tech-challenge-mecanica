package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.domain.OrdemServico;
import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import com.f1rsters.tech_challenge_mecanica.domain.Servico;
import com.f1rsters.tech_challenge_mecanica.domain.StatusOrdemServico;
import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.dto.CriarOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.NotificacaoStatusDTO;
import com.f1rsters.tech_challenge_mecanica.dto.OrdemServicoPublicDTO;
import com.f1rsters.tech_challenge_mecanica.dto.RespostaOrcamentoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.StatusOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import com.f1rsters.tech_challenge_mecanica.repository.OrdemServicoRepository;
import com.f1rsters.tech_challenge_mecanica.repository.PecaRepository;
import com.f1rsters.tech_challenge_mecanica.repository.ServicoRepository;
import com.f1rsters.tech_challenge_mecanica.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdemServicoServiceTest {

    @Mock
    private OrdemServicoRepository repo;

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private VeiculoRepository veiculoRepo;

    @Mock
    private ServicoRepository servicoRepo;

    @Mock
    private PecaRepository pecaRepo;

    @InjectMocks
    private OrdemServicoService service;

    @Test
    void deveCriarOrdemComCalculoDeTotalEDecrementoDeEstoque() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setCpfCnpj("52998224725");
        cliente.setNome("Cliente Teste");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("ABC1234");
        veiculo.setCliente(cliente);

        Servico servico = new Servico();
        servico.setId(10L);
        servico.setDescricao("Troca de oleo");
        servico.setValor(new BigDecimal("120.00"));

        Peca peca = new Peca();
        peca.setId(20L);
        peca.setDescricao("Filtro");
        peca.setQuantidadeEstoque(2);
        peca.setValorUnitario(new BigDecimal("30.00"));

        CriarOrdemServicoDTO dto = new CriarOrdemServicoDTO();
        dto.cpfCnpjCliente = "529.982.247-25";
        dto.placaVeiculo = "abc-1234";
        dto.servicos = List.of(10L);
        dto.pecas = List.of(20L);

        when(clienteRepo.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculo));
        when(servicoRepo.findAllById(List.of(10L))).thenReturn(List.of(servico));
        when(pecaRepo.findAllById(List.of(20L))).thenReturn(List.of(peca));
        when(repo.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico os = service.criarOrdem(dto);

        assertEquals(StatusOrdemServico.RECEBIDA, os.getStatus());
        assertEquals(new BigDecimal("150.00"), os.getValorTotal());
        assertEquals(1, peca.getQuantidadeEstoque());
        assertNotNull(os.getCriadoEm());
        verify(pecaRepo, times(1)).save(peca);
        verify(repo, times(1)).save(any(OrdemServico.class));
    }

    @Test
    void deveFalharQuandoClienteNaoExiste() {
        CriarOrdemServicoDTO dto = new CriarOrdemServicoDTO();
        dto.cpfCnpjCliente = "529.982.247-25";
        dto.placaVeiculo = "ABC1234";
        dto.servicos = List.of(1L);

        when(clienteRepo.findByCpfCnpj("52998224725")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.criarOrdem(dto));
        verify(repo, never()).save(any(OrdemServico.class));
    }

    @Test
    void deveFalharQuandoVeiculoNaoExiste() {
        Cliente cliente = new Cliente();
        cliente.setCpfCnpj("52998224725");

        CriarOrdemServicoDTO dto = new CriarOrdemServicoDTO();
        dto.cpfCnpjCliente = "529.982.247-25";
        dto.placaVeiculo = "ABC1234";
        dto.servicos = List.of(1L);

        when(clienteRepo.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findByPlaca("ABC1234")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.criarOrdem(dto));
        assertTrue(ex.getMessage().contains("Veículo não encontrado"));
        verify(repo, never()).save(any(OrdemServico.class));
    }

    @Test
    void deveCriarOrdemSemPecasQuandoListaNula() {
        Cliente cliente = new Cliente();
        cliente.setCpfCnpj("52998224725");

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");

        Servico servico = new Servico();
        servico.setValor(new BigDecimal("50.00"));

        CriarOrdemServicoDTO dto = new CriarOrdemServicoDTO();
        dto.cpfCnpjCliente = "52998224725";
        dto.placaVeiculo = "ABC1234";
        dto.servicos = List.of(1L);
        dto.pecas = null;

        when(clienteRepo.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculo));
        when(servicoRepo.findAllById(List.of(1L))).thenReturn(List.of(servico));
        when(repo.save(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServico os = service.criarOrdem(dto);

        assertEquals(new BigDecimal("50.00"), os.getValorTotal());
        assertEquals(0, os.getPecas().size());
        verify(pecaRepo, never()).findAllById(any());
    }

    @Test
    void deveFalharQuandoPecaSemEstoque() {
        Cliente cliente = new Cliente();
        cliente.setCpfCnpj("52998224725");

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");

        Servico servico = new Servico();
        servico.setId(1L);
        servico.setValor(new BigDecimal("100.00"));

        Peca peca = new Peca();
        peca.setId(2L);
        peca.setDescricao("Filtro");
        peca.setQuantidadeEstoque(0);
        peca.setValorUnitario(new BigDecimal("10.00"));

        CriarOrdemServicoDTO dto = new CriarOrdemServicoDTO();
        dto.cpfCnpjCliente = "529.982.247-25";
        dto.placaVeiculo = "ABC1234";
        dto.servicos = List.of(1L);
        dto.pecas = List.of(2L);

        when(clienteRepo.findByCpfCnpj("52998224725")).thenReturn(Optional.of(cliente));
        when(veiculoRepo.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculo));
        when(servicoRepo.findAllById(List.of(1L))).thenReturn(List.of(servico));
        when(pecaRepo.findAllById(List.of(2L))).thenReturn(List.of(peca));

        assertThrows(RuntimeException.class, () -> service.criarOrdem(dto));
        verify(repo, never()).save(any(OrdemServico.class));
    }

    @Test
    void getPublicInfoDeveRetornar404QuandoOsNaoExiste() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.getPublicInfo(999L));
    }

    @Test
    void getPublicInfoDeveRetornarDadosMascarados() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");

        Servico servico = new Servico();
        servico.setDescricao("Troca de oleo");

        Peca peca = new Peca();
        peca.setDescricao("Filtro");

        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setServicos(List.of(servico));
        os.setPecas(List.of(peca));
        os.setStatus(StatusOrdemServico.RECEBIDA);
        os.setValorTotal(new BigDecimal("99.90"));

        when(repo.findById(1L)).thenReturn(Optional.of(os));

        OrdemServicoPublicDTO dto = service.getPublicInfo(1L);

        assertEquals("ABC****", dto.placaVeiculo);
        assertEquals("Maria", dto.nomeCliente);
        assertEquals(List.of("Troca de oleo"), dto.servicos);
        assertEquals(List.of("Filtro"), dto.pecas);
    }

    @Test
    void getPublicInfoDeveRetornarListaVaziaQuandoPecasNulas() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria");

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");

        Servico servico = new Servico();
        servico.setDescricao("Troca de oleo");

        OrdemServico os = new OrdemServico();
        os.setId(2L);
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setServicos(List.of(servico));
        os.setPecas(null);
        os.setStatus(StatusOrdemServico.RECEBIDA);
        os.setValorTotal(new BigDecimal("99.90"));

        when(repo.findById(2L)).thenReturn(Optional.of(os));

        OrdemServicoPublicDTO dto = service.getPublicInfo(2L);

        assertEquals(List.of(), dto.pecas);
    }

    @Test
    void deveAtualizarStatusDaOs() {
        OrdemServico os = new OrdemServico();
        os.setStatus(StatusOrdemServico.RECEBIDA);

        when(repo.findById(1L)).thenReturn(Optional.of(os));
        when(repo.save(os)).thenReturn(os);

        OrdemServico atualizada = service.atualizarStatus(1L, StatusOrdemServico.EM_EXECUCAO);

        assertSame(os, atualizada);
        assertEquals(StatusOrdemServico.EM_EXECUCAO, atualizada.getStatus());
    }

    @Test
    void deveFalharAoAtualizarStatusQuandoOsNaoExiste() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.atualizarStatus(1L, StatusOrdemServico.ENTREGUE));
        assertTrue(ex.getMessage().contains("OS não encontrada"));
    }

    @Test
    void deveListarTodasAsOrdens() {
        when(repo.findAllActiveOrderByStatusAndDate()).thenReturn(List.of(new OrdemServico(), new OrdemServico()));

        List<OrdemServico> ordens = service.listarTodas();

        assertEquals(2, ordens.size());
    }

    @Test
    void deveDetalharOrdemPorId() {
        OrdemServico os = new OrdemServico();
        when(repo.findById(20L)).thenReturn(Optional.of(os));

        assertSame(os, service.detalhar(20L));
    }

    @Test
    void deveFalharAoDetalharQuandoNaoExiste() {
        when(repo.findById(20L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.detalhar(20L));
        assertTrue(ex.getMessage().contains("OS não encontrada"));
    }

    @Test
    void deveConsultarStatusDaOS() {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setStatus(StatusOrdemServico.DIAGNOSTICO);
        os.setCriadoEm(LocalDateTime.now());

        when(repo.findById(1L)).thenReturn(Optional.of(os));

        StatusOrdemServicoDTO dto = service.consultarStatus(1L);

        assertEquals(1L, dto.id());
        assertEquals(StatusOrdemServico.DIAGNOSTICO, dto.status());
        assertNotNull(dto.atualizadoEm());
    }

    @Test
    void deveFalharAoConsultarStatusQuandoOSNaoExiste() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.consultarStatus(999L));
        assertTrue(ex.getMessage().contains("OS não encontrada"));
    }

    @Test
    void deveAprovarOrcamentoEMoverParaEmExecucao() {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);

        RespostaOrcamentoDTO dto = new RespostaOrcamentoDTO(true, "SISTEMA_EXTERNO", "Cliente aprovou");

        when(repo.findById(1L)).thenReturn(Optional.of(os));
        when(repo.save(os)).thenReturn(os);

        OrdemServico resultado = service.processarRespostaOrcamento(1L, dto);

        assertEquals(StatusOrdemServico.EM_EXECUCAO, resultado.getStatus());
    }

    @Test
    void deveRecusarOrcamentoEManterStatus() {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);

        RespostaOrcamentoDTO dto = new RespostaOrcamentoDTO(false, "SISTEMA_EXTERNO", "Cliente recusou");

        when(repo.findById(1L)).thenReturn(Optional.of(os));
        when(repo.save(os)).thenReturn(os);

        OrdemServico resultado = service.processarRespostaOrcamento(1L, dto);

        assertEquals(StatusOrdemServico.AGUARDANDO_APROVACAO, resultado.getStatus());
    }

    @Test
    void deveFalharAoResponderOrcamentoQuandoStatusNaoEhAguardandoAprovacao() {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setStatus(StatusOrdemServico.RECEBIDA);

        RespostaOrcamentoDTO dto = new RespostaOrcamentoDTO(true, "SISTEMA_EXTERNO", "Aprovação");

        when(repo.findById(1L)).thenReturn(Optional.of(os));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.processarRespostaOrcamento(1L, dto));
        assertTrue(ex.getMessage().contains("não está aguardando aprovação"));
    }

    @Test
    void deveProcessarNotificacaoDeStatus() {
        OrdemServico os = new OrdemServico();
        os.setId(1L);
        os.setStatus(StatusOrdemServico.RECEBIDA);

        NotificacaoStatusDTO dto = new NotificacaoStatusDTO(StatusOrdemServico.DIAGNOSTICO, "EMAIL", "Diagnóstico concluído");

        when(repo.findById(1L)).thenReturn(Optional.of(os));
        when(repo.save(os)).thenReturn(os);

        OrdemServico resultado = service.processarNotificacaoStatus(1L, dto);

        assertEquals(StatusOrdemServico.DIAGNOSTICO, resultado.getStatus());
    }

    @Test
    void deveFalharAoProcessarNotificacaoQuandoOSNaoExiste() {
        NotificacaoStatusDTO dto = new NotificacaoStatusDTO(StatusOrdemServico.DIAGNOSTICO, "EMAIL", "Diagnóstico");

        when(repo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.processarNotificacaoStatus(999L, dto));
        assertTrue(ex.getMessage().contains("OS não encontrada"));
    }
}

