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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrdemServicoService {
    private static final Logger log = LoggerFactory.getLogger(OrdemServicoService.class);
    
    // String literals constants to avoid duplication
    private static final String OS_NAO_ENCONTRADA = "OS não encontrada";
    private static final String OPERATION = "operation";
    private static final String ERROR_REASON = "error_reason";
    private static final String ERROR_TYPE = "error_type";
    private static final String ERROR_MESSAGE = "error_message";
    private static final String OS_ID = "os_id";
    private static final String TIMER_DURATION_DESCRIPTION = "Duração de processamento das operações de OS";
    private static final String TIMER_NAME = "ordem_servico.processing.duration";
    private static final String CRIAR_ORDEM = "criar_ordem";
    private static final String ATUALIZAR_STATUS = "atualizar_status";
    private static final String PROCESSAR_ORCAMENTO = "processar_orcamento";
    private static final String PROCESSAR_NOTIFICACAO_STATUS = "processar_notificacao_status";
    private static final String OS_NAO_ENCONTRADA_REASON = "os_nao_encontrada";
    private static final String DETALHAR = "detalhar";
    private static final String CONSULTAR_STATUS = "consultar_status";
    
    private final OrdemServicoRepository repo;
    private final ClienteRepository clienteRepo;
    private final VeiculoRepository veiculoRepo;
    private final ServicoRepository servicoRepo;
    private final PecaRepository pecaRepo;
    private final MeterRegistry meterRegistry;
    
    private final Counter criarOrdemSuccessCounter;
    private final Counter criarOrdemErrorCounter;
    private final Counter atualizarStatusSuccessCounter;
    private final Counter atualizarStatusErrorCounter;
    private final Counter processarOrcamentoSuccessCounter;
    private final Counter processarOrcamentoErrorCounter;
    private final Counter processarNotificacaoStatusSuccessCounter;
    private final Counter processarNotificacaoStatusErrorCounter;
    private final Counter osCreatedTotalCounter;
    private final Counter processingFailureCounter;
    private final Counter statusTransitionCounter;

    public OrdemServicoService(OrdemServicoRepository repo,
                               ClienteRepository clienteRepo,
                               VeiculoRepository veiculoRepo,
                               ServicoRepository servicoRepo,
                               PecaRepository pecaRepo,
                               MeterRegistry meterRegistry) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
        this.veiculoRepo = veiculoRepo;
        this.servicoRepo = servicoRepo;
        this.pecaRepo = pecaRepo;
        this.meterRegistry = meterRegistry;
        
        this.criarOrdemSuccessCounter = Counter.builder("ordem_servico.criar_ordem.success.total")
                .description("Sucesso na criação de ordens de serviço")
                .register(meterRegistry);
        this.criarOrdemErrorCounter = Counter.builder("ordem_servico.criar_ordem.unexpected_error.total")
                .description("Erros não esperados na criação de ordens de serviço")
                .register(meterRegistry);
        this.atualizarStatusSuccessCounter = Counter.builder("ordem_servico.atualizar_status.success.total")
                .description("Sucesso na atualização de status de ordens de serviço")
                .register(meterRegistry);
        this.atualizarStatusErrorCounter = Counter.builder("ordem_servico.atualizar_status.unexpected_error.total")
                .description("Erros não esperados na atualização de status")
                .register(meterRegistry);
        this.processarOrcamentoSuccessCounter = Counter.builder("ordem_servico.processar_orcamento.success.total")
                .description("Sucesso no processamento de orçamento")
                .register(meterRegistry);
        this.processarOrcamentoErrorCounter = Counter.builder("ordem_servico.processar_orcamento.unexpected_error.total")
                .description("Erros não esperados no processamento de orçamento")
                .register(meterRegistry);
        this.processarNotificacaoStatusSuccessCounter = Counter.builder("ordem_servico.processar_notificacao_status.success.total")
                .description("Sucesso no processamento de notificação de status")
                .register(meterRegistry);
        this.processarNotificacaoStatusErrorCounter = Counter.builder("ordem_servico.processar_notificacao_status.unexpected_error.total")
                .description("Erros não esperados no processamento de notificação de status")
                .register(meterRegistry);
        this.osCreatedTotalCounter = Counter.builder("ordem_servico.created.total")
                .description("Total de ordens de serviço criadas")
                .register(meterRegistry);
        this.processingFailureCounter = Counter.builder("ordem_servico.processing.failure.total")
                .description("Falhas no processamento de ordens de serviço")
                .register(meterRegistry);
        this.statusTransitionCounter = Counter.builder("ordem_servico.status.transition.total")
                .description("Total de transições de status de ordens de serviço")
                .register(meterRegistry);
    }

    @Transactional
    public OrdemServico criarOrdem(CriarOrdemServicoDTO dto) {
        Timer.Sample sample = Timer.start(meterRegistry);
        MDC.put(OPERATION, CRIAR_ORDEM);
        
        try {
            log.info("Iniciando criação de ordem de serviço");
            
            String cpfCnpjNormalizado = InputNormalizer.normalizeCpfCnpj(dto.cpfCnpjCliente);
            String placaNormalizada = InputNormalizer.normalizePlaca(dto.placaVeiculo);

            // 1. Buscar cliente por CPF/CNPJ
            Cliente cliente = clienteRepo.findByCpfCnpj(cpfCnpjNormalizado)
                    .orElseThrow(() -> {
                        MDC.put(ERROR_REASON, "cliente_nao_encontrado");
                        MDC.put("cpf_cnpj", SensitiveDataMasker.maskCpfCnpj(cpfCnpjNormalizado));
                        log.error("Cliente não encontrado para CPF/CNPJ: {}", cpfCnpjNormalizado);
                        return businessException("Cliente não encontrado", "cliente_nao_encontrado", null, CRIAR_ORDEM);
                    });
            
            MDC.put("client_id", cliente.getId().toString());
            MDC.put("client_name", cliente.getNome());

            // 2. Buscar veículo por placa
            Veiculo veiculo = veiculoRepo.findByPlaca(placaNormalizada)
                    .orElseThrow(() -> {
                        MDC.put(ERROR_REASON, "veiculo_nao_encontrado");
                        MDC.put("vehicle_plate", SensitiveDataMasker.maskPlaca(placaNormalizada));
                        log.error("Veículo não encontrado para placa: {}", placaNormalizada);
                        return businessException("Veículo não encontrado", "veiculo_nao_encontrado", null, CRIAR_ORDEM);
                    });
            
            MDC.put("vehicle_id", veiculo.getId().toString());
            MDC.put("vehicle_plate", SensitiveDataMasker.maskPlaca(veiculo.getPlaca()));

            // 3. Buscar serviços
            List<Servico> servicos = servicoRepo.findAllById(dto.servicos);
            MDC.put("servicos_count", String.valueOf(servicos.size()));

            // 4. Buscar peças
            List<Peca> pecas = dto.pecas != null ? pecaRepo.findAllById(dto.pecas) : List.of();
            MDC.put("pecas_count", String.valueOf(pecas.size()));

            // 5. Validar e descontar estoque das peças
            for (Peca pecaEmUso : pecas) {
                if (pecaEmUso.getQuantidadeEstoque() < 1) {
                    MDC.put(ERROR_REASON, "estoque_insuficiente");
                    MDC.put("peca_descricao", pecaEmUso.getDescricao());
                    log.error("Sem estoque suficiente da peça: {}", pecaEmUso.getDescricao());
                    throw businessException("Sem estoque suficiente da peça: " + pecaEmUso.getDescricao(), "estoque_insuficiente", null, "criar_ordem");
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
            os.setCriadoEm(LocalDateTime.now(ZoneId.systemDefault()));
            OrdemServico created = repo.save(os);
            
            MDC.put(OS_ID, created.getId().toString());
            MDC.put("os_status", created.getStatus().name());
            MDC.put("os_value", created.getValorTotal().toString());
            
            log.info("Ordem de serviço criada com sucesso: id={}, status={}", created.getId(), created.getStatus());
            
            osCreatedTotalCounter.increment();
            criarOrdemSuccessCounter.increment();
            
            sample.stop(Timer.builder(TIMER_NAME)
                    .description(TIMER_DURATION_DESCRIPTION)
                    .tag(OPERATION, CRIAR_ORDEM)
                    .register(meterRegistry));
            
            return created;
            
        } catch (Exception e) {
            MDC.put(ERROR_TYPE, e.getClass().getSimpleName());
            MDC.put(ERROR_MESSAGE, e.getMessage());
            log.error("Erro não tratado ao criar ordem de serviço", e);
            
            recordFailure(CRIAR_ORDEM, e.getClass().getSimpleName(), null);
            criarOrdemErrorCounter.increment();
            
            throw e;
            
        } finally {
            MDC.clear();
        }
    }

    public OrdemServico atualizarStatus(Long id, StatusOrdemServico novoStatus) {
        Timer.Sample sample = Timer.start(meterRegistry);
        MDC.put(OPERATION, ATUALIZAR_STATUS);
        MDC.put(OS_ID, id.toString());
        MDC.put("novo_status", novoStatus.name());
        
        try {
            log.info("Atualizando status da OS: id={}, novoStatus={}", id, novoStatus);
            
            OrdemServico os = repo.findById(id).orElseThrow(() -> {
                MDC.put(ERROR_REASON, OS_NAO_ENCONTRADA_REASON);
                log.error("OS não encontrada: id={}", id);
                return businessException(OS_NAO_ENCONTRADA, OS_NAO_ENCONTRADA_REASON, id, ATUALIZAR_STATUS);
            });
            
            StatusOrdemServico statusAnterior = os.getStatus();
            MDC.put("status_anterior", statusAnterior.name());
            
            os.setStatus(novoStatus);
            OrdemServico updated = repo.save(os);
            
            log.info("Status atualizado com sucesso: id={}, de={}, para={}", id, statusAnterior, novoStatus);
            
            recordStatusTransition(updated, statusAnterior, novoStatus);
            
            atualizarStatusSuccessCounter.increment();
            
            sample.stop(Timer.builder(TIMER_NAME)
                    .description(TIMER_DURATION_DESCRIPTION)
                    .tag(OPERATION, ATUALIZAR_STATUS)
                    .register(meterRegistry));
            
            return updated;
            
        } catch (Exception e) {
            MDC.put(ERROR_TYPE, e.getClass().getSimpleName());
            MDC.put(ERROR_MESSAGE, e.getMessage());
            log.error("Erro não tratado ao atualizar status da OS", e);
            
            recordFailure(ATUALIZAR_STATUS, e.getClass().getSimpleName(), id);
            atualizarStatusErrorCounter.increment();
            
            throw e;
            
        } finally {
            MDC.clear();
        }
    }

    public List<OrdemServico> listarTodas() {
        return repo.findAllActiveOrderByStatusAndDate();
    }

    public OrdemServico detalhar(Long id) {
        MDC.put(OPERATION, DETALHAR);
        MDC.put(OS_ID, id.toString());
        
        try {
            log.info("Buscando detalhes da OS: id={}", id);
            
            OrdemServico os = repo.findById(id).orElseThrow(() -> {
                MDC.put(ERROR_REASON, OS_NAO_ENCONTRADA_REASON);
                log.error("OS não encontrada: id={}", id);
                return businessException(OS_NAO_ENCONTRADA, OS_NAO_ENCONTRADA_REASON, id, DETALHAR);
            });
            
            log.info("OS encontrada: id={}, status={}", id, os.getStatus());
            
            return os;
            
        } finally {
            MDC.clear();
        }
    }

    public StatusOrdemServicoDTO consultarStatus(Long id) {
        MDC.put(OPERATION, CONSULTAR_STATUS);
        MDC.put(OS_ID, id.toString());
        
        try {
            log.info("Consultando status da OS: id={}", id);
            
            OrdemServico os = repo.findById(id).orElseThrow(() -> {
                MDC.put(ERROR_REASON, "os_nao_encontrada");
                log.error("OS não encontrada: id={}", id);
                return businessException(OS_NAO_ENCONTRADA, OS_NAO_ENCONTRADA_REASON, id, CONSULTAR_STATUS);
            });
            
            log.info("Status consultado: id={}, status={}", id, os.getStatus());
            
            return StatusOrdemServicoDTO.from(os.getId(), os.getStatus(), os.getCriadoEm());
            
        } finally {
            MDC.clear();
        }
    }

    @Transactional
    public OrdemServico processarRespostaOrcamento(Long id, RespostaOrcamentoDTO dto) {
        Timer.Sample sample = Timer.start(meterRegistry);
        MDC.put(OPERATION, PROCESSAR_ORCAMENTO);
        MDC.put(OS_ID, id.toString());
        MDC.put("aprovado", String.valueOf(dto.aprovado()));
        
        try {
            log.info("Processando resposta de orçamento: id={}, aprovado={}", id, dto.aprovado());
            
            OrdemServico os = repo.findById(id).orElseThrow(() -> {
                MDC.put(ERROR_REASON, OS_NAO_ENCONTRADA_REASON);
                log.error("OS não encontrada: id={}", id);
                return businessException(OS_NAO_ENCONTRADA, OS_NAO_ENCONTRADA_REASON, id, PROCESSAR_ORCAMENTO);
            });
            
            MDC.put("status_atual", os.getStatus().name());
            
            if (os.getStatus() != StatusOrdemServico.AGUARDANDO_APROVACAO) {
                MDC.put(ERROR_REASON, "transicao_status_invalida");
                log.error("OS não está aguardando aprovação: id={}, status={}", id, os.getStatus());
                throw businessException("OS não está aguardando aprovação", "transicao_status_invalida", id, PROCESSAR_ORCAMENTO);
            }
            
            StatusOrdemServico statusAnterior = os.getStatus();
            if (dto.aprovado()) {
                os.setStatus(StatusOrdemServico.EM_EXECUCAO);
                log.info("Orçamento aprovado: id={}, novoStatus=EM_EXECUCAO", id);
            } else {
                log.info("Orçamento recusado: id={}, status mantido=AGUARDANDO_APROVACAO", id);
            }
            
            OrdemServico updated = repo.save(os);
            
            recordStatusTransition(updated, statusAnterior, updated.getStatus());
            
            processarOrcamentoSuccessCounter.increment();
            
            sample.stop(Timer.builder(TIMER_NAME)
                    .description(TIMER_DURATION_DESCRIPTION)
                    .tag(OPERATION, PROCESSAR_ORCAMENTO)
                    .register(meterRegistry));
            
            return updated;
            
        } catch (Exception e) {
            MDC.put(ERROR_TYPE, e.getClass().getSimpleName());
            MDC.put(ERROR_MESSAGE, e.getMessage());
            log.error("Erro não tratado ao processar resposta de orçamento", e);
            
            recordFailure(PROCESSAR_ORCAMENTO, e.getClass().getSimpleName(), id);
            processarOrcamentoErrorCounter.increment();
            
            throw e;
            
        } finally {
            MDC.clear();
        }
    }

    @Transactional
    public OrdemServico processarNotificacaoStatus(Long id, NotificacaoStatusDTO dto) {
        Timer.Sample sample = Timer.start(meterRegistry);
        MDC.put(OPERATION, PROCESSAR_NOTIFICACAO_STATUS);
        MDC.put(OS_ID, id.toString());
        MDC.put("novo_status", dto.novoStatus().name());
        
        try {
            log.info("Processando notificação de status: id={}, novoStatus={}", id, dto.novoStatus());
            
            OrdemServico os = repo.findById(id).orElseThrow(() -> {
                MDC.put(ERROR_REASON, OS_NAO_ENCONTRADA_REASON);
                log.error("OS não encontrada: id={}", id);
                return businessException(OS_NAO_ENCONTRADA, OS_NAO_ENCONTRADA_REASON, id, PROCESSAR_NOTIFICACAO_STATUS);
            });
            
            // Registrar status anterior para rastreabilidade
            StatusOrdemServico statusAnterior = os.getStatus();
            MDC.put("status_anterior", statusAnterior.name());
            
            // Atualizar status
            os.setStatus(dto.novoStatus());
            OrdemServico updated = repo.save(os);
            
            log.info("Status atualizado via notificação: id={}, de={}, para={}", id, statusAnterior, dto.novoStatus());
            
            recordStatusTransition(updated, statusAnterior, dto.novoStatus());
            
            processarNotificacaoStatusSuccessCounter.increment();
            
            sample.stop(Timer.builder(TIMER_NAME)
                    .description(TIMER_DURATION_DESCRIPTION)
                    .tag(OPERATION, PROCESSAR_NOTIFICACAO_STATUS)
                    .register(meterRegistry));
            
            return updated;
            
        } catch (Exception e) {
            MDC.put(ERROR_TYPE, e.getClass().getSimpleName());
            MDC.put(ERROR_MESSAGE, e.getMessage());
            log.error("Erro não tratado ao processar notificação de status", e);
            
            recordFailure(PROCESSAR_NOTIFICACAO_STATUS, e.getClass().getSimpleName(), id);
            processarNotificacaoStatusErrorCounter.increment();
            
            throw e;
            
        } finally {
            MDC.clear();
        }
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

    private void recordFailure(String operation, String errorType, Long osId) {
        Counter.builder("ordem_servico.processing.failure.total")
                .description("Falhas no processamento de ordens de serviço")
                .tag(OPERATION, operation)
                .tag(ERROR_TYPE, errorType)
                .tag(OS_ID, osId != null ? osId.toString() : "unknown")
                .register(meterRegistry)
                .increment();
    }

    private RuntimeException businessException(String message, String reason, Long osId, String operation) {
        recordFailure(operation, reason, osId);
        log.error("Falha no processamento de OS: reason={}, operation={}, osId={}, message={}", 
                  reason, operation, osId, message);
        return new RuntimeException(message);
    }

    private void recordStatusTransition(OrdemServico os,
                                        StatusOrdemServico statusAnterior,
                                        StatusOrdemServico statusAtual) {
        Counter.builder("ordem_servico.status.transition.total")
                .description("Total de transições de status de ordens de serviço")
                .tag("from", statusAnterior.name())
                .tag("to", statusAtual.name())
                .register(meterRegistry)
                .increment();

        if (os.getCriadoEm() == null) {
            return;
        }

        if (statusAtual == StatusOrdemServico.DIAGNOSTICO
                || statusAtual == StatusOrdemServico.EM_EXECUCAO
                || statusAtual == StatusOrdemServico.FINALIZADA) {
            double elapsedSeconds = Math.max(0, Duration.between(os.getCriadoEm().atZone(ZoneId.systemDefault()), ZonedDateTime.now(ZoneId.systemDefault())).toSeconds());
            DistributionSummary.builder("ordem_servico.status.lead_time.seconds")
                    .description("Tempo em segundos para alcançar um status de OS")
                    .tag("status", statusAtual.name())
                    .register(meterRegistry)
                    .record(elapsedSeconds);
        }
    }
}