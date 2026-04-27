package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoDTO;
import com.f1rsters.tech_challenge_mecanica.repository.VeiculoRepository;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import com.f1rsters.tech_challenge_mecanica.util.InputNormalizer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeiculoService {
    private final VeiculoRepository repo;
    private final ClienteRepository clienteRepo;

    public VeiculoService(VeiculoRepository repo, ClienteRepository clienteRepo) {
        this.repo = repo;
        this.clienteRepo = clienteRepo;
    }

    public Veiculo save(VeiculoDTO dto) {
        Veiculo v = new Veiculo();
        v.setPlaca(InputNormalizer.normalizePlaca(dto.placa));
        v.setMarca(dto.marca);
        v.setModelo(dto.modelo);
        v.setAno(dto.ano);
        Cliente cliente = clienteRepo.findById(dto.clienteId).orElseThrow();
        v.setCliente(cliente);
        return repo.save(v);
    }

    public List<Veiculo> listAll() {
        return repo.findAll();
    }

    public Veiculo update(Long id, VeiculoDTO dto) {
        Veiculo v = repo.findById(id).orElseThrow();
        v.setPlaca(InputNormalizer.normalizePlaca(dto.placa));
        v.setMarca(dto.marca);
        v.setModelo(dto.modelo);
        v.setAno(dto.ano);
        Cliente cliente = clienteRepo.findById(dto.clienteId).orElseThrow();
        v.setCliente(cliente);
        return repo.save(v);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Veiculo get(Long id) {
        return repo.findById(id).orElseThrow();
    }
}