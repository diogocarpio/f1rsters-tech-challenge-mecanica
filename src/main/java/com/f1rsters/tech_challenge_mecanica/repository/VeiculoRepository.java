package com.f1rsters.tech_challenge_mecanica.repository;

import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
}