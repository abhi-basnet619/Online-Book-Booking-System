package com.bookstore.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long bookId;
    private String title;
    private String author;
    private Integer quantity;
    private BigDecimal unitPrice; // final discounted price
    private BigDecimal lineTotal;
    private String imageUrl;
}
