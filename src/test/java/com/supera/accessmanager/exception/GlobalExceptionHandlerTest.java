package com.supera.accessmanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();

        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/teste");
    }

    @Test
    void deveTratarRuntimeException() {
        RuntimeException ex = new RuntimeException("Erro qualquer");

        var response = handler.handleRuntime(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(400);
        assertThat(body.erro()).isEqualTo("Bad Request");
        assertThat(body.mensagem()).isEqualTo("Erro qualquer");
        assertThat(body.path()).isEqualTo("/api/teste");
        assertThat(body.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void deveTratarBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Credenciais incorretas");

        var response = handler.handleCredenciais(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.status()).isEqualTo(401);
        assertThat(body.erro()).isEqualTo("Unauthorized");
        assertThat(body.mensagem()).isEqualTo("Credenciais inválidas");
        assertThat(body.path()).isEqualTo("/api/teste");
    }

    @Test
    void deveTratarMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "email", "inválido");

        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        MethodParameter methodParameter = mock(MethodParameter.class);

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        var response = handler.handleValidacao(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        var body = response.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.mensagem()).isEqualTo("email: inválido");
    }

    @Test
    void deveTratarConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Valor inválido");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        var response = handler.handleConstraint(ex, request);

        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().mensagem()).isEqualTo("Valor inválido");
    }

    @Test
    void deveTratarNotFoundException() {
        NotFoundException ex = new NotFoundException("Recurso não encontrado");

        var response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        var body = response.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.mensagem()).isEqualTo("Recurso não encontrado");
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.erro()).isEqualTo("Not Found");
    }

    @Test
    void deveTratarBusinessException() {
        BusinessException ex = new BusinessException("Regra de negócio falhou");

        var response = handler.handleBusiness(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        var body = response.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.mensagem()).isEqualTo("Regra de negócio falhou");
        assertThat(body.erro()).isEqualTo("Bad Request");
    }
}
