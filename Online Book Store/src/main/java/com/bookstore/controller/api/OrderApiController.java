package com.bookstore.controller.api;

import com.bookstore.dto.order.OrderResponse;
import com.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponse place(Authentication auth) {
        return orderService.placeOrder(auth.getName());
    }

    @GetMapping
    public List<OrderResponse> myOrders(Authentication auth) {
        return orderService.myOrders(auth.getName());
    }

    @GetMapping("/{id}")
    public OrderResponse myOrderDetail(Authentication auth, @PathVariable Long id) {
        return orderService.myOrderDetail(auth.getName(), id);
    }
}
