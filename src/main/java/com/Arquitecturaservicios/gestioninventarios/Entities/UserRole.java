package com.Arquitecturaservicios.gestioninventarios.Entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("Administrador", "Acceso completo a todo el sistema"),
    AUX_BODEGA("Auxiliar de Bodega", "Gestión de inventario, entrada y salida de productos"),
    VENDEDOR("Vendedor", "Gestión de ventas y consulta de productos"),
    GERENTE("Gerente", "Reportes, estadísticas y supervisión general");

    private final String displayName;
    private final String description;
}
