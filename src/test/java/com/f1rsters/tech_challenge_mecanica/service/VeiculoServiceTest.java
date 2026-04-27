package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.domain.Veiculo;
import com.f1rsters.tech_challenge_mecanica.dto.VeiculoDTO;
import com.f1rsters.tech_challenge_mecanica.repository.ClienteRepository;
import com.f1rsters.tech_challenge_mecanica.repository.VeiculoRepository;
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
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository repo;

    @Mock
    private ClienteRepository clienteRepo;

    @InjectMocks
    private VeiculoService service;

    @Test
    void deveSalvarVeiculoComPlacaNormalizada() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        VeiculoDTO dto = new VeiculoDTO();
        dto.clienteId = 1L;
        dto.placa = "abc-1234";
        dto.marca = "Ford";
        dto.modelo = "Ka";
        dto.ano = 2020;

        when(clienteRepo.findById(1L)).thenReturn(Optional.of(cliente));
        when(repo.save(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Veiculo salvo = service.save(dto);

        assertEquals("ABC1234", salvo.getPlaca());
        assertEquals(cliente, salvo.getCliente());
        assertEquals("Ford", salvo.getMarca());
        verify(repo, times(1)).save(any(Veiculo.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        VeiculoDTO dto = new VeiculoDTO();
        dto.clienteId = 999L;
        dto.placa = "ABC1234";

        when(clienteRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.save(dto));
        verify(repo, never()).save(any(Veiculo.class));
    }

    @Test
    void deveAtualizarVeiculoComSucesso() {
        Cliente cliente = new Cliente();
        cliente.setId(8L);

        Veiculo existente = new Veiculo();
        existente.setId(5L);

        VeiculoDTO dto = new VeiculoDTO();
        dto.clienteId = 8L;
        dto.placa = "xyz-9a99";
        dto.marca = "VW";
        dto.modelo = "Gol";
        dto.ano = 2017;

        when(repo.findById(5L)).thenReturn(Optional.of(existente));
        when(clienteRepo.findById(8L)).thenReturn(Optional.of(cliente));
        when(repo.save(existente)).thenReturn(existente);

        Veiculo atualizado = service.update(5L, dto);

        assertSame(existente, atualizado);
        assertEquals("XYZ9A99", atualizado.getPlaca());
        assertEquals("VW", atualizado.getMarca());
        assertEquals(cliente, atualizado.getCliente());
    }

    @Test
    void deveFalharAoAtualizarQuandoVeiculoNaoExiste() {
        VeiculoDTO dto = new VeiculoDTO();
        dto.clienteId = 1L;
        dto.placa = "AAA0000";

        when(repo.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.update(404L, dto));
        verify(repo, never()).save(any(Veiculo.class));
    }

    @Test
    void deveFalharAoAtualizarQuandoClienteNaoExiste() {
        Veiculo existente = new Veiculo();
        VeiculoDTO dto = new VeiculoDTO();
        dto.clienteId = 404L;
        dto.placa = "AAA0000";

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(clienteRepo.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.update(1L, dto));
        verify(repo, never()).save(any(Veiculo.class));
    }

    @Test
    void deveListarTodosVeiculos() {
        when(repo.findAll()).thenReturn(List.of(new Veiculo(), new Veiculo()));

        List<Veiculo> veiculos = service.listAll();

        assertEquals(2, veiculos.size());
        verify(repo).findAll();
    }

    @Test
    void deveObterVeiculoPorId() {
        Veiculo v = new Veiculo();
        when(repo.findById(3L)).thenReturn(Optional.of(v));

        Veiculo resultado = service.get(3L);

        assertSame(v, resultado);
    }

    @Test
    void deveFalharAoObterVeiculoInexistente() {
        when(repo.findById(3L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.get(3L));
    }

    @Test
    void deveExcluirVeiculoPorId() {
        service.delete(44L);
        verify(repo).deleteById(44L);
    }
}

