package com.Arquitecturaservicios.gestioninventarios.dto;

import java.time.LocalDateTime;
import java.util.List;

public record usuarioResponseDto (
        Long id,
        String email,
        Boolean active,
        List<String> rol,
        LocalDateTime lastLogin,
        LocalDateTime dateCreate,
        LocalDateTime dateUpdate
){
}
