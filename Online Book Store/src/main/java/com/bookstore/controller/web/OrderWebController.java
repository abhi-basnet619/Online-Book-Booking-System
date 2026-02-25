package com.bookstore.controller.web;

import com.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderWebController {

    private final OrderService orderService;

    @GetMapping
    public String myOrders(Authentication auth, Model model) {
        model.addAttribute("orders", orderService.myOrders(auth.getName()));
        return "orders";
    }

    @PostMapping("/place")
    public String place(Authentication auth) {
        orderService.placeOrder(auth.getName());
        return "redirect:/orders";
    }

    @GetMapping("/{id}")
    public String details(Authentication auth, @PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.myOrderDetail(auth.getName(), id));
        return "order-detail";
    }
}
