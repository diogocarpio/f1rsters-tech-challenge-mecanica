package com.f1rsters.tech_challenge_mecanica.service;

import com.f1rsters.tech_challenge_mecanica.domain.Servico;
import com.f1rsters.tech_challenge_mecanica.dto.ServicoDTO;
import com.f1rsters.tech_challenge_mecanica.repository.ServicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoServiceTest {

    @Mock
    private ServicoRepository repo;

    @InjectMocks
    private ServicoService service;

    @Test
    void deveSalvarServicoComSucesso() {
        ServicoDTO dto = new ServicoDTO();
        dto.descricao = "Alinhamento";
        dto.valor = new BigDecimal("79.90");

        when(repo.save(any(Servico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Servico salvo = service.save(dto);

        assertEquals("Alinhamento", salvo.getDescricao());
        assertEquals(new BigDecimal("79.90"), salvo.getValor());
    }

    @Test
    void deveAtualizarServicoComSucesso() {
        Servico existente = new Servico();
        existente.setId(1L);

        ServicoDTO dto = new ServicoDTO();
        dto.descricao = "Balanceamento";
        dto.valor = new BigDecimal("69.00");

        when(repo.findById(1L)).thenReturn(Optional.of(existente));
        when(repo.save(existente)).thenReturn(existente);

        Servico atualizado = service.update(1L, dto);

        assertSame(existente, atualizado);
        assertEquals("Balanceamento", atualizado.getDescricao());
        assertEquals(new BigDecimal("69.00"), atualizado.getValor());
    }

    @Test
    void deveFalharAoAtualizarServicoInexistente() {
        when(repo.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.update(404L, new ServicoDTO()));
    }

    @Test
    void deveListarTodosServicos() {
        when(repo.findAll()).thenReturn(List.of(new Servico(), new Servico()));

        List<Servico> servicos = service.listAll();

        assertEquals(2, servicos.size());
    }

    @Test
    void deveObterServicoPorId() {
        Servico servico = new Servico();
        when(repo.findById(10L)).thenReturn(Optional.of(servico));

        assertSame(servico, service.get(10L));
    }

    @Test
    void deveFalharAoObterServicoInexistente() {
        when(repo.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.get(10L));
    }

    @Test
    void deveExcluirServicoPorId() {
        service.delete(77L);

        verify(repo).deleteById(77L);
    }
}

