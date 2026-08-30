package com.Arquitecturaservicios.gestioninventarios.Repository;

import com.Arquitecturaservicios.gestioninventarios.Entities.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    List<UsuarioRol> findByUsuarioId(Long usuarioId);
    Optional<UsuarioRol> findByUsuarioIdAndRolId(Long usuarioId, Long rolId);
}
