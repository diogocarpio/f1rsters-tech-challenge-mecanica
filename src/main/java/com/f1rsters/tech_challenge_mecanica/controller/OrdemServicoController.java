package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.dto.AtualizarStatusOSDTO;
import com.f1rsters.tech_challenge_mecanica.dto.CriarOrdemServicoDTO;
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
}