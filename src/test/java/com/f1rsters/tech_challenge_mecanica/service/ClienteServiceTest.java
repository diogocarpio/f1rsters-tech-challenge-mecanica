package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.dto.ClienteDTO;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repo;

    @InjectMocks
    private ClienteService service;

    @Test
    void deveSalvarClienteComCpfCnpjNormalizado() {
        ClienteDTO dto = new ClienteDTO();
        dto.nome = "Joao";
        dto.cpfCnpj = "529.982.247-25";

        when(repo.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente salvo = service.save(dto);

        assertEquals("Joao", salvo.getNome());
        assertEquals("52998224725", salvo.getCpfCnpj());
        verify(repo, times(1)).save(any(Cliente.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarClienteInexistente() {
        ClienteDTO dto = new ClienteDTO();
        dto.nome = "Maria";
        dto.cpfCnpj = "52998224725";

        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.update(99L, dto));
        verify(repo, never()).save(any(Cliente.class));
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        Cliente existente = new Cliente();
        existente.setId(10L);

        ClienteDTO dto = new ClienteDTO();
        dto.nome = "Maria";
        dto.cpfCnpj = "04.252.011/0001-10";

        when(repo.findById(10L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);

        Cliente atualizado = service.update(10L, dto);

        assertSame(existente, atualizado);
        assertEquals("Maria", atualizado.getNome());
        assertEquals("04252011000110", atualizado.getCpfCnpj());
    }

    @Test
    void deveListarTodosClientes() {
        Cliente c1 = new Cliente();
        c1.setId(1L);
        Cliente c2 = new Cliente();
        c2.setId(2L);
        when(repo.findAll()).thenReturn(List.of(c1, c2));

        List<Cliente> clientes = service.listAll();

        assertEquals(2, clientes.size());
        verify(repo).findAll();
    }

    @Test
    void deveObterClientePorId() {
        Cliente cliente = new Cliente();
        cliente.setId(7L);
        when(repo.findById(7L)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.get(7L);

        assertSame(cliente, resultado);
    }

    @Test
    void deveLancarExcecaoAoObterClienteInexistente() {
        when(repo.findById(70L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.get(70L));
    }

    @Test
    void deveExcluirClientePorId() {
        service.delete(33L);
        verify(repo).deleteById(33L);
    }
}

