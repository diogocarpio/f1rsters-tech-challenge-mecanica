package com.f1rsters.tech_challenge_mecanica.controller;

import com.f1rsters.tech_challenge_mecanica.dto.ClienteResponseDTO;
import com.f1rsters.tech_challenge_mecanica.mapper.ClienteMapper;
import com.f1rsters.tech_challenge_mecanica.service.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Cliente Autenticado", description = "Endpoints protegidos por autenticacao via CPF")
public class ClienteAutenticadoController {

    private final ClienteService service;

    public ClienteAutenticadoController(ClienteService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ClienteResponseDTO me(Authentication authentication) {
        return ClienteMapper.toResponse(service.getByCpfCnpj(authentication.getName()));
    }
}
