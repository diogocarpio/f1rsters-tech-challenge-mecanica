package com.f1rsters.tech_challenge_mecanica.repository;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);
}
