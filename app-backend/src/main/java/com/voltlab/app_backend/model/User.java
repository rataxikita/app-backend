package com.voltlab.app_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;
    
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) //para que se guarde como string en la base de datos
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;
    
    public enum Role {
        USER, ADMIN
    }

    /*para hacer auditorias de la tabla*/

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); //fecha de creacion

    private LocalDateTime updatedAt; //fecha de actualizacion

    @PreUpdate//Se ejecuta automáticamente antes de cada UPDATE
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
