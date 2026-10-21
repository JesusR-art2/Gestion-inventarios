package com.Arquitecturaservicios.gestioninventarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioDto(
        @NotBlank(message = "Email es requerido")
        @Email(message = "Email no válido")
        String email,
        Boolean active,
        @NotBlank(message = "Password es requerido")
        @Size(min = 8, message = "Password debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "Rol es requerido")
        String rol) {}