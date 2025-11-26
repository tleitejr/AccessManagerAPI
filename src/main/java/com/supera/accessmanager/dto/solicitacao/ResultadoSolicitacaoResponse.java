package com.supera.accessmanager.dto.solicitacao;

public record ResultadoSolicitacaoResponse(
        boolean aprovado,
        String mensagem,
        String protocolo,
        String motivoNegacao
) {}
