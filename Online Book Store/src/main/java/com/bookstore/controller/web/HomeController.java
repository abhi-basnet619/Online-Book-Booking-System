package com.bookstore.controller.web;

import com.bookstore.domain.enums.BookCategory;
import com.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping("/")
    public String home(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "category", required = false) BookCategory category,
                       Model model) {

        if (category != null) {
            model.addAttribute("books", bookService.getByCategory(category));
        } else if (q != null && !q.isBlank()) {
            model.addAttribute("books", bookService.search(q));
        } else {
            model.addAttribute("books", bookService.getAll());
        }
        model.addAttribute("categories", BookCategory.values());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("deals", bookService.getDeals());
        return "index";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getById(id));
        return "book-detail";
    }

    @GetMapping("/books/deals")
    public String deals(Model model) {
        model.addAttribute("books", bookService.getDeals());
        model.addAttribute("categories", BookCategory.values());
        model.addAttribute("q", "");
        model.addAttribute("selectedCategory", null);
        model.addAttribute("deals", bookService.getDeals());
        return "index";
    }
}
