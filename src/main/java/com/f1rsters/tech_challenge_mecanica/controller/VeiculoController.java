package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.service.VeiculoService;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoDTO;
import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/veiculos")
public class VeiculoController {
    private final VeiculoService service;

    public VeiculoController(VeiculoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Veiculo> create(@RequestBody @Valid VeiculoDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public List<Veiculo> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Veiculo get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Veiculo update(@PathVariable Long id, @RequestBody @Valid VeiculoDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
