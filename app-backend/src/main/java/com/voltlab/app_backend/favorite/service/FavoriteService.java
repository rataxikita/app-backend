package com.voltlab.app_backend.favorite.service;

import com.voltlab.app_backend.favorite.dto.FavoriteResponse;
import com.voltlab.app_backend.favorite.model.Favorite;
import com.voltlab.app_backend.favorite.repository.FavoriteRepository;
import com.voltlab.app_backend.product.model.Product;
import com.voltlab.app_backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    public List<FavoriteResponse> list(Long userId) {
        return favoriteRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public FavoriteResponse add(Long userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Favorite fav = favoriteRepository.findByUserIdAndProduct_Id(userId, productId)
                .orElseGet(() -> {
                    Favorite f = new Favorite();
                    f.setUserId(userId);
                    f.setProduct(product);
                    return f;
                });
        Favorite saved = favoriteRepository.save(fav);
        return toResponse(saved);
    }

    @Transactional
    public void remove(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProduct_Id(userId, productId);
    }

    private FavoriteResponse toResponse(Favorite f) {
        return new FavoriteResponse(
                f.getId(),
                f.getProduct().getId(),
                f.getProduct().getNombre()
        );
    }
}


