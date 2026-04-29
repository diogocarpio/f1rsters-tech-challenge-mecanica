package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.dto.ClienteDTO;
import com.f1rsters.tech_challenge_mecanica.dto.ClienteResponseDTO;
import com.f1rsters.tech_challenge_mecanica.mapper.ClienteMapper;
import com.f1rsters.tech_challenge_mecanica.service.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clientes")
@Tag(name = "Clientes", description = "Endpoints de clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> create(@RequestBody @Valid ClienteDTO dto) {
        return ResponseEntity.ok(ClienteMapper.toResponse(service.save(dto)));
    }

    @GetMapping
    public List<ClienteResponseDTO> listAll() {
        return service.listAll().stream().map(ClienteMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO get(@PathVariable Long id) {
        return ClienteMapper.toResponse(service.get(id));
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO update(@PathVariable Long id, @RequestBody @Valid ClienteDTO dto) {
        return ClienteMapper.toResponse(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}