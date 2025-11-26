package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LimiteModulosValidatorTest {

    private LimiteModulosValidator validator;

    @BeforeEach
    void setup() {
        validator = new LimiteModulosValidator();
    }

    @Test
    void devePermitirQuandoQuantidadeEntre1e3() {
        Set<Modulo> modulos = Set.of(
                Modulo.builder().id(1L).build(),
                Modulo.builder().id(2L).build()
        );

        assertDoesNotThrow(() -> validator.validar(modulos));
    }

    @Test
    void deveLancarErroQuandoNaoTemNenhumModulo() {
        Set<Modulo> modulos = Set.of();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(modulos));

        assertEquals("A solicitação deve conter entre 1 e 3 módulos.", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoTemMaisQue3Modulos() {
        Set<Modulo> modulos = Set.of(
                Modulo.builder().id(1L).build(),
                Modulo.builder().id(2L).build(),
                Modulo.builder().id(3L).build(),
                Modulo.builder().id(4L).build()
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(modulos));

        assertEquals("A solicitação deve conter entre 1 e 3 módulos.", ex.getMessage());
    }
}
