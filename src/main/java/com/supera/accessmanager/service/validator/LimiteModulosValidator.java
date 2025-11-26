package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class LimiteModulosValidator {

    public void validar(Set<Modulo> modulos) {
        int tamanho = modulos.size();

        if (tamanho < 1 || tamanho > 3) {
            throw new RuntimeException("A solicitação deve conter entre 1 e 3 módulos.");
        }
    }
}
