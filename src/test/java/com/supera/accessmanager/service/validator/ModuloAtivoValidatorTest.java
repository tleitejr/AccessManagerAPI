package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.modulo.TipoModulo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModuloAtivoValidatorTest {

    private ModuloAtivoValidator validator;

    @BeforeEach
    void setup() {
        validator = new ModuloAtivoValidator();
    }

    @Test
    void devePermitirQuandoTodosModulosEstaoAtivos() {
        Set<Modulo> modulos = Set.of(
                Modulo.builder().id(1L).ativo(true).tipo(TipoModulo.APROVADOR_FINANCEIRO).build(),
                Modulo.builder().id(2L).ativo(true).tipo(TipoModulo.GESTAO_FINANCEIRA).build()
        );

        assertDoesNotThrow(() -> validator.validar(modulos));
    }

    @Test
    void deveLancarErroQuandoAlgumModuloEstaInativo() {
        Modulo inativo = Modulo.builder()
                .id(99L)
                .ativo(false)
                .tipo(TipoModulo.ADMINISTRADOR_RH)
                .build();

        Set<Modulo> modulos = Set.of(inativo);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(modulos));

        assertEquals("O módulo " + inativo.getTipo() + " está inativo.", ex.getMessage());
    }
}
