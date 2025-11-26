package com.supera.accessmanager.service;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.modulo.TipoModulo;
import com.supera.accessmanager.domain.solicitacao.SolicitacaoAcesso;
import com.supera.accessmanager.domain.solicitacao.StatusSolicitacao;
import com.supera.accessmanager.domain.usuario.Departamento;
import com.supera.accessmanager.domain.usuario.Papel;
import com.supera.accessmanager.domain.usuario.Usuario;
import com.supera.accessmanager.dto.solicitacao.*;
import com.supera.accessmanager.exception.BusinessException;
import com.supera.accessmanager.exception.NotFoundException;
import com.supera.accessmanager.repository.SolicitacaoAcessoRepository;
import com.supera.accessmanager.service.validator.*;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoAcessoServiceTest {

    @InjectMocks
    private SolicitacaoAcessoService service;

    @Mock private UsuarioService usuarioService;
    @Mock private ModuloService moduloService;
    @Mock private SolicitacaoAcessoRepository solicitacaoRepository;

    @Mock private ModuloAtivoValidator moduloAtivoValidator;
    @Mock private IncompatibilidadeValidator incompatibilidadeValidator;
    @Mock private LimiteModulosValidator limiteModulosValidator;
    @Mock private DepartamentoValidator departamentoValidator;
    @Mock private JustificativaValidator justificativaValidator;

    private Usuario usuario;
    private Set<Modulo> modulos;

    @BeforeEach
    void setup() {
        usuario = Instancio.of(Usuario.class)
                .set(Select.field("id"), 1L)
                .set(Select.field("nome"), "Admin")
                .set(Select.field("email"), "admin@empresa.com")
                .set(Select.field("senha"), "$2a$10$BUH6GeEU20oiZG7nMIoi8.SNOaX9aZdoFW9QK8yvkEMf7bhOMNfKe")
                .set(Select.field("departamento"), Departamento.TI)
                .set(Select.field("papeis"), Set.of(Papel.ADMIN))
                .create();

        modulos = Set.of(
                Instancio.of(Modulo.class)
                        .set(Select.field("id"), 10L)
                        .set(Select.field("tipo"), TipoModulo.PORTAL_COLABORADOR)
                        .set(Select.field("descricao"), "Acesso geral ao portal")
                        .set(Select.field("ativo"), true)
                        .set(Select.field("departamentosPermitidos"), Set.of(Departamento.TI))
                        .set(Select.field("modulosIncompativeis"), Set.of(Modulo.class))
                        .create()
        );
    }

    @Test
    void deveCriarSolicitacaoComSucesso() {

        CriarSolicitacaoRequest request = new CriarSolicitacaoRequest(
                Set.of(10L),
                "Justificativa válida para teste",
                false
        );

        Set<Long> modulosIdsSet = new HashSet<>(request.modulosIds());

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(moduloService.buscarModulosPorIds(eq(modulosIdsSet)))
                .thenReturn(modulos);

        ResultadoSolicitacaoResponse resp = service.criar(request);

        assertNotNull(resp);
        assertTrue(resp.protocolo().startsWith("SOL-"));

        verify(limiteModulosValidator).validar(eq(modulos));
        verify(justificativaValidator).validar(eq(request.justificativa()));
        verify(moduloAtivoValidator).validar(eq(modulos));
        verify(departamentoValidator).validar(eq(usuario), eq(modulos));
        verify(incompatibilidadeValidator).validar(eq(modulos));
        verify(solicitacaoRepository).save(any(SolicitacaoAcesso.class));
    }

    @Test
    void deveFalharJustificativaInvalida() {

        CriarSolicitacaoRequest request = new CriarSolicitacaoRequest(
                Set.of(10L),
                "teste",
                false
        );

        Set<Long> ids = new HashSet<>(request.modulosIds());

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(moduloService.buscarModulosPorIds(eq(ids)))
                .thenReturn(modulos);

        doThrow(new BusinessException("Justificativa inválida"))
                .when(justificativaValidator)
                .validar(eq("teste"));

        assertThrows(BusinessException.class, () -> service.criar(request));

        verify(justificativaValidator).validar(eq("teste"));
        verify(solicitacaoRepository, never()).save(any());
    }

    @Test
    void deveFalharLimiteDeModulos() {

        CriarSolicitacaoRequest request = new CriarSolicitacaoRequest(
                Set.of(10L),
                "Justificativa válida para teste",
                false
        );

        Set<Long> ids = new HashSet<>(request.modulosIds());

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(moduloService.buscarModulosPorIds(eq(ids)))
                .thenReturn(modulos);

        doThrow(new BusinessException("Limite"))
                .when(limiteModulosValidator)
                .validar(eq(modulos));

        assertThrows(BusinessException.class, () -> service.criar(request));
    }

    @Test
    void deveFalharModuloInativo() {

        CriarSolicitacaoRequest request = new CriarSolicitacaoRequest(
                Set.of(10L),
                "Justificativa válida para teste",
                false
        );

        Set<Long> ids = new HashSet<>(request.modulosIds());

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(moduloService.buscarModulosPorIds(eq(ids)))
                .thenReturn(modulos);

        doThrow(new BusinessException("Inativo"))
                .when(moduloAtivoValidator)
                .validar(eq(modulos));

        assertThrows(BusinessException.class, () -> service.criar(request));
    }

    @Test
    void deveFalharIncompatibilidade() {

        CriarSolicitacaoRequest request = new CriarSolicitacaoRequest(
                Set.of(10L),
                "Justificativa válida para teste",
                false
        );

        Set<Long> ids = new HashSet<>(request.modulosIds());

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(moduloService.buscarModulosPorIds(eq(ids)))
                .thenReturn(modulos);

        doThrow(new BusinessException("Incompatível"))
                .when(incompatibilidadeValidator)
                .validar(eq(modulos));

        assertThrows(BusinessException.class, () -> service.criar(request));
    }

    @Test
    void deveRenovarSolicitacao() {

        SolicitacaoAcesso anterior = montarSolicitacaoAprovada(
                LocalDateTime.now().plusDays(10)
        );

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(anterior));

        SolicitarRenovacaoRequest request = new SolicitarRenovacaoRequest(1L);

        ResultadoSolicitacaoResponse resp = service.renovar(request);

        assertNotNull(resp);
        assertTrue(resp.protocolo().startsWith("SOL-"));

        verify(solicitacaoRepository).save(any(SolicitacaoAcesso.class));
    }

    @Test
    void renovarFalhaNaoEncontrada() {

        when(solicitacaoRepository.findById(eq(99L))).thenReturn(Optional.empty());

        SolicitarRenovacaoRequest req = new SolicitarRenovacaoRequest(99L);

        assertThrows(NotFoundException.class, () -> service.renovar(req));
    }

    @Test
    void renovarFalhaOutroUsuario() {

        Usuario outro = Instancio.of(Usuario.class)
                .set(Select.field("id"), 99L)
                .create();

        SolicitacaoAcesso anterior = montarSolicitacaoAprovada(LocalDateTime.now().plusDays(10));
        anterior.setUsuario(outro);

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(anterior));

        SolicitarRenovacaoRequest req = new SolicitarRenovacaoRequest(1L);

        assertThrows(BusinessException.class, () -> service.renovar(req));
    }

    @Test
    void renovarFalhaStatus() {

        SolicitacaoAcesso anterior = montarSolicitacaoAprovada(LocalDateTime.now().plusDays(10));
        anterior.setStatus(StatusSolicitacao.CANCELADA);

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(anterior));

        assertThrows(BusinessException.class,
                () -> service.renovar(new SolicitarRenovacaoRequest(1L)));
    }

    @Test
    void renovarFalhaDias() {

        SolicitacaoAcesso anterior = montarSolicitacaoAprovada(
                LocalDateTime.now().plusDays(40)
        );

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(anterior));

        assertThrows(BusinessException.class,
                () -> service.renovar(new SolicitarRenovacaoRequest(1L)));
    }

    @Test
    void deveCancelarSolicitacao() {

        SolicitacaoAcesso s = montarSolicitacaoAprovada(LocalDateTime.now().plusDays(20));

        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(s));
        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);

        CancelarSolicitacaoRequest req =
                new CancelarSolicitacaoRequest(1L, "Motivo válido");

        service.cancelar(req);

        assertEquals(StatusSolicitacao.CANCELADA, s.getStatus());
        assertEquals("Motivo válido", s.getMotivoNegacao());

        verify(solicitacaoRepository).save(eq(s));
    }

    @Test
    void cancelarFalhaNaoEncontrado() {

        when(solicitacaoRepository.findById(eq(99L))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.cancelar(new CancelarSolicitacaoRequest(99L, "m")));
    }

    @Test
    void cancelarFalhaOutroUsuario() {

        Usuario outro = Instancio.of(Usuario.class)
                .set(Select.field("id"), 99L)
                .create();

        SolicitacaoAcesso s = montarSolicitacaoAprovada(LocalDateTime.now().plusDays(20));
        s.setUsuario(outro);

        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(s));
        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);

        assertThrows(BusinessException.class,
                () -> service.cancelar(new CancelarSolicitacaoRequest(1L, "M")));
    }

    @Test
    void deveListarSolicitacoes() {

        Pageable pageable = PageRequest.of(0, 10);

        SolicitacaoAcesso s = montarSolicitacaoAprovada(LocalDateTime.now().plusDays(10));

        Page<SolicitacaoAcesso> page =
                new PageImpl<>(List.of(s), pageable, 1);

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(solicitacaoRepository.findByUsuarioId(eq(1L), eq(pageable)))
                .thenReturn(page);

        Page<ListagemSolicitacaoResponse> resp =
                service.listarSolicitacoesDoUsuario(pageable);

        assertEquals(1, resp.getTotalElements());
        verify(solicitacaoRepository).findByUsuarioId(eq(1L), eq(pageable));
    }

    @Test
    void deveBuscarPorId() {

        SolicitacaoAcesso s = montarSolicitacaoAprovada(LocalDateTime.now().plusDays(10));

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(s));

        SolicitacaoResponse resp = service.buscarPorId(1L);

        assertNotNull(resp);
        assertEquals(s.getId(), resp.id());
        verify(solicitacaoRepository).findById(eq(1L));
    }

    @Test
    void buscarPorIdNaoEncontrado() {

        when(solicitacaoRepository.findById(eq(99L))).thenReturn(Optional.empty());
        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);

        assertThrows(NotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void buscarPorIdOutroUsuario() {

        Usuario outro = Instancio.of(Usuario.class)
                .set(Select.field("id"), 99L).create();

        SolicitacaoAcesso s = montarSolicitacaoAprovada(LocalDateTime.now().plusDays(10));
        s.setUsuario(outro);

        when(usuarioService.getUsuarioAutenticado()).thenReturn(usuario);
        when(solicitacaoRepository.findById(eq(1L))).thenReturn(Optional.of(s));

        assertThrows(BusinessException.class, () -> service.buscarPorId(1L));
    }

    private SolicitacaoAcesso montarSolicitacaoAprovada(LocalDateTime expira) {

        return SolicitacaoAcesso.builder()
                .id(1L)
                .usuario(usuario)
                .modulosSolicitados(modulos)
                .justificativa("Teste")
                .urgente(false)
                .status(StatusSolicitacao.APROVADA)
                .dataSolicitacao(LocalDateTime.now())
                .dataExpiracao(expira)
                .protocolo("SOL-TESTE-0001")
                .build();
    }
}
