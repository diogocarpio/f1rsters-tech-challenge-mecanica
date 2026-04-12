package com.f1rsters.tech_challenge_mecanica.repository;

import com.f1rsters.tech_challenge_mecanica.domain.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
}