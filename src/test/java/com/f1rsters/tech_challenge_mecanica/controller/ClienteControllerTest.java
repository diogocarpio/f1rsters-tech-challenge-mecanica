package com.f1rsters.tech_challenge_mecanica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1rsters.tech_challenge_mecanica.dto.ClienteDTO;
import com.f1rsters.tech_challenge_mecanica.domain.Cliente;
import com.f1rsters.tech_challenge_mecanica.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente createCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        return cliente;
    }

    private ClienteDTO createDTO() {
        ClienteDTO dto = new ClienteDTO();
        dto.nome = "João Silva";
        return dto;
    }

    @Test
    void shouldCreateCliente() throws Exception {

        Cliente cliente = createCliente();

        when(clienteService.save(any())).thenReturn(cliente);

        mockMvc.perform(
                        post("/api/admin/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDTO()))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldListClientes() throws Exception {

        when(clienteService.listAll()).thenReturn(List.of(createCliente()));

        mockMvc.perform(get("/api/admin/clientes"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetClienteById() throws Exception {

        when(clienteService.get(1L)).thenReturn(createCliente());

        mockMvc.perform(get("/api/admin/clientes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateCliente() throws Exception {

        when(clienteService.update(eq(1L), any())).thenReturn(createCliente());

        mockMvc.perform(
                        put("/api/admin/clientes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDTO()))
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteCliente() throws Exception {

        doNothing().when(clienteService).delete(1L);

        mockMvc.perform(delete("/api/admin/clientes/1"))
                .andExpect(status().isOk());
    }
}