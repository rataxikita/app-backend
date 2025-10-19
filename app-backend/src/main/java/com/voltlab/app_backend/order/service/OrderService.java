package com.voltlab.app_backend.order.service;

import com.voltlab.app_backend.cart.model.Cart;
import com.voltlab.app_backend.cart.model.CartItem;
import com.voltlab.app_backend.cart.repository.CartItemRepository;
import com.voltlab.app_backend.cart.repository.CartRepository;
import com.voltlab.app_backend.order.dto.OrderHistoryResponse;
import com.voltlab.app_backend.order.dto.OrderRequest;
import com.voltlab.app_backend.order.dto.OrderResponse;
import com.voltlab.app_backend.order.dto.OrderStatusUpdateRequest;
import com.voltlab.app_backend.order.model.Order;
import com.voltlab.app_backend.order.model.OrderItem;
import com.voltlab.app_backend.order.repository.OrderRepository;
import com.voltlab.app_backend.product.model.Product;
import com.voltlab.app_backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public List<OrderResponse> listByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse createFromCart(OrderRequest request) {
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());
        if (items.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus("PENDIENTE");

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) {
            Product product = ci.getProduct();
            int qty = ci.getQuantity();
            if (product.getStock() == null || product.getStock() < qty) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getNombre());
            }
            product.setStock(product.getStock() - qty);
            productRepository.save(product);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(qty);
            oi.setPrice(product.getPrecio());
            order.getItems().add(oi);

            total = total.add(product.getPrecio().multiply(BigDecimal.valueOf(qty)));
        }
        order.setTotalAmount(total);

        // Guarda pedido y limpia carrito
        Order saved = orderRepository.save(order);
        cartItemRepository.deleteByCart_Id(cart.getId());
        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> items = order.getItems().stream().map(oi ->
                new OrderResponse.OrderItemResponse(
                        oi.getProduct().getId(),
                        oi.getProduct().getNombre(),
                        oi.getQuantity(),
                        oi.getPrice()
                )
        ).toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                items
        );
    }

    public List<OrderHistoryResponse> history(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(o ->
                new OrderHistoryResponse(
                        o.getId(),
                        o.getStatus(),
                        o.getTotalAmount(),
                        o.getCreatedAt(),
                        toResponse(o).getItems()
                )
        ).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        String newStatus = request.getStatus();
        if (!isValidStatus(newStatus)) {
            throw new RuntimeException("Estado inválido");
        }
        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    private boolean isValidStatus(String status) {
        return "PENDIENTE".equals(status) ||
                "CONFIRMADO".equals(status) ||
                "ENVIADO".equals(status) ||
                "ENTREGADO".equals(status) ||
                "CANCELADO".equals(status);
    }
}


