// java
package com.supera.accessmanager.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationEntryPointTest {

    private final JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

    @Test
    void quandoCommence_deveRetornar401EConteudoJson() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.setRequestURI("/api/test/resource");

        AuthenticationException authEx = new AuthenticationException("Falha de autenticação") {
        };

        entryPoint.commence(request, response, authEx);

        assertEquals(401, response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());

        String body = response.getContentAsString();
        assertNotNull(body);

        assertTrue(body.contains("\"timestamp\""), "Deve conter campo timestamp");
        assertTrue(body.contains("\"status\": 401"), "Deve conter status 401");
        assertTrue(body.contains("\"erro\": \"Acesso não autorizado\""), "Deve conter mensagem de erro padronizada");
        assertTrue(body.contains("\"mensagem\": \"Token inválido, expirado ou ausente.\""),
                "Deve conter mensagem explicativa sobre token");
        assertTrue(body.contains("\"path\": \"/api/test/resource\""), "Deve conter o path da requisição");
    }
}
