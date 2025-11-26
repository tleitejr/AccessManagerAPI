package com.supera.accessmanager.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Falha de autenticação: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String json = """
            {
              "timestamp": "%s",
              "status": 401,
              "erro": "Acesso não autorizado",
              "mensagem": "Token inválido, expirado ou ausente.",
              "path": "%s"
            }
            """.formatted(LocalDateTime.now(), request.getRequestURI());

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
