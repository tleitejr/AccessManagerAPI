package com.supera.accessmanager.service;

import com.supera.accessmanager.domain.usuario.Departamento;
import com.supera.accessmanager.domain.usuario.Papel;
import com.supera.accessmanager.domain.usuario.Usuario;
import com.supera.accessmanager.repository.UsuarioRepository;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = Instancio.of(Usuario.class)
                .set(Select.field("id"), 1L)
                .set(Select.field("nome"), "Admin")
                .set(Select.field("email"), "admin@empresa.com")
                .set(Select.field("senha"), "$2a$10$BUH6GeEU20oiZG7nMIoi8.SNOaX9aZdoFW9QK8yvkEMf7bhOMNfKe")
                .set(Select.field("departamento"), Departamento.TI)
                .set(Select.field("papeis"), Set.of(Papel.ADMIN))
                .create();

        SecurityContextHolder.clearContext();
    }

    @Test
    void deveRetornarUsuarioAutenticado() {
        String email = "teste@exemplo.com";
        usuario.setEmail(email);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.getUsuarioAutenticado();

        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());

        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    void deveLancarErroQuandoUsuarioNaoExiste() {
        String email = "faltando@exemplo.com";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);

        SecurityContextHolder.setContext(securityContext);

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.getUsuarioAutenticado());

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoAuthenticationEhNull() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(NullPointerException.class,
                () -> usuarioService.getUsuarioAutenticado());
    }

    @Test
    void deveLancarErroQuandoEmailEhNull() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> usuarioService.getUsuarioAutenticado());

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}
