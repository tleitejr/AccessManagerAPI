package com.supera.accessmanager.service;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ModuloService {

    private final ModuloRepository moduloRepository;

    public Set<Modulo> buscarModulosPorIds(Set<Long> ids) {
        List<Modulo> encontrados = moduloRepository.findAllById(ids);

        if (encontrados.size() != ids.size()) {
            throw new RuntimeException("Um ou mais módulos não foram encontrados");
        }

        return Set.copyOf(encontrados);
    }

    public List<Modulo> listarTodos() {
        return moduloRepository.findAll();
    }

}
