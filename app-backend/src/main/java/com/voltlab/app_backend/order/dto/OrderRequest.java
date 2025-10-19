package com.voltlab.app_backend.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    // Crear pedido desde carrito por userId
    @NotNull(message = "El userId es requerido")
    private Long userId;
}


