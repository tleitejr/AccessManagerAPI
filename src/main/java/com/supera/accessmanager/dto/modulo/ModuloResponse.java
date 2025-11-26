package com.supera.accessmanager.dto.modulo;

import java.util.Set;

public record ModuloResponse(
        Long id,
        String tipo,
        String descricao,
        boolean ativo,
        Set<String> departamentosPermitidos,
        Set<String> modulosIncompativeis
) {}
