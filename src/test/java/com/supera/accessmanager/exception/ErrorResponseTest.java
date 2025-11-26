package com.supera.accessmanager.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void deveCriarErrorResponseCorretamente() {
        LocalDateTime agora = LocalDateTime.now();

        ErrorResponse response = new ErrorResponse(
                agora,
                400,
                "Bad Request",
                "Campo inválido",
                "/api/teste"
        );

        assertThat(response.timestamp()).isEqualTo(agora);
        assertThat(response.status()).isEqualTo(400);
        assertThat(response.erro()).isEqualTo("Bad Request");
        assertThat(response.mensagem()).isEqualTo("Campo inválido");
        assertThat(response.path()).isEqualTo("/api/teste");
    }

    @Test
    void devePermitirCamposNulos() {
        ErrorResponse response = new ErrorResponse(
                null,
                0,
                null,
                null,
                null
        );

        assertThat(response.timestamp()).isNull();
        assertThat(response.status()).isZero();
        assertThat(response.erro()).isNull();
        assertThat(response.mensagem()).isNull();
        assertThat(response.path()).isNull();
    }
}
