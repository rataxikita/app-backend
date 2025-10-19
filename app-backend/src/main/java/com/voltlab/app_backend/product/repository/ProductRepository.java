package com.voltlab.app_backend.product.repository;

import com.voltlab.app_backend.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNombre(String nombre);
    boolean existsBySku(String sku);
    List<Product> findByActivoTrue();
    List<Product> findByNombreContainingIgnoreCaseOrMarcaContainingIgnoreCase(String nombre, String marca);
    List<Product> findByCategoria_Id(Long categoryId);
}


