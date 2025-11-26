package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.usuario.Departamento;
import com.supera.accessmanager.domain.usuario.Papel;
import com.supera.accessmanager.domain.usuario.Usuario;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DepartamentoValidatorTest {

    private DepartamentoValidator validator;

    private Usuario usuario;
    private Modulo moduloPermitido;
    private Modulo moduloNaoPermitido;

    @BeforeEach
    void setup() {
        validator = new DepartamentoValidator();

        usuario = Instancio.of(Usuario.class)
                .set(Select.field("id"), 1L)
                .set(Select.field("nome"), "Admin")
                .set(Select.field("email"), "admin@empresa.com")
                .set(Select.field("senha"), "$2a$10$BUH6GeEU20oiZG7nMIoi8.SNOaX9aZdoFW9QK8yvkEMf7bhOMNfKe")
                .set(Select.field("departamento"), Departamento.TI)
                .set(Select.field("papeis"), Set.of(Papel.ADMIN))
                .create();

        moduloPermitido = Instancio.of(Modulo.class)
                .ignore(Select.field(Modulo.class, "modulosIncompativeis")) // evita loop
                .set(Select.field(Modulo.class, "departamentosPermitidos"),
                        Set.of(usuario.getDepartamento()))
                .create();

        moduloNaoPermitido = Instancio.of(Modulo.class)
                .ignore(Select.field(Modulo.class, "modulosIncompativeis"))
                .set(Select.field(Modulo.class, "departamentosPermitidos"),
                        Set.of(Departamento.RH)) // qualquer depto diferente
                .create();
    }

    @Test
    void deveValidarComSucessoQuandoTodosModulosSaoPermitidos() {
        Set<Modulo> modulos = Set.of(moduloPermitido);

        assertDoesNotThrow(() ->
                validator.validar(usuario, modulos)
        );
    }

    @Test
    void deveLancarErroQuandoModuloNaoEhPermitido() {
        Set<Modulo> modulos = Set.of(moduloNaoPermitido);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                validator.validar(usuario, modulos)
        );

        String expectedMsg = "O módulo " +
                moduloNaoPermitido.getTipo() +
                " não é permitido para seu departamento.";

        assertEquals(expectedMsg, ex.getMessage());
    }

    @Test
    void deveLancarErroSeApenasUmModuloNaoForPermitido() {
        Set<Modulo> modulos = Set.of(moduloPermitido, moduloNaoPermitido);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                validator.validar(usuario, modulos)
        );

        String expectedMsg = "O módulo " +
                moduloNaoPermitido.getTipo() +
                " não é permitido para seu departamento.";

        assertEquals(expectedMsg, ex.getMessage());
    }
}
