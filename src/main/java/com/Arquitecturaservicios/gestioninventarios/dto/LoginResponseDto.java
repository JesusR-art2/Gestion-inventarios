package com.Arquitecturaservicios.gestioninventarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    
    private String token;
    private String email;
    private String nombre;
    private String documento;
    private String rol;
    private Long issuedAt;
    private Long expiration;
}
