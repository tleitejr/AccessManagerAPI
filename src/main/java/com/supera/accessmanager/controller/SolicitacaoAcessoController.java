package com.supera.accessmanager.controller;

import com.supera.accessmanager.dto.solicitacao.*;
import com.supera.accessmanager.service.SolicitacaoAcessoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoAcessoController {

    private final SolicitacaoAcessoService solicitacaoService;

    @PostMapping
    public ResultadoSolicitacaoResponse criar(@RequestBody CriarSolicitacaoRequest request) {
        return solicitacaoService.criar(request);
    }

    @PostMapping("/renovar")
    public ResultadoSolicitacaoResponse renovar(@RequestBody SolicitarRenovacaoRequest request) {
        return solicitacaoService.renovar(request);
    }

    @DeleteMapping
    public void cancelar(@RequestBody CancelarSolicitacaoRequest request) {
        solicitacaoService.cancelar(request);
    }

    @GetMapping
    public Page<ListagemSolicitacaoResponse> listar(Pageable pageable) {
        return solicitacaoService.listarSolicitacoesDoUsuario(pageable);
    }

    @GetMapping("/{id}")
    public SolicitacaoResponse buscarPorId(@PathVariable Long id) {
        return solicitacaoService.buscarPorId(id);
    }
}
