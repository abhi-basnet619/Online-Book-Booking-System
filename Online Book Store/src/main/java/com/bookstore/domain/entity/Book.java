package com.bookstore.domain.entity;

import com.bookstore.domain.enums.AvailabilityStatus;
import com.bookstore.domain.enums.BookCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_books_title", columnList = "title"),
        @Index(name = "idx_books_author", columnList = "author"),
        @Index(name = "idx_books_category", columnList = "category")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Book extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(nullable = false, length = 180)
    private String author;

    @Lob
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private BookCategory category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercent; // 15-20 (optional)

    @Column(nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AvailabilityStatus availabilityStatus;

    @Column(length = 500)
    private String imageUrl;

    @Transient
    public BigDecimal getFinalPrice() {
        if (discountPercent == null) return price;
        BigDecimal discount = price.multiply(discountPercent).divide(BigDecimal.valueOf(100));
        return price.subtract(discount).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
