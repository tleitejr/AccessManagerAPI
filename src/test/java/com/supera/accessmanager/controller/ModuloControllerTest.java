package com.supera.accessmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.modulo.TipoModulo;
import com.supera.accessmanager.dto.modulo.ModuloResponse;
import com.supera.accessmanager.service.ModuloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ModuloController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModuloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModuloService moduloService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void deveListarTodosOsModulos() throws Exception {

        Modulo m1 = Modulo.builder()
                .id(1L)
                .tipo(TipoModulo.GESTAO_FINANCEIRA)
                .descricao("Módulo de Gestão Financeira")
                .ativo(true)
                .departamentosPermitidos(Set.of())
                .modulosIncompativeis(Set.of())
                .build();

        Modulo m2 = Modulo.builder()
                .id(2L)
                .tipo(TipoModulo.AUDITORIA)
                .descricao("Módulo de Auditoria")
                .ativo(true)
                .departamentosPermitidos(Set.of())
                .modulosIncompativeis(Set.of())
                .build();

        when(moduloService.listarTodos()).thenReturn(List.of(m1, m2));

        var result = mockMvc.perform(get("/api/v1/modulos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var json = result.getResponse().getContentAsString();

        List<ModuloResponse> resposta = mapper.readValue(
                json,
                mapper.getTypeFactory().constructCollectionType(List.class, ModuloResponse.class)
        );

        assertThat(resposta).hasSize(2);

        assertThat(resposta.get(0).id()).isEqualTo(1L);
        assertThat(resposta.get(0).tipo()).isEqualTo("GESTAO_FINANCEIRA");

        assertThat(resposta.get(1).id()).isEqualTo(2L);
        assertThat(resposta.get(1).tipo()).isEqualTo("AUDITORIA");

        verify(moduloService).listarTodos();
    }
}

