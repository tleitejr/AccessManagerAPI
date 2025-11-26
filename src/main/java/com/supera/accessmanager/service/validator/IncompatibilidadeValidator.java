package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class IncompatibilidadeValidator {

    public void validar(Set<Modulo> modulosSolicitados) {

        for (Modulo m1 : modulosSolicitados) {
            for (Modulo m2 : modulosSolicitados) {

                if (m1 == m2) continue;

                boolean incompat = m1.getModulosIncompativeis()
                        .contains(m2);

                if (incompat) {
                    throw new RuntimeException(
                            "Os módulos " + m1.getTipo() +
                                    " e " + m2.getTipo() +
                                    " são incompatíveis."
                    );
                }
            }
        }
    }
}
