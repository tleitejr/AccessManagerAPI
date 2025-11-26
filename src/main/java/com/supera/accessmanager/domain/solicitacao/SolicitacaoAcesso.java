package com.supera.accessmanager.domain.solicitacao;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "solicitacao_acesso")
public class SolicitacaoAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "protocolo", nullable = false)
    private String protocolo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "justificativa")
    private String justificativa;

    @Column(name = "urgente", nullable = false)
    private boolean urgente;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private StatusSolicitacao status;

    @Column(name = "motivo_negacao")
    private String motivoNegacao;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @ManyToMany
    @JoinTable(
            name = "solicitacao_acesso_modulos",
            joinColumns = @JoinColumn(name = "solicitacao_id"),
            inverseJoinColumns = @JoinColumn(name = "modulo_id")
    )
    private Set<Modulo> modulosSolicitados;

    @ManyToOne
    @JoinColumn(name = "solicitacao_anterior_id")
    private SolicitacaoAcesso solicitacaoAnterior;
}
