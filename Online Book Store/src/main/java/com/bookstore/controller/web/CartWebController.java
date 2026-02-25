package com.bookstore.controller.web;

import com.bookstore.dto.cart.CartUpdateRequest;
import com.bookstore.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartWebController {

    private final CartService cartService;

    @GetMapping
    public String view(Authentication auth, Model model) {
        model.addAttribute("cart", cartService.getCartForUser(auth.getName()));
        return "cart";
    }

    @PostMapping("/add/{bookId}")
    public String add(Authentication auth, @PathVariable Long bookId, @RequestParam(defaultValue = "1") int qty) {
        cartService.addToCart(auth.getName(), bookId, qty);
        return "redirect:/cart";
    }

    @PostMapping("/update/{bookId}")
    public String update(Authentication auth, @PathVariable Long bookId, @Valid CartUpdateRequest req) {
        cartService.updateQty(auth.getName(), bookId, req.getQuantity());
        return "redirect:/cart";
    }

    @PostMapping("/remove/{bookId}")
    public String remove(Authentication auth, @PathVariable Long bookId) {
        cartService.removeItem(auth.getName(), bookId);
        return "redirect:/cart";
    }
}
