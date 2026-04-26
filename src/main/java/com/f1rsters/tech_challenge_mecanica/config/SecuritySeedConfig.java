package com.f1rsters.tech_challenge_mecanica.config;

import com.f1rsters.tech_challenge_mecanica.domain.Role;
import com.f1rsters.tech_challenge_mecanica.domain.Usuario;
import com.f1rsters.tech_challenge_mecanica.repository.UsuarioRepository;
import com.f1rsters.tech_challenge_mecanica.util.InputNormalizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class SecuritySeedConfig {

    @Bean
    public CommandLineRunner adminUserSeed(UsuarioRepository usuarioRepository,
                                           PasswordEncoder passwordEncoder,
                                           @Value("${security.seed.enabled:true}") boolean enabled,
                                           @Value("${security.seed.admin.email:admin@oficina.local}") String adminEmail,
                                           @Value("${security.seed.admin.password:admin123}") String adminPassword) {
        return args -> {
            String normalizedAdminEmail = InputNormalizer.normalizeEmail(adminEmail);
            if (!enabled || usuarioRepository.existsByEmail(normalizedAdminEmail)) {
                return;
            }

            Usuario usuario = new Usuario();
            usuario.setEmail(normalizedAdminEmail);
            usuario.setSenhaHash(passwordEncoder.encode(adminPassword));
            usuario.setAtivo(true);
            usuario.setRoles(Set.of(Role.ADMIN));
            usuarioRepository.save(usuario);
        };
    }
}

