package com.voltlab.app_backend.cart.service;

import com.voltlab.app_backend.cart.dto.AddToCartRequest;
import com.voltlab.app_backend.cart.dto.CartResponse;
import com.voltlab.app_backend.cart.dto.UpdateCartItemRequest;
import com.voltlab.app_backend.cart.model.Cart;
import com.voltlab.app_backend.cart.model.CartItem;
import com.voltlab.app_backend.cart.repository.CartItemRepository;
import com.voltlab.app_backend.cart.repository.CartRepository;
import com.voltlab.app_backend.product.model.Product;
import com.voltlab.app_backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CartItem item = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setProduct(product);
                    ci.setQuantity(0);
                    ci.setPrice(product.getPrecio());
                    return ci;
                });

        int newQty = item.getQuantity() + request.getQuantity();
        if (newQty <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        if (product.getStock() == null || product.getStock() < newQty) {
            throw new RuntimeException("Stock insuficiente");
        }
        item.setQuantity(newQty);
        cartItemRepository.save(item);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long userId, Long productId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item de carrito no encontrado"));
        if (request.getQuantity() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        Product product = item.getProduct();
        if (product.getStock() == null || product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente");
        }
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Item de carrito no encontrado"));
        cartItemRepository.delete(item);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCart_Id(cart.getId());
        return toCartResponse(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(userId);
            return cartRepository.save(c);
        });
    }

    private CartResponse toCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        List<CartResponse.Item> responses = items.stream().map(i -> new CartResponse.Item(
                i.getProduct().getId(),
                i.getProduct().getNombre(),
                i.getQuantity(),
                i.getPrice(),
                i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
        )).toList();

        BigDecimal total = responses.stream()
                .map(CartResponse.Item::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cart.getId(), cart.getUserId(), responses, total);
    }
}


