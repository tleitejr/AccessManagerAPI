package com.supera.accessmanager.service.validator;

import org.springframework.stereotype.Component;

@Component
public class JustificativaValidator {

    public void validar(String justificativa) {
        if (justificativa == null) {
            throw new RuntimeException("A justificativa é obrigatória.");
        }

        int tamanho = justificativa.trim().length();

        if (tamanho < 20 || tamanho > 500) {
            throw new RuntimeException("A justificativa deve ter entre 20 e 500 caracteres.");
        }
    }
}
