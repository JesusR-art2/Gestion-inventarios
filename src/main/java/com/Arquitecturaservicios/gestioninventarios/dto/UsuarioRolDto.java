package com.Arquitecturaservicios.gestioninventarios.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class UsuarioRolDto {
    private Long id;
    
    @NotNull(message = "Usuario ID es requerido")
    private Long usuarioId;
    
    @NotNull(message = "Rol ID es requerido")
    private Long rolId;
    
    private LocalDateTime fechaAsignacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }
}
