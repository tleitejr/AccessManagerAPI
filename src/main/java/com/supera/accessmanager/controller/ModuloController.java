package com.supera.accessmanager.controller;

import com.supera.accessmanager.dto.modulo.ModuloResponse;
import com.supera.accessmanager.mapper.ModuloMapper;
import com.supera.accessmanager.service.ModuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modulos")
@RequiredArgsConstructor
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping
    public List<ModuloResponse> listar() {
        return moduloService.listarTodos()
                .stream()
                .map(ModuloMapper::toResponse)
                .toList();
    }
}
