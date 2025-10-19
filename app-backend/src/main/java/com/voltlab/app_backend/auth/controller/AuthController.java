package com.voltlab.app_backend.auth.controller;

import com.voltlab.app_backend.auth.dto.AuthResponse;
import com.voltlab.app_backend.auth.dto.LoginRequest;
import com.voltlab.app_backend.auth.dto.RegisterRequest;
import com.voltlab.app_backend.auth.dto.UpdateProfileRequest;
import com.voltlab.app_backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de autenticación básico sin JWT ni Spring Security avanzado.
 * Provee endpoints para registro, login y actualización de perfil.
 *
 * Notas de diseño (simplicidad):
 * - Se usan validaciones con @Valid sobre los DTOs.
 * - Errores de validación y de negocio (RuntimeException) se manejan con @ExceptionHandler.
 * - Respuestas JSON claras y simples.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registro de usuarios.
     * - Valida el payload con @Valid.
     * - Si el correo ya existe, el servicio lanza RuntimeException -> 400.
     * - Devuelve datos básicos del usuario registrado.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login de usuarios.
     * - Valida el payload con @Valid.
     * - Si las credenciales son inválidas, el servicio lanza RuntimeException -> 400.
     * - Devuelve datos básicos del usuario autenticado.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualización básica de perfil (nombre / apellido) por id de usuario.
     * - Requiere un userId válido en la ruta.
     * - Campos opcionales en el cuerpo (se actualiza solo lo enviado y no vacío).
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        authService.updateProfile(userId, request);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Perfil actualizado correctamente");
        body.put("userId", userId);
        return ResponseEntity.ok(body);
    }

    // =====================
    // Manejo básico de errores
    // =====================

    /**
     * Maneja errores de validación de @Valid, devolviendo 400 y un mapa de errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        }
        errors.put("message", "Error de validación");
        errors.put("errors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Maneja errores simples de negocio (RuntimeException) devolviendo 400 con el mensaje.
     * Para un manejo más fino, podrían usarse excepciones específicas con distintos estados.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeExceptions(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}


