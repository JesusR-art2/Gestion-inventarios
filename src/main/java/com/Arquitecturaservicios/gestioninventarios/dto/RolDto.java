package com.Arquitecturaservicios.gestioninventarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RolDto {
    private Long id;
    
    @NotBlank(message = "Nombre es requerido")
    @Size(max = 50, message = "Nombre no puede exceder 50 caracteres")
    private String name;
    
    @NotBlank(message = "Descripción es requerida")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
