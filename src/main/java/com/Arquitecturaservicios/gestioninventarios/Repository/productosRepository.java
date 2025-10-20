package com.Arquitecturaservicios.gestioninventarios.Repository;

import com.Arquitecturaservicios.gestioninventarios.Entities.Productos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface productosRepository extends JpaRepository<Productos, Long> {

    Optional<Productos> findByCodigo(Integer codigo);
}
