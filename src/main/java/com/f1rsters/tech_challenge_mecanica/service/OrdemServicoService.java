package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.*;
import com.f1rsters.tech_challenge_mecanica.dto.CriarOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.OrdemServicoPublicDTO;
import com.f1rsters.tech_challenge_mecanica.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 1. Buscar cliente por CPF/CNPJ
        Cliente cliente = clienteRepo.findByCpfCnpj(dto.cpfCnpjCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // 2. Buscar ou criar veículo por placa
        Veiculo veiculo = veiculoRepo.findByPlaca(dto.placaVeiculo)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        // 3. Buscar serviços
        List<Servico> servicos = servicoRepo.findAllById(dto.servicos);

        // 4. Buscar peças (opcional)
        List<Peca> pecas = dto.pecas != null ? pecaRepo.findAllById(dto.pecas) : List.of();

        // 5. Calcular valor total
        BigDecimal totalServicos = servicos.stream().map(Servico::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPecas = pecas.stream().map(Peca::getValorUnitario).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorTotal = totalServicos.add(totalPecas);

        // 6. Construir OS
        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setVeiculo(veiculo);
        os.setServicos(servicos);
        os.setPecas(pecas);
        os.setValorTotal(valorTotal);
        os.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        os.setCriadoEm(LocalDateTime.now());

        return repo.save(os);
    }

    public OrdemServico atualizarStatus(Long id, StatusOrdemServico novoStatus) {
        OrdemServico os = repo.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
        os.setStatus(novoStatus);
        return repo.save(os);
    }

    public List<OrdemServico> listarTodas() {
        return repo.findAll();
    }

    public OrdemServico detalhar(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("OS não encontrada"));
    }

    public OrdemServicoPublicDTO getPublicInfo(Long id) {
        OrdemServico os = repo.findById(id).orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada"));
        return new OrdemServicoPublicDTO(
                os.getId(),
                os.getStatus(),
                os.getCriadoEm(),
                os.getCliente().getNome(),
                os.getVeiculo().getPlaca(),
                os.getServicos().stream().map(s -> s.getDescricao()).toList(),
                os.getPecas() != null ? os.getPecas().stream().map(p -> p.getDescricao()).toList() : List.of(),
                os.getValorTotal()
        );
    }
}