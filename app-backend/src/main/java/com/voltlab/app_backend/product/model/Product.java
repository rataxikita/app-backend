package com.voltlab.app_backend.product.model;

import com.voltlab.app_backend.product.model.category.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Producto para e-commerce simple.
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_nombre", columnList = "nombre"),
        @Index(name = "idx_product_marca", columnList = "marca"),
        @Index(name = "idx_product_sku", columnList = "sku", unique = true)
})
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category categoria;

    @Column(length = 120)
    private String marca;

    @Column(length = 255)
    private String imageUrl;

    @Column(length = 80, unique = true)
    private String sku;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.activo == null) this.activo = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


