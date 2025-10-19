package com.voltlab.app_backend.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<Item> items;
    private BigDecimal total;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private Long productId;
        private String productNombre;
        private Integer cantidad;
        private BigDecimal precio; // precio unitario guardado en el item
        private BigDecimal subtotal;
    }
}


