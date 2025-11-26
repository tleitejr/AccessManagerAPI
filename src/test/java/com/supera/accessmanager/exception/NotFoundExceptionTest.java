package com.supera.accessmanager.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotFoundExceptionTest {

    @Test
    void deveConterMensagemCorreta() {
        NotFoundException ex = new NotFoundException("Usuário não encontrado");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Usuário não encontrado");
    }

    @Test
    void devePermitirMensagemNula() {
        NotFoundException ex = new NotFoundException(null);

        assertThat(ex.getMessage()).isNull();
    }
}
