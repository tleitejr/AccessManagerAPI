package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DepartamentoValidator {

    public void validar(Usuario usuario, Set<Modulo> modulos) {

        modulos.forEach(modulo -> {
            boolean permitido = modulo.getDepartamentosPermitidos()
                    .contains(usuario.getDepartamento());

            if (!permitido) {
                throw new RuntimeException(
                        "O módulo " + modulo.getTipo() +
                                " não é permitido para seu departamento."
                );
            }
        });
    }
}
