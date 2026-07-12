package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.*;
import com.f1rsters.tech_challenge_mecanica.dto.CriarOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.NotificacaoStatusDTO;
import com.f1rsters.tech_challenge_mecanica.dto.OrdemServicoPublicDTO;
import com.f1rsters.tech_challenge_mecanica.dto.RespostaOrcamentoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.StatusOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.repository.*;
import com.f1rsters.tech_challenge_mecanica.util.InputNormalizer;
import com.f1rsters.tech_challenge_mecanica.util.SensitiveDataMasker;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdemServicoService {
    private final OrdemServicoRepository repo;
    private final ClienteRepository clienteRepo;
    private final VeiculoRepository veiculoRepo;
    private final ServicoRepository servicoRepo;
    private final PecaRepository pecaRepo;

    public OrdemServicoService(OrdemServicoRepository repo,
                               ClienteRepository clienteRepo,
                               VeiculoRepository veiculoRepo,
                               ServicoRepository servicoRepo,
                               PecaRepository pecaRepo) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
        this.veiculoRepo = veiculoRepo;
        this.servicoRepo = servicoRepo;
        this.pecaRepo = pecaRepo;
    }

    @Transactional
    public OrdemServico criarOrdem(CriarOrdemServicoDTO dto) {
        String cpfCnpjNormalizado = InputNormalizer.normalizeCpfCnpj(dto.cpfCnpjCliente);
        String placaNormalizada = InputNormalizer.normalizePlaca(dto.placaVeiculo);

        // 1. Buscar cliente por CPF/CNPJ
        Cliente cliente = clienteRepo.findByCpfCnpj(cpfCnpjNormalizado)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // 2. Buscar veículo por placa
        Veiculo veiculo = veiculoRepo.findByPlaca(placaNormalizada)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        // 3. Buscar serviços
        List<Servico> servicos = servicoRepo.findAllById(dto.servicos);

        // 4. Buscar peças
        List<Peca> pecas = dto.pecas != null ? pecaRepo.findAllById(dto.pecas) : List.of();

        // 5. Validar e descontar estoque das peças
        for (Peca pecaEmUso : pecas) {
            if (pecaEmUso.getQuantidadeEstoque() < 1) {
                throw new RuntimeException("Sem estoque suficiente da peça: " + pecaEmUso.getDescricao());
            }
            // Desconta 1 unidade
            pecaEmUso.setQuantidadeEstoque(pecaEmUso.getQuantidadeEstoque() - 1);
            pecaRepo.save(pecaEmUso);
        }

        // 6. Calcular valor total
        BigDecimal totalServicos = servicos.stream().map(Servico::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPecas = pecas.stream().map(Peca::getValorUnitario).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorTotal = totalServicos.add(totalPecas);

        // 7. Construir OS
        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setServicos(servicos);
        os.setPecas(pecas);
        os.setValorTotal(valorTotal);
        os.setStatus(StatusOrdemServico.RECEBIDA);
        os.setCriadoEm(LocalDateTime.now());

        return repo.save(os);
    }

    public OrdemServico atualizarStatus(Long id, StatusOrdemServico novoStatus) {
        OrdemServico os = repo.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
        os.setStatus(novoStatus);
        return repo.save(os);
    }

    public List<OrdemServico> listarTodas() {
        return repo.findAllActiveOrderByStatusAndDate();
    }

    public OrdemServico detalhar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    public StatusOrdemServicoDTO consultarStatus(Long id) {
        OrdemServico os = repo.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
        return StatusOrdemServicoDTO.from(os.getId(), os.getStatus(), os.getCriadoEm());
    }

    @Transactional
    public OrdemServico processarRespostaOrcamento(Long id, RespostaOrcamentoDTO dto) {
        OrdemServico os = repo.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
        
        if (os.getStatus() != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new RuntimeException("OS não está aguardando aprovação");
        }
        
        if (dto.aprovado()) {
            os.setStatus(StatusOrdemServico.EM_EXECUCAO);
        } else {
            // Em caso de recusa, manter o status atual mas registrar a decisão
            // Para simplificar, vamos manter AGUARDANDO_APROVACAO
        }
        
        return repo.save(os);
    }

    @Transactional
    public OrdemServico processarNotificacaoStatus(Long id, NotificacaoStatusDTO dto) {
        OrdemServico os = repo.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
        
        // Registrar status anterior para rastreabilidade
        StatusOrdemServico statusAnterior = os.getStatus();
        
        // Atualizar status
        os.setStatus(dto.novoStatus());
        
        return repo.save(os);
    }

    public OrdemServicoPublicDTO getPublicInfo(Long id) {
        OrdemServico os = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem de Servico nao encontrada"));
        return new OrdemServicoPublicDTO(
                os.getId(),
                os.getStatus(),
                os.getCriadoEm(),
                os.getCliente().getNome(),
                SensitiveDataMasker.maskPlaca(os.getVeiculo().getPlaca()),
                os.getServicos().stream().map(Servico::getDescricao).toList(),
                os.getPecas() != null ? os.getPecas().stream().map(Peca::getDescricao).toList() : List.of(),
                os.getValorTotal()
        );
    }
}