package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.dto.ClienteDTO;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import com.f1rsters.tech_challenge_mecanica.util.InputNormalizer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public Cliente save(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setNome(dto.nome);
        c.setCpfCnpj(InputNormalizer.normalizeCpfCnpj(dto.cpfCnpj));
        return repo.save(c);
    }

    public List<Cliente> listAll() {
        return repo.findAll();
    }

    public Cliente update(Long id, ClienteDTO dto) {
        Cliente c = repo.findById(id).orElseThrow();
        c.setNome(dto.nome);
        c.setCpfCnpj(InputNormalizer.normalizeCpfCnpj(dto.cpfCnpj));
        return repo.save(c);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Cliente get(Long id) {
        return repo.findById(id).orElseThrow();
    }
}