package com.supera.accessmanager.dto.solicitacao;

import java.time.LocalDateTime;
import java.util.Set;

public record SolicitacaoResponse(
        Long id,
        String protocolo,
        Set<String> modulos,
        String justificativa,
        boolean urgente,
        String status,
        String motivoNegacao,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataExpiracao
) {}
