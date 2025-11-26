package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ModuloAtivoValidator {

    public void validar(Set<Modulo> modulos) {
        modulos.forEach(modulo -> {
            if (!modulo.isAtivo()) {
                throw new RuntimeException("O módulo " + modulo.getTipo() + " está inativo.");
            }
        });
    }
}
