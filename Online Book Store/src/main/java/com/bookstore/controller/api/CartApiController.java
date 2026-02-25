package com.bookstore.controller.api;

import com.bookstore.dto.cart.CartResponse;
import com.bookstore.dto.cart.CartUpdateRequest;
import com.bookstore.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

    @GetMapping
    public CartResponse get(Authentication auth) {
        return cartService.getCartForUser(auth.getName());
    }

    @PostMapping("/add/{bookId}")
    public CartResponse add(Authentication auth, @PathVariable Long bookId, @RequestParam(defaultValue = "1") int qty) {
        return cartService.addToCart(auth.getName(), bookId, qty);
    }

    @PutMapping("/update/{bookId}")
    public CartResponse update(Authentication auth, @PathVariable Long bookId, @Valid @RequestBody CartUpdateRequest req) {
        return cartService.updateQty(auth.getName(), bookId, req.getQuantity());
    }

    @DeleteMapping("/remove/{bookId}")
    public CartResponse remove(Authentication auth, @PathVariable Long bookId) {
        return cartService.removeItem(auth.getName(), bookId);
    }
}
