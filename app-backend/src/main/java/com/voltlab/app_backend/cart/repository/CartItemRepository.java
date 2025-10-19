package com.voltlab.app_backend.cart.repository;

import com.voltlab.app_backend.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart_Id(Long cartId);
    Optional<CartItem> findByCart_IdAndProduct_Id(Long cartId, Long productId);
    void deleteByCart_Id(Long cartId);
}


