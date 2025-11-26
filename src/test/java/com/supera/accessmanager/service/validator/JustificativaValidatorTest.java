package com.supera.accessmanager.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JustificativaValidatorTest {

    private JustificativaValidator validator;

    @BeforeEach
    void setup() {
        validator = new JustificativaValidator();
    }

    @Test
    void deveValidarQuandoJustificativaValida() {
        String texto = "Esta justificativa possui mais de vinte caracteres.";

        assertDoesNotThrow(() -> validator.validar(texto));
    }

    @Test
    void deveLancarErroQuandoJustificativaNula() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(null));

        assertEquals("A justificativa é obrigatória.", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoJustificativaMenorQue20() {
        String texto = "Muito curta";

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(texto));

        assertEquals("A justificativa deve ter entre 20 e 500 caracteres.", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoJustificativaMaiorQue500() {
        String texto = "a".repeat(501);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(texto));

        assertEquals("A justificativa deve ter entre 20 e 500 caracteres.", ex.getMessage());
    }
}
