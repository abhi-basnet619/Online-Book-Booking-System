package com.bookstore.controller.api;

import com.bookstore.domain.enums.BookCategory;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookApiController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponse> list(@RequestParam(value = "q", required = false) String q,
                                  @RequestParam(value = "category", required = false) BookCategory category,
                                  @RequestParam(value = "deals", required = false) Boolean deals) {
        if (Boolean.TRUE.equals(deals)) return bookService.getDeals();
        if (category != null) return bookService.getByCategory(category);
        if (q != null && !q.isBlank()) return bookService.search(q);
        return bookService.getAll();
    }

    @GetMapping("/{id}")
    public BookResponse detail(@PathVariable Long id) { return bookService.getById(id); }
}
