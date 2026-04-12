package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.dto.OrdemServicoPublicDTO;
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
    public ResponseEntity<OrdemServicoPublicDTO> visualizar(@PathVariable Long id) {
        OrdemServicoPublicDTO dto = service.getPublicInfo(id);
        return ResponseEntity.ok(dto);
    }
}