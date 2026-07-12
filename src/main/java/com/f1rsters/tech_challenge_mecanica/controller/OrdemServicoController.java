package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.dto.AtualizarStatusOSDTO;
import com.f1rsters.tech_challenge_mecanica.dto.CriarOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.NotificacaoStatusDTO;
import com.f1rsters.tech_challenge_mecanica.dto.RespostaOrcamentoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.StatusOrdemServicoDTO;
import com.f1rsters.tech_challenge_mecanica.domain.OrdemServico;
import com.f1rsters.tech_challenge_mecanica.service.OrdemServicoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ordens-servico")
@Tag(name = "Ordens de Servico", description = "Endpoints de ordens de servico (admin)")
public class OrdemServicoController {
    private final OrdemServicoService service;

    public OrdemServicoController(OrdemServicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrdemServico> criar(@RequestBody @Valid CriarOrdemServicoDTO dto) {
        return ResponseEntity.ok(service.criarOrdem(dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrdemServico> atualizarStatus(@PathVariable Long id, @RequestBody @Valid AtualizarStatusOSDTO dto) {
        return ResponseEntity.ok(service.atualizarStatus(id, dto.novoStatus));
    }

    @GetMapping
    public List<OrdemServico> listar() { return service.listarTodas(); }

    @GetMapping("/{id}")
    public OrdemServico detalhar(@PathVariable Long id) { return service.detalhar(id); }

    @GetMapping("/{id}/status")
    public ResponseEntity<StatusOrdemServicoDTO> consultarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(service.consultarStatus(id));
    }

    @PostMapping("/{id}/orcamento/resposta")
    public ResponseEntity<OrdemServico> responderOrcamento(@PathVariable Long id, @RequestBody @Valid RespostaOrcamentoDTO dto) {
        return ResponseEntity.ok(service.processarRespostaOrcamento(id, dto));
    }

    @PostMapping("/{id}/status/notificacao")
    public ResponseEntity<OrdemServico> notificarStatus(@PathVariable Long id, @RequestBody @Valid NotificacaoStatusDTO dto) {
        return ResponseEntity.ok(service.processarNotificacaoStatus(id, dto));
    }
}