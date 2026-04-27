package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Peca;
import com.f1rsters.tech_challenge_mecanica.dto.PecaDTO;
import com.f1rsters.tech_challenge_mecanica.repository.PecaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PecaServiceTest {

    @Mock
    private PecaRepository repo;

    @InjectMocks
    private PecaService service;

    @Test
    void deveBaixarEstoqueComSucesso() {
        Peca peca = new Peca();
        peca.setId(1L);
        peca.setDescricao("Filtro de ar");
        peca.setQuantidadeEstoque(10);
        peca.setValorUnitario(new BigDecimal("25.00"));

        when(repo.findById(1L)).thenReturn(Optional.of(peca));
        when(repo.save(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Peca atualizada = service.baixarEstoque(1L, 3);

        assertEquals(7, atualizada.getQuantidadeEstoque());
        verify(repo, times(1)).save(peca);
    }

    @Test
    void deveFalharQuandoPecaNaoExiste() {
        when(repo.findById(100L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.baixarEstoque(100L, 1));
        assertTrue(ex.getMessage().contains("Peça não encontrada"));
        verify(repo, never()).save(any(Peca.class));
    }

    @Test
    void deveFalharQuandoEstoqueInsuficiente() {
        Peca peca = new Peca();
        peca.setId(2L);
        peca.setDescricao("Pastilha");
        peca.setQuantidadeEstoque(1);

        when(repo.findById(2L)).thenReturn(Optional.of(peca));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.baixarEstoque(2L, 2));
        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
        verify(repo, never()).save(any(Peca.class));
    }

    @Test
    void deveSalvarPecaComSucesso() {
        PecaDTO dto = new PecaDTO();
        dto.descricao = "Correia";
        dto.quantidadeEstoque = 15;
        dto.valorUnitario = new BigDecimal("49.90");

        when(repo.save(any(Peca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Peca salva = service.save(dto);

        assertEquals("Correia", salva.getDescricao());
        assertEquals(15, salva.getQuantidadeEstoque());
        assertEquals(new BigDecimal("49.90"), salva.getValorUnitario());
    }

    @Test
    void deveAtualizarPecaComSucesso() {
        Peca existente = new Peca();
        existente.setId(9L);

        PecaDTO dto = new PecaDTO();
        dto.descricao = "Bateria";
        dto.quantidadeEstoque = 8;
        dto.valorUnitario = new BigDecimal("399.00");

        when(repo.findById(9L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);

        Peca atualizada = service.update(9L, dto);

        assertSame(existente, atualizada);
        assertEquals("Bateria", atualizada.getDescricao());
        assertEquals(8, atualizada.getQuantidadeEstoque());
    }

    @Test
    void deveFalharAoAtualizarPecaInexistente() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.update(999L, new PecaDTO()));
    }

    @Test
    void deveListarTodasPecas() {
        when(repo.findAll()).thenReturn(List.of(new Peca(), new Peca()));
        List<Peca> pecas = service.listAll();
        assertEquals(2, pecas.size());
    }

    @Test
    void deveObterPecaPorId() {
        Peca peca = new Peca();
        when(repo.findById(10L)).thenReturn(Optional.of(peca));
        assertSame(peca, service.get(10L));
    }

    @Test
    void deveFalharAoObterPecaInexistente() {
        when(repo.findById(10L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.get(10L));
    }

    @Test
    void deveExcluirPecaPorId() {
        service.delete(55L);
        verify(repo).deleteById(55L);
    }
}

