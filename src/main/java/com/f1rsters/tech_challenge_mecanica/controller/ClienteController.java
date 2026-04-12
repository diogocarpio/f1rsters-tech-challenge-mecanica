package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.service.ClienteService;
import com.f1rsters.tech_challenge_mecanica.dto.ClienteDTO;
import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Cliente> create(@RequestBody @Valid ClienteDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public List<Cliente> listAll() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Cliente get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @RequestBody @Valid ClienteDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}