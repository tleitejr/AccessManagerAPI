package com.supera.accessmanager.repository;

import com.supera.accessmanager.domain.solicitacao.SolicitacaoAcesso;
import com.supera.accessmanager.domain.solicitacao.StatusSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoAcessoRepository extends JpaRepository<SolicitacaoAcesso, Long> {
    Page<SolicitacaoAcesso> findByUsuarioId(Long usuarioId, Pageable pageable);
}
