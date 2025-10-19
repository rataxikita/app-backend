package com.voltlab.app_backend.favorite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    private Long id;
    private Long productId;
    private String productNombre;
}


