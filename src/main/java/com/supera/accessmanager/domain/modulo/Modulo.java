package com.supera.accessmanager.domain.modulo;

import com.supera.accessmanager.domain.usuario.Departamento;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "modulo")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 100, nullable = false)
    private TipoModulo tipo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Departamento> departamentosPermitidos;

    @ManyToMany
    @JoinTable(
            name = "modulo_incompatibilidades",
            joinColumns = @JoinColumn(name = "modulo_id"),
            inverseJoinColumns = @JoinColumn(name = "incompat_id")
    )
    private Set<Modulo> modulosIncompativeis;
}
