package com.supera.accessmanager.repository;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.modulo.TipoModulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    Optional<Modulo> findByTipo(TipoModulo tipo);
}
