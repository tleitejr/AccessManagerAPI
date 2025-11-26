package com.supera.accessmanager.mapper;

import com.supera.accessmanager.domain.solicitacao.SolicitacaoAcesso;
import com.supera.accessmanager.dto.solicitacao.ListagemSolicitacaoResponse;
import com.supera.accessmanager.dto.solicitacao.ResultadoSolicitacaoResponse;
import com.supera.accessmanager.dto.solicitacao.SolicitacaoResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SolicitacaoMapper {

    public static SolicitacaoResponse toResponse(SolicitacaoAcesso s) {
        return new SolicitacaoResponse(
                s.getId(),
                s.getProtocolo(),
                s.getModulosSolicitados()
                        .stream()
                        .map(m -> m.getTipo().name())
                        .collect(Collectors.toSet()),
                s.getJustificativa(),
                s.isUrgente(),
                s.getStatus().name(),
                s.getMotivoNegacao(),
                s.getDataSolicitacao(),
                s.getDataExpiracao()
        );
    }

    public static ListagemSolicitacaoResponse toListagem(SolicitacaoAcesso s) {
        return new ListagemSolicitacaoResponse(
                s.getId(),
                s.getProtocolo(),
                s.getModulosSolicitados()
                        .stream()
                        .map(m -> m.getTipo().name())
                        .collect(Collectors.toSet()),
                s.getStatus().name(),
                s.getJustificativa(),
                s.isUrgente(),
                s.getDataSolicitacao(),
                s.getDataExpiracao(),
                s.getMotivoNegacao()
        );
    }

    public static ResultadoSolicitacaoResponse resultadoAprovado(SolicitacaoAcesso s) {
        return new ResultadoSolicitacaoResponse(
                true,
                "Solicitação criada com sucesso! Protocolo: " + s.getProtocolo()
                        + ". Seus acessos já estão disponíveis!",
                s.getProtocolo(),
                null
        );
    }

    public static ResultadoSolicitacaoResponse resultadoNegado(SolicitacaoAcesso s) {
        return new ResultadoSolicitacaoResponse(
                false,
                "Solicitação negada. Motivo: " + s.getMotivoNegacao(),
                s.getProtocolo(),
                s.getMotivoNegacao()
        );
    }
}