package com.supera.accessmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supera.accessmanager.dto.solicitacao.*;
import com.supera.accessmanager.service.SolicitacaoAcessoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(SolicitacaoAcessoController.class)
@AutoConfigureMockMvc(addFilters = false)
class SolicitacaoAcessoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SolicitacaoAcessoService solicitacaoService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void deveCriarSolicitacaoComSucesso() throws Exception {

        CriarSolicitacaoRequest request = new CriarSolicitacaoRequest(
                Set.of(1L, 2L),
                "Justificativa bem grande para validar corretamente",
                false
        );

        ResultadoSolicitacaoResponse response = new ResultadoSolicitacaoResponse(
                true,
                "Solicitação criada!",
                "SOL-20250101-1234",
                null
        );

        when(solicitacaoService.criar(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/solicitacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.aprovado").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mensagem").value("Solicitação criada!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.protocolo").value("SOL-20250101-1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.motivoNegacao").isEmpty());

        verify(solicitacaoService).criar(request);
    }

    @Test
    void deveRenovarSolicitacaoComSucesso() throws Exception {

        SolicitarRenovacaoRequest request = new SolicitarRenovacaoRequest(10L);

        ResultadoSolicitacaoResponse response = new ResultadoSolicitacaoResponse(
                true,
                "Renovada",
                "SOL-20250101-9999",
                null
        );

        when(solicitacaoService.renovar(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/solicitacoes/renovar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.protocolo").value("SOL-20250101-9999"));

        verify(solicitacaoService).renovar(request);
    }

    @Test
    void deveCancelarSolicitacao() throws Exception {

        CancelarSolicitacaoRequest request = new CancelarSolicitacaoRequest(
                15L,
                "Não preciso mais"
        );

        doNothing().when(solicitacaoService).cancelar(request);

        mockMvc.perform(delete("/api/v1/solicitacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(solicitacaoService).cancelar(request);
    }

    @Test
    void deveListarSolicitacoesPaginado() throws Exception {

        ListagemSolicitacaoResponse item = new ListagemSolicitacaoResponse(
                1L, "SOL-20250101-1234", Set.of("GESTAO_FINANCEIRA"),
                "APROVADA", "Justificativa X", false,
                LocalDateTime.now(), LocalDateTime.now().plusDays(5),
                null
        );

        Page<ListagemSolicitacaoResponse> page =
                new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1);

        when(solicitacaoService.listarSolicitacoesDoUsuario(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/solicitacoes?page=0&size=10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].protocolo").value("SOL-20250101-1234"));

        verify(solicitacaoService).listarSolicitacoesDoUsuario(any(Pageable.class));
    }

    @Test
    void deveBuscarSolicitacaoPorId() throws Exception {

        SolicitacaoResponse response = new SolicitacaoResponse(
                1L,
                "SOL-20250101-1234",
                Set.of("GESTAO_FINANCEIRA"),
                "Teste",
                false,
                "APROVADA",
                null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(5)
        );

        when(solicitacaoService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/solicitacoes/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));

        verify(solicitacaoService).buscarPorId(1L);
    }
}
