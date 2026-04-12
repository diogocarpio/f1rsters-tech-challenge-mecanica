package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.dto.BaixaEstoqueDTO;
import com.f1rsters.tech_challenge_mecanica.service.PecaService;
import com.f1rsters.tech_challenge_mecanica.dto.PecaDTO;
import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pecas")
public class PecaController {
    private final PecaService service;

    public PecaController(PecaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Peca> create(@RequestBody @Valid PecaDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public List<Peca> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Peca get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Peca update(@PathVariable Long id, @RequestBody @Valid PecaDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/estoque")
    public List<Peca> listarEstoque() {
        return service.listAll();
    }

    @PostMapping("/baixa")
    public ResponseEntity<Peca> baixarEstoque(@RequestBody @Valid BaixaEstoqueDTO dto) {
        return ResponseEntity.ok(service.baixarEstoque(dto.pecaId, dto.quantidade));
    }
}