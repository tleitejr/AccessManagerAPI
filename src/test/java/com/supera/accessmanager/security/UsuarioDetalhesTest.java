package com.supera.accessmanager.security;

import com.supera.accessmanager.domain.usuario.Departamento;
import com.supera.accessmanager.domain.usuario.Papel;
import com.supera.accessmanager.domain.usuario.Usuario;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioDetalhesTest {

    private UsuarioDetalhes usuarioDetalhes;

    @BeforeEach
    void setup() {
        Usuario usuario = Instancio.of(Usuario.class)
                .set(Select.field("id"), 1L)
                .set(Select.field("nome"), "Admin")
                .set(Select.field("email"), "teste@exemplo.com")
                .set(Select.field("senha"), "123456")
                .set(Select.field("departamento"), Departamento.TI)
                .set(Select.field("papeis"), Set.of(Papel.ADMIN, Papel.USUARIO))
                .create();

        usuarioDetalhes = new UsuarioDetalhes(usuario);
    }

    @Test
    void deveRetornarAuthoritiesCorretas() {
        var authorities = usuarioDetalhes.getAuthorities();

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USUARIO");
    }


    @Test
    void deveRetornarSenhaCorreta() {
        assertThat(usuarioDetalhes.getPassword()).isEqualTo("123456");
    }

    @Test
    void deveRetornarEmailComoUsername() {
        assertThat(usuarioDetalhes.getUsername()).isEqualTo("teste@exemplo.com");
    }

    @Test
    void deveRetornarFlagsCorretas() {
        assertThat(usuarioDetalhes.isAccountNonExpired()).isTrue();
        assertThat(usuarioDetalhes.isAccountNonLocked()).isTrue();
        assertThat(usuarioDetalhes.isCredentialsNonExpired()).isTrue();
        assertThat(usuarioDetalhes.isEnabled()).isTrue();
    }
}
