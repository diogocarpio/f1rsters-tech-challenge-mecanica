package com.f1rsters.tech_challenge_mecanica.repository;

import com.f1rsters.tech_challenge_mecanica.domain.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
}