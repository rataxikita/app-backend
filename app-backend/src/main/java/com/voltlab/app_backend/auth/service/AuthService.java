package com.voltlab.app_backend.auth.service;

import com.voltlab.app_backend.auth.dto.*;
import com.voltlab.app_backend.model.User;
import com.voltlab.app_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        
        if (userRepository.existsByCorreo(request.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        User user = new User();
        user.setCorreo(request.getCorreo());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        
        User savedUser = userRepository.save(user);
        
        return new AuthResponse(
                null,  // No usamos token
                "Bearer",
                savedUser.getId(),
                savedUser.getCorreo(),
                savedUser.getNombre(),
                savedUser.getApellido(),
                savedUser.getRole().name()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        
        User user = userRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return new AuthResponse(
                null,  // No usamos token
                "Bearer",
                user.getId(),
                user.getCorreo(),
                user.getNombre(),
                user.getApellido(),
                user.getRole().name()
        );
    }
    
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (request.getNombre() != null && !request.getNombre().isEmpty()) {
            user.setNombre(request.getNombre());
        }
        
        if (request.getApellido() != null && !request.getApellido().isEmpty()) {
            user.setApellido(request.getApellido());
        }
        
        userRepository.save(user);
    }
}
