package com.f1rsters.tech_challenge_mecanica.security;

import com.f1rsters.tech_challenge_mecanica.domain.Usuario;
import com.f1rsters.tech_challenge_mecanica.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @Test
    void shouldLoadUserByUsername() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CustomUserDetailsService userDetailsService = new CustomUserDetailsService(usuarioRepository);
        
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setSenhaHash("hashedPassword");
        usuario.setAtivo(true);
        usuario.setRoles(Set.of(com.f1rsters.tech_challenge_mecanica.domain.Role.ADMIN));
        
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(usuario));
        
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");
        
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CustomUserDetailsService userDetailsService = new CustomUserDetailsService(usuarioRepository);
        
        when(usuarioRepository.findByEmail("notfound@example.com")).thenReturn(java.util.Optional.empty());
        
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("notfound@example.com");
        });
    }
}
