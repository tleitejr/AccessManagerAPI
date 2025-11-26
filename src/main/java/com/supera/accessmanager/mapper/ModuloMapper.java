package com.supera.accessmanager.mapper;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.dto.modulo.ModuloResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ModuloMapper {

    public static ModuloResponse toResponse(Modulo modulo) {
        return new ModuloResponse(
                modulo.getId(),
                modulo.getTipo().name(),
                modulo.getDescricao(),
                modulo.isAtivo(),
                modulo.getDepartamentosPermitidos()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet()),
                modulo.getModulosIncompativeis()
                        .stream()
                        .map(m -> m.getTipo().name())
                        .collect(Collectors.toSet())
        );
    }
}
