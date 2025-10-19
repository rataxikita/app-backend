package com.voltlab.app_backend.cart.controller;

import com.voltlab.app_backend.cart.dto.AddToCartRequest;
import com.voltlab.app_backend.cart.dto.CartResponse;
import com.voltlab.app_backend.cart.dto.UpdateCartItemRequest;
import com.voltlab.app_backend.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Obtiene el carrito del usuario con totales e items.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    /**
     * Agrega un producto al carrito (o suma cantidad si ya existe).
     */
    @PostMapping("/{userId}")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        CartResponse response = cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza la cantidad de un producto en el carrito.
     */
    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.updateItem(userId, productId, request));
    }

    /**
     * Elimina un producto del carrito.
     */
    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    /**
     * Vacía el carrito del usuario.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<CartResponse> clearCart(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    // Manejo básico de errores
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        }
        errors.put("message", "Error de validación");
        errors.put("errors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeExceptions(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}


