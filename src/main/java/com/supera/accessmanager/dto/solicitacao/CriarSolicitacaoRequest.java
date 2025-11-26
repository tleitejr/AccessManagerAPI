package com.supera.accessmanager.dto.solicitacao;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CriarSolicitacaoRequest(

        @NotEmpty(message = "Selecione pelo menos 1 módulo")
        @Size(min = 1, max = 3, message = "É permitido solicitar entre 1 e 3 módulos")
        Set<Long> modulosIds,

        @NotNull
        @Size(min = 20, max = 500, message = "A justificativa deve ter entre 20 e 500 caracteres")
        String justificativa,

        boolean urgente
) {}
