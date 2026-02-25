package com.bookstore.service;

import com.bookstore.domain.entity.Book;
import com.bookstore.domain.enums.BookCategory;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.dto.book.BookUpsertRequest;
import com.bookstore.exception.NotFoundException;
import com.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream().map(this::toResponse).toList();
    }

    public BookResponse getById(Long id) {
        return toResponse(bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found")));
    }

    public List<BookResponse> getDeals() {
        return bookRepository.findByDiscountPercentIsNotNull().stream().map(this::toResponse).toList();
    }

    public List<BookResponse> getByCategory(BookCategory category) {
        return bookRepository.findByCategory(category).stream().map(this::toResponse).toList();
    }

    public List<BookResponse> search(String q) {
        if (q == null || q.isBlank()) return getAll();
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(q, q).stream().map(this::toResponse).toList();
    }

    @Transactional
    public BookResponse create(BookUpsertRequest req) {
        Book book = Book.builder()
                .title(req.getTitle())
                .author(req.getAuthor())
                .description(req.getDescription())
                .category(req.getCategory())
                .price(req.getPrice())
                .discountPercent(req.getDiscountPercent())
                .stockQuantity(req.getStockQuantity())
                .availabilityStatus(req.getAvailabilityStatus())
                .imageUrl(req.getImageUrl())
                .build();
        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookUpsertRequest req) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));
        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setDescription(req.getDescription());
        book.setCategory(req.getCategory());
        book.setPrice(req.getPrice());
        book.setDiscountPercent(req.getDiscountPercent());
        book.setStockQuantity(req.getStockQuantity());
        book.setAvailabilityStatus(req.getAvailabilityStatus());
        book.setImageUrl(req.getImageUrl());
        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) throw new NotFoundException("Book not found");
        bookRepository.deleteById(id);
    }

    public BookResponse toResponse(Book b) {
        return BookResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .author(b.getAuthor())
                .description(b.getDescription())
                .category(b.getCategory())
                .price(b.getPrice())
                .discountPercent(b.getDiscountPercent())
                .finalPrice(b.getFinalPrice())
                .stockQuantity(b.getStockQuantity())
                .availabilityStatus(b.getAvailabilityStatus())
                .imageUrl(b.getImageUrl())
                .build();
    }
}
