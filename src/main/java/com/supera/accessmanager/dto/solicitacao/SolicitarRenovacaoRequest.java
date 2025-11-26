package com.supera.accessmanager.dto.solicitacao;

import jakarta.validation.constraints.NotNull;

public record SolicitarRenovacaoRequest(
        @NotNull Long idSolicitacaoAnterior
) {}
