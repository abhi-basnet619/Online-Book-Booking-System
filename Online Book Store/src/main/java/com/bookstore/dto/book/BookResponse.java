package com.bookstore.dto.book;

import com.bookstore.domain.enums.AvailabilityStatus;
import com.bookstore.domain.enums.BookCategory;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private BookCategory category;
    private BigDecimal price;
    private BigDecimal discountPercent;
    private BigDecimal finalPrice;
    private Integer stockQuantity;
    private AvailabilityStatus availabilityStatus;
    private String imageUrl;
}
