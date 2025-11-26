package com.supera.accessmanager.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void deveConterMensagemCorreta() {
        BusinessException ex = new BusinessException("Erro de negócio");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Erro de negócio");
    }

    @Test
    void devePermitirMensagemNula() {
        BusinessException ex = new BusinessException(null);

        assertThat(ex.getMessage()).isNull();
    }
}
