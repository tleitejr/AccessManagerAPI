package com.supera.accessmanager.service;

import com.supera.accessmanager.dto.auth.LoginRequest;
import com.supera.accessmanager.dto.auth.LoginResponse;
import com.supera.accessmanager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest request;

    @BeforeEach
    void setup() {
        request = new LoginRequest("email@teste.com", "senha123");
    }

    @Test
    void deveAutenticarComSucesso() {
        String tokenGerado = "jwt-token-teste";
        Long expiracao = 3600L;

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        Authentication authenticationMock = mock(Authentication.class);

        when(authenticationManager.authenticate(eq(authToken)))
                .thenReturn(authenticationMock);

        when(jwtService.gerarToken(request.email())).thenReturn(tokenGerado);

        when(jwtService.getExpiracao()).thenReturn(expiracao);

        LoginResponse response = authService.autenticar(request);

        assertNotNull(response);
        assertEquals(tokenGerado, response.token());
        assertEquals("Bearer", response.tipo());
        assertEquals(expiracao, response.expiresIn());

        verify(authenticationManager, times(1)).authenticate(eq(authToken));
        verify(jwtService, times(1)).gerarToken(request.email());
        verify(jwtService, times(1)).getExpiracao();
    }

    @Test
    void deveLancarErroQuandoAutenticacaoFalha() {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        doThrow(new RuntimeException("Falha na autenticação"))
                .when(authenticationManager).authenticate(eq(authToken));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.autenticar(request));

        assertEquals("Falha na autenticação", ex.getMessage());

        verify(authenticationManager, times(1)).authenticate(eq(authToken));
        verify(jwtService, times(0)).gerarToken(anyString());
        verify(jwtService, times(0)).getExpiracao();
    }
}
