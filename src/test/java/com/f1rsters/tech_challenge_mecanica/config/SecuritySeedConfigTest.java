package com.f1rsters.tech_challenge_mecanica.config;

import com.f1rsters.tech_challenge_mecanica.domain.Role;
import com.f1rsters.tech_challenge_mecanica.domain.Usuario;
import com.f1rsters.tech_challenge_mecanica.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SecuritySeedConfigTest {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private SecuritySeedConfig securitySeedConfig;

    @BeforeEach
    void setup() {
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        securitySeedConfig = new SecuritySeedConfig();
    }

    @Test
    void shouldNotCreateAdminWhenSeedDisabled() throws Exception {

        CommandLineRunner runner = securitySeedConfig.adminUserSeed(
                usuarioRepository,
                passwordEncoder,
                false,
                "admin@oficina.local",
                "admin123"
        );

        runner.run();

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateAdminWhenUserAlreadyExists() throws Exception {

        when(usuarioRepository.existsByEmail("admin@oficina.local")).thenReturn(true);

        CommandLineRunner runner = securitySeedConfig.adminUserSeed(
                usuarioRepository,
                passwordEncoder,
                true,
                "admin@oficina.local",
                "admin123"
        );

        runner.run();

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void shouldCreateAdminWhenUserDoesNotExistAndSeedEnabled() throws Exception {

        when(usuarioRepository.existsByEmail("admin@oficina.local")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");

        CommandLineRunner runner = securitySeedConfig.adminUserSeed(
                usuarioRepository,
                passwordEncoder,
                true,
                "admin@oficina.local",
                "admin123"
        );

        runner.run();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario savedUser = captor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo("admin@oficina.local");
        assertThat(savedUser.getSenhaHash()).isEqualTo("encodedPassword");
        assertThat(savedUser.isAtivo()).isTrue();
        assertThat(savedUser.getRoles()).isEqualTo(Set.of(Role.ADMIN));
    }
}
