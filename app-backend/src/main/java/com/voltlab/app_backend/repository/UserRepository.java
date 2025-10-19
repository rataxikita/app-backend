package com.voltlab.app_backend.repository;

import com.voltlab.app_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByCorreo(String correo);
    
    Boolean existsByCorreo(String correo);
}