package com.f1rsters.tech_challenge_mecanica.repository;

import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PecaRepository extends JpaRepository<Peca, Long> {
}