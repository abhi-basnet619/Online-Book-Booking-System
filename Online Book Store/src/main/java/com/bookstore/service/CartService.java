package com.bookstore.service;

import com.bookstore.domain.entity.Cart;
import com.bookstore.domain.entity.CartItem;
import com.bookstore.domain.entity.User;
import com.bookstore.dto.cart.CartItemResponse;
import com.bookstore.dto.cart.CartResponse;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Value("${app.order.delivery-charge}")
    private BigDecimal deliveryCharge;

    @Transactional(readOnly = true)
    public CartResponse getCartForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));
        return map(cart);
    }

    @Transactional
    public CartResponse addToCart(String email, Long bookId, int qty) {
        if (qty <= 0) throw new BadRequestException("Quantity must be >= 1");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));
        var book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));

        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId).orElse(null);
        if (item == null) {
            item = CartItem.builder().cart(cart).book(book).quantity(qty).build();
        } else {
            item.setQuantity(item.getQuantity() + qty);
        }
        cartItemRepository.save(item);
        return map(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartResponse updateQty(String email, Long bookId, int qty) {
        if (qty <= 0) throw new BadRequestException("Quantity must be >= 1");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));
        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId).orElseThrow(() -> new NotFoundException("Item not found"));
        item.setQuantity(qty);
        cartItemRepository.save(item);
        return map(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartResponse removeItem(String email, Long bookId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new NotFoundException("Cart not found"));
        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId).orElseThrow(() -> new NotFoundException("Item not found"));
        cartItemRepository.delete(item);
        return map(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public CartResponse map(Cart cart) {
        var items = cart.getItems().stream().map(ci -> {
            var b = ci.getBook();
            var unit = b.getFinalPrice();
            var line = unit.multiply(BigDecimal.valueOf(ci.getQuantity())).setScale(2, java.math.RoundingMode.HALF_UP);
            return CartItemResponse.builder()
                    .bookId(b.getId())
                    .title(b.getTitle())
                    .author(b.getAuthor())
                    .quantity(ci.getQuantity())
                    .unitPrice(unit)
                    .lineTotal(line)
                    .imageUrl(b.getImageUrl())
                    .build();
        }).toList();

        BigDecimal subtotal = items.stream().map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(deliveryCharge).setScale(2, java.math.RoundingMode.HALF_UP);
        return CartResponse.builder()
                .items(items)
                .subtotal(subtotal)
                .deliveryCharge(deliveryCharge)
                .total(total)
                .build();
    }
}
