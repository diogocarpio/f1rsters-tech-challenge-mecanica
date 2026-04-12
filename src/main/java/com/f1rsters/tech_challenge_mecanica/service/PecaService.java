package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import com.f1rsters.tech_challenge_mecanica.dto.PecaDTO;
import com.f1rsters.tech_challenge_mecanica.repository.PecaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PecaService {
    private final PecaRepository repo;

    public PecaService(PecaRepository repo) {
        this.repo = repo;
    }

    public Peca save(PecaDTO dto) {
        Peca p = new Peca();
        p.setDescricao(dto.descricao);
        p.setQuantidadeEstoque(dto.quantidadeEstoque);
        p.setValorUnitario(dto.valorUnitario);
        return repo.save(p);
    }

    public List<Peca> listAll() {
        return repo.findAll();
    }

    public Peca update(Long id, PecaDTO dto) {
        Peca p = repo.findById(id).orElseThrow();
        p.setDescricao(dto.descricao);
        p.setQuantidadeEstoque(dto.quantidadeEstoque);
        p.setValorUnitario(dto.valorUnitario);
        return repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Peca get(Long id) {
        return repo.findById(id).orElseThrow();
    }
}