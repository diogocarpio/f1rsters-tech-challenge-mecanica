package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.service.ServicoService;
import com.f1rsters.tech_challenge_mecanica.dto.ServicoDTO;
import com.f1rsters.tech_challenge_mecanica.domain.Servico;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/servicos")
@Tag(name = "Servicos", description = "Endpoints de servicos")
public class ServicoController {
    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Servico> create(@RequestBody @Valid ServicoDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public List<Servico> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Servico get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Servico update(@PathVariable Long id, @RequestBody @Valid ServicoDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}