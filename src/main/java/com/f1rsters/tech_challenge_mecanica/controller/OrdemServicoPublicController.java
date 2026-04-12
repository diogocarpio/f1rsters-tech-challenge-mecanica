package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.domain.OrdemServico;
import com.f1rsters.tech_challenge_mecanica.service.OrdemServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/ordens-servico")
public class OrdemServicoPublicController {
    private final OrdemServicoService service;
    public OrdemServicoPublicController(OrdemServicoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServico> visualizar(@PathVariable Long id) {
        return ResponseEntity.of(java.util.Optional.ofNullable(service.detalhar(id)));
    }
}