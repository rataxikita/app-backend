package com.voltlab.app_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    private String token; //token de autenticacion
    private String type = "Bearer"; //tipo de token, siempre se va a enviar como Bearer
    private Long userId;
    private String correo;
    private String nombre;
    private String apellido;
    private String role;
}
