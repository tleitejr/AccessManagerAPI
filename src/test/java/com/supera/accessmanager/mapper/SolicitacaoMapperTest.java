package com.supera.accessmanager.mapper;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.modulo.TipoModulo;
import com.supera.accessmanager.domain.solicitacao.SolicitacaoAcesso;
import com.supera.accessmanager.domain.solicitacao.StatusSolicitacao;
import com.supera.accessmanager.dto.solicitacao.ListagemSolicitacaoResponse;
import com.supera.accessmanager.dto.solicitacao.ResultadoSolicitacaoResponse;
import com.supera.accessmanager.dto.solicitacao.SolicitacaoResponse;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class SolicitacaoMapperTest {

    private SolicitacaoAcesso solicitacao;
    private Modulo modulo1;
    private Modulo modulo2;

    @BeforeEach
    void setup() {
        modulo1 = Instancio.of(Modulo.class)
                .set(field(Modulo.class, "tipo"), TipoModulo.PORTAL_COLABORADOR)
                .create();

        modulo2 = Instancio.of(Modulo.class)
                .set(field(Modulo.class, "tipo"), TipoModulo.GESTAO_FINANCEIRA)
                .create();

        solicitacao = SolicitacaoAcesso.builder()
                .id(10L)
                .protocolo("SOL-20250101-1234")
                .justificativa("Acesso necessário para tarefas internas")
                .urgente(true)
                .status(StatusSolicitacao.APROVADA)
                .motivoNegacao(null)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(30))
                .modulosSolicitados(Set.of(modulo1, modulo2))
                .build();
    }

    @Test
    void deveMapearParaSolicitacaoResponse() {
        SolicitacaoResponse r = SolicitacaoMapper.toResponse(solicitacao);

        assertThat(r.id()).isEqualTo(solicitacao.getId());
        assertThat(r.protocolo()).isEqualTo("SOL-20250101-1234");
        assertThat(r.modulos())
                .containsExactlyInAnyOrder(
                        "PORTAL_COLABORADOR",
                        "GESTAO_FINANCEIRA"
                );
        assertThat(r.justificativa()).isEqualTo(solicitacao.getJustificativa());
        assertThat(r.urgente()).isTrue();
        assertThat(r.status()).isEqualTo("APROVADA");
        assertThat(r.motivoNegacao()).isNull();
        assertThat(r.dataSolicitacao()).isEqualTo(solicitacao.getDataSolicitacao());
        assertThat(r.dataExpiracao()).isEqualTo(solicitacao.getDataExpiracao());
    }

    @Test
    void deveMapearParaListagemSolicitacaoResponse() {
        ListagemSolicitacaoResponse r = SolicitacaoMapper.toListagem(solicitacao);

        assertThat(r.id()).isEqualTo(10L);
        assertThat(r.protocolo()).isEqualTo("SOL-20250101-1234");
        assertThat(r.modulos())
                .containsExactlyInAnyOrder(
                        "PORTAL_COLABORADOR",
                        "GESTAO_FINANCEIRA"
                );
        assertThat(r.status()).isEqualTo("APROVADA");
        assertThat(r.justificativa()).isEqualTo("Acesso necessário para tarefas internas");
        assertThat(r.urgente()).isTrue();
        assertThat(r.dataSolicitacao()).isEqualTo(solicitacao.getDataSolicitacao());
        assertThat(r.dataExpiracao()).isEqualTo(solicitacao.getDataExpiracao());
        assertThat(r.motivoNegacao()).isNull();
    }

    @Test
    void deveMapearResultadoAprovado() {
        ResultadoSolicitacaoResponse r = SolicitacaoMapper.resultadoAprovado(solicitacao);

        assertThat(r.aprovado()).isTrue();
        assertThat(r.protocolo()).isEqualTo("SOL-20250101-1234");
        assertThat(r.motivoNegacao()).isNull();
        assertThat(r.mensagem())
                .isEqualTo("Solicitação criada com sucesso! Protocolo: SOL-20250101-1234. Seus acessos já estão disponíveis!");
    }

    @Test
    void deveMapearResultadoNegado() {
        solicitacao.setMotivoNegacao("Usuário não possui permissão");

        ResultadoSolicitacaoResponse r = SolicitacaoMapper.resultadoNegado(solicitacao);

        assertThat(r.aprovado()).isFalse();
        assertThat(r.protocolo()).isEqualTo("SOL-20250101-1234");
        assertThat(r.motivoNegacao()).isEqualTo("Usuário não possui permissão");
        assertThat(r.mensagem())
                .isEqualTo("Solicitação negada. Motivo: Usuário não possui permissão");
    }
}
