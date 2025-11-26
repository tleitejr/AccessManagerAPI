package com.supera.accessmanager.service;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.repository.ModuloRepository;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuloServiceTest {

    @Mock
    private ModuloRepository moduloRepository;

    @InjectMocks
    private ModuloService moduloService;

    private Modulo modulo1;
    private Modulo modulo2;

    @BeforeEach
    void setup() {
        modulo1 = Instancio.of(Modulo.class)
                .ignore(Select.field(Modulo.class, "departamentosPermitidos"))
                .ignore(Select.field(Modulo.class, "modulosIncompativeis"))
                .set(Select.field(Modulo.class, "ativo"), true)
                .create();

        modulo2 = Instancio.of(Modulo.class)
                .ignore(Select.field(Modulo.class, "departamentosPermitidos"))
                .ignore(Select.field(Modulo.class, "modulosIncompativeis"))
                .set(Select.field(Modulo.class, "ativo"), true)
                .create();
    }

    @Test
    void deveRetornarModulosQuandoTodosExistem() {
        Set<Long> ids = Set.of(1L, 2L);

        modulo1.setId(1L);
        modulo2.setId(2L);

        List<Modulo> encontrados = List.of(modulo1, modulo2);

        when(moduloRepository.findAllById(ids)).thenReturn(encontrados);

        Set<Modulo> resultado = moduloService.buscarModulosPorIds(ids);

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(modulo1));
        assertTrue(resultado.contains(modulo2));

        verify(moduloRepository, times(1)).findAllById(ids);
    }

    @Test
    void deveLancarExcecaoQuandoAlgumModuloNaoExiste() {
        Set<Long> ids = Set.of(1L, 2L);

        modulo1.setId(1L);

        when(moduloRepository.findAllById(ids)).thenReturn(List.of(modulo1));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                moduloService.buscarModulosPorIds(ids)
        );

        assertEquals("Um ou mais módulos não foram encontrados", ex.getMessage());

        verify(moduloRepository, times(1)).findAllById(ids);
    }

    @Test
    void deveListarTodosOsModulos() {
        List<Modulo> lista = List.of(modulo1, modulo2);

        when(moduloRepository.findAll()).thenReturn(lista);

        List<Modulo> resultado = moduloService.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals(lista, resultado);

        verify(moduloRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistemModulos() {
        when(moduloRepository.findAll()).thenReturn(List.of());

        List<Modulo> resultado = moduloService.listarTodos();

        assertTrue(resultado.isEmpty());

        verify(moduloRepository, times(1)).findAll();
    }
}
