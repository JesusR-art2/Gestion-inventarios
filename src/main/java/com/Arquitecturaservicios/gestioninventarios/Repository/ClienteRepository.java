package com.Arquitecturaservicios.gestioninventarios.Repository;

import com.Arquitecturaservicios.gestioninventarios.Entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDocumento(String documento);
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByDocumento(String documento);
}
