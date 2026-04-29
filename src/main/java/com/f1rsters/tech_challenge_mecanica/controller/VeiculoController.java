package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.dto.VeiculoDTO;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoResponseDTO;
import com.f1rsters.tech_challenge_mecanica.mapper.VeiculoMapper;
import com.f1rsters.tech_challenge_mecanica.service.VeiculoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/veiculos")
@Tag(name = "Veiculos", description = "Endpoints de veiculos")
public class VeiculoController {
    private final VeiculoService service;

    public VeiculoController(VeiculoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VeiculoResponseDTO> create(@RequestBody @Valid VeiculoDTO dto) {
        return ResponseEntity.ok(VeiculoMapper.toResponse(service.save(dto)));
    }

    @GetMapping
    public List<VeiculoResponseDTO> listAll() {
        return service.listAll().stream().map(VeiculoMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public VeiculoResponseDTO get(@PathVariable Long id) {
        return VeiculoMapper.toResponse(service.get(id));
    }

    @PutMapping("/{id}")
    public VeiculoResponseDTO update(@PathVariable Long id, @RequestBody @Valid VeiculoDTO dto) {
        return VeiculoMapper.toResponse(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
