package com.bookstore.service;

import com.bookstore.domain.entity.Cart;
import com.bookstore.domain.entity.Order;
import com.bookstore.domain.entity.OrderItem;
import com.bookstore.domain.entity.User;
import com.bookstore.domain.enums.AvailabilityStatus;
import com.bookstore.domain.enums.OrderStatus;
import com.bookstore.dto.order.OrderItemResponse;
import com.bookstore.dto.order.OrderResponse;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Value("${app.order.delivery-charge}")
    private BigDecimal deliveryCharge;

    @Transactional
    public OrderResponse placeOrder(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) throw new BadRequestException("Cart is empty");

        // Validate availability + stock at the time of placing order (soft check)
        cart.getItems().forEach(ci -> {
            var b = ci.getBook();
            if (b.getAvailabilityStatus() == AvailabilityStatus.OUT_OF_STOCK) {
                throw new BadRequestException("Item out of stock: " + b.getTitle());
            }
            if (b.getStockQuantity() < ci.getQuantity()) {
                throw new BadRequestException("Insufficient stock for: " + b.getTitle());
            }
        });

        var cartSnapshot = cartService.map(cart);
        BigDecimal subtotal = cartSnapshot.getSubtotal();

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .itemsSubtotal(subtotal)
                .deliveryCharge(deliveryCharge)
                .totalAmount(subtotal.add(deliveryCharge).setScale(2, java.math.RoundingMode.HALF_UP))
                .build();

        // Copy items
        for (var ci : cart.getItems()) {
            var b = ci.getBook();
            BigDecimal unit = b.getFinalPrice();
            BigDecimal line = unit.multiply(BigDecimal.valueOf(ci.getQuantity())).setScale(2, java.math.RoundingMode.HALF_UP);
            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .book(b)
                    .quantity(ci.getQuantity())
                    .unitPrice(unit)
                    .lineTotal(line)
                    .build());
        }

        Order saved = orderRepository.save(order);

        // clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(saved, true);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(o -> toResponse(o, false)).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse myOrderDetail(String email, Long orderId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) throw new BadRequestException("Access denied");
        return toResponse(order, true);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> allOrders() {
        return orderRepository.findAll().stream().map(o -> toResponse(o, false)).toList();
    }

    @Transactional
    public OrderResponse updateStatusAdmin(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        if (status == OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.CONFIRMED) {
            // On confirm: hard check and deduct stock
            for (var item : order.getItems()) {
                var book = item.getBook();
                if (book.getAvailabilityStatus() == AvailabilityStatus.OUT_OF_STOCK) {
                    throw new BadRequestException("Cannot confirm: item out of stock: " + book.getTitle());
                }
                if (book.getStockQuantity() < item.getQuantity()) {
                    throw new BadRequestException("Cannot confirm: insufficient stock for: " + book.getTitle());
                }
            }
            for (var item : order.getItems()) {
                var book = item.getBook();
                book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
                if (book.getStockQuantity() == 0) {
                    book.setAvailabilityStatus(AvailabilityStatus.OUT_OF_STOCK);
                }
            }
        }

        order.setStatus(status);
        Order saved = orderRepository.save(order);
        return toResponse(saved, true);
    }

    public OrderResponse toResponse(Order order, boolean includeItems) {
        var builder = OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .itemsSubtotal(order.getItemsSubtotal())
                .deliveryCharge(order.getDeliveryCharge())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt());

        if (includeItems) {
            builder.items(order.getItems().stream().map(oi -> OrderItemResponse.builder()
                    .bookId(oi.getBook().getId())
                    .title(oi.getBook().getTitle())
                    .quantity(oi.getQuantity())
                    .unitPrice(oi.getUnitPrice())
                    .lineTotal(oi.getLineTotal())
                    .build()).toList());
        }
        return builder.build();
    }
}
