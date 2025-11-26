package com.supera.accessmanager.service;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.solicitacao.SolicitacaoAcesso;
import com.supera.accessmanager.domain.solicitacao.StatusSolicitacao;
import com.supera.accessmanager.domain.usuario.Usuario;
import com.supera.accessmanager.dto.solicitacao.*;
import com.supera.accessmanager.exception.BusinessException;
import com.supera.accessmanager.exception.NotFoundException;
import com.supera.accessmanager.mapper.SolicitacaoMapper;
import com.supera.accessmanager.repository.SolicitacaoAcessoRepository;
import com.supera.accessmanager.service.validator.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SolicitacaoAcessoService {

    private final UsuarioService usuarioService;
    private final ModuloService moduloService;
    private final SolicitacaoAcessoRepository solicitacaoRepository;

    private final ModuloAtivoValidator moduloAtivoValidator;
    private final IncompatibilidadeValidator incompatibilidadeValidator;
    private final LimiteModulosValidator limiteModulosValidator;
    private final DepartamentoValidator departamentoValidator;
    private final JustificativaValidator justificativaValidator;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();

    public ResultadoSolicitacaoResponse criar(CriarSolicitacaoRequest request) {

        Usuario usuario = usuarioService.getUsuarioAutenticado();
        Set<Modulo> modulos = moduloService.buscarModulosPorIds(request.modulosIds());

        limiteModulosValidator.validar(modulos);
        justificativaValidator.validar(request.justificativa());
        moduloAtivoValidator.validar(modulos);
        departamentoValidator.validar(usuario, modulos);
        incompatibilidadeValidator.validar(modulos);

        SolicitacaoAcesso s = SolicitacaoAcesso.builder()
                .usuario(usuario)
                .modulosSolicitados(modulos)
                .justificativa(request.justificativa())
                .urgente(request.urgente())
                .status(StatusSolicitacao.APROVADA)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(180))
                .protocolo(gerarProtocolo())
                .build();

        solicitacaoRepository.save(s);

        return SolicitacaoMapper.resultadoAprovado(s);
    }

    public ResultadoSolicitacaoResponse renovar(SolicitarRenovacaoRequest request) {

        Usuario usuario = usuarioService.getUsuarioAutenticado();

        SolicitacaoAcesso anterior = solicitacaoRepository.findById(request.idSolicitacaoAnterior())
                .orElseThrow(() -> new NotFoundException("Solicitação anterior não encontrada"));

        if (!anterior.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("A solicitação não pertence ao usuário autenticado.");
        }

        if (anterior.getStatus() != StatusSolicitacao.APROVADA) {
            throw new BusinessException("Somente solicitações aprovadas podem ser renovadas.");
        }

        long diasRestantes =
                java.time.Duration.between(LocalDateTime.now(), anterior.getDataExpiracao()).toDays();

        if (diasRestantes > 30) {
            throw new BusinessException("A renovação só é permitida quando restarem 30 dias ou menos para expirar.");
        }

        SolicitacaoAcesso nova = SolicitacaoAcesso.builder()
                .usuario(usuario)
                .modulosSolicitados(anterior.getModulosSolicitados())
                .justificativa("Renovação da solicitação " + anterior.getProtocolo())
                .urgente(false)
                .status(StatusSolicitacao.APROVADA)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(LocalDateTime.now().plusDays(180))
                .protocolo(gerarProtocolo())
                .solicitacaoAnterior(anterior)
                .build();

        solicitacaoRepository.save(nova);

        return SolicitacaoMapper.resultadoAprovado(nova);
    }

    public void cancelar(CancelarSolicitacaoRequest request) {

        SolicitacaoAcesso s = solicitacaoRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("Solicitação não encontrada"));

        Usuario usuario = usuarioService.getUsuarioAutenticado();

        if (!s.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("A solicitação não pertence ao usuário autenticado.");
        }

        s.setStatus(StatusSolicitacao.CANCELADA);
        s.setMotivoNegacao(request.motivo());

        solicitacaoRepository.save(s);
    }

    public Page<ListagemSolicitacaoResponse> listarSolicitacoesDoUsuario(Pageable pageable) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();

        return solicitacaoRepository.findByUsuarioId(usuario.getId(), pageable)
                .map(SolicitacaoMapper::toListagem);
    }

    public SolicitacaoResponse buscarPorId(Long id) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();

        SolicitacaoAcesso s = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Solicitação não encontrada"));

        if (!s.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("A solicitação não pertence ao usuário autenticado.");
        }

        return SolicitacaoMapper.toResponse(s);
    }

    private String gerarProtocolo() {
        String date = LocalDateTime.now().format(DF);
        int seq = RANDOM.nextInt(9999) + 1;
        return "SOL-" + date + "-" + String.format("%04d", seq);
    }
}
