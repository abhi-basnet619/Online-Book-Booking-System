package com.bookstore.dto.book;

import com.bookstore.domain.enums.AvailabilityStatus;
import com.bookstore.domain.enums.BookCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookUpsertRequest {
    @NotBlank @Size(max = 220)
    private String title;

    @NotBlank @Size(max = 180)
    private String author;

    @NotBlank
    private String description;

    @NotNull
    private BookCategory category;

    @NotNull @DecimalMin("0.01")
    private BigDecimal price;

    @DecimalMin("0.0") @DecimalMax("20.0")
    private BigDecimal discountPercent;

    @NotNull @Min(0)
    private Integer stockQuantity;

    @NotNull
    private AvailabilityStatus availabilityStatus;

    @Size(max = 500)
    private String imageUrl;
}
