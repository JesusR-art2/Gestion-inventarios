package com.Arquitecturaservicios.gestioninventarios.Repository;

import com.Arquitecturaservicios.gestioninventarios.Entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByRuc(String ruc);
    List<Proveedor> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByRuc(String ruc);
    List<Proveedor> findByContactoContainingIgnoreCase(String contacto);
}