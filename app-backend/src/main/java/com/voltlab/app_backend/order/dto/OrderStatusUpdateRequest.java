package com.voltlab.app_backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    @NotBlank(message = "El estado es requerido")
    private String status; // PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO
}


