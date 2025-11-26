package com.supera.accessmanager.dto.solicitacao;

import java.time.LocalDateTime;
import java.util.Set;

public record ListagemSolicitacaoResponse(
        Long id,
        String protocolo,
        Set<String> modulos,
        String status,
        String justificativa,
        boolean urgente,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataExpiracao,
        String motivoNegacao
) {}
