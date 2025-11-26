package com.supera.accessmanager.dto.solicitacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelarSolicitacaoRequest(
        @NotNull Long id,

        @Size(min = 10, max = 200, message = "Motivo deve ter entre 10 e 200 caracteres")
        String motivo
) {}
