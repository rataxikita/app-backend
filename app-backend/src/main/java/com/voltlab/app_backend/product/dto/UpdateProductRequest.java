package com.voltlab.app_backend.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no debe superar 1000 caracteres")
    private String descripcion;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Size(max = 120, message = "La marca no debe superar 120 caracteres")
    private String marca;

    @Size(max = 255, message = "La URL de imagen no debe superar 255 caracteres")
    private String imageUrl;

    @Size(max = 80, message = "El SKU no debe superar 80 caracteres")
    private String sku;

    private Boolean activo;

    private Long categoryId;
    @Size(max = 120, message = "El nombre de categoría no debe superar 120 caracteres")
    private String categoryNombre;
}


