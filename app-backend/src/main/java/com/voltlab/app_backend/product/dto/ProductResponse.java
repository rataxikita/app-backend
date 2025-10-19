package com.voltlab.app_backend.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String marca;
    private String imageUrl;
    private String sku;
    private Boolean activo;
    private Long categoryId;
    private String categoryNombre;
}


