package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Servico;
import com.f1rsters.tech_challenge_mecanica.dto.ServicoDTO;
import com.f1rsters.tech_challenge_mecanica.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {
    private final ServicoRepository repo;

    public ServicoService(ServicoRepository repo) {
        this.repo = repo;
    }

    public Servico save(ServicoDTO dto) {
        Servico s = new Servico();
        s.setDescricao(dto.descricao);
        s.setValor(dto.valor);
        return repo.save(s);
    }

    public List<Servico> listAll() {
        return repo.findAll();
    }

    public Servico update(Long id, ServicoDTO dto) {
        Servico s = repo.findById(id).orElseThrow();
        s.setDescricao(dto.descricao);
        s.setValor(dto.valor);
        return repo.save(s);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Servico get(Long id) {
        return repo.findById(id).orElseThrow();
    }
}