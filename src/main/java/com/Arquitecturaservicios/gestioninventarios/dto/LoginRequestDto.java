package com.Arquitecturaservicios.gestioninventarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequestDto {
    
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe ser válido")
    private String email;
    
    @NotBlank(message = "Password es requerido")
    private String password;
}
