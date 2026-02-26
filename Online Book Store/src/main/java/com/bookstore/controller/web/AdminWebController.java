package com.bookstore.controller.web;

import com.bookstore.domain.enums.OrderStatus;
import com.bookstore.dto.book.BookUpsertRequest;
import com.bookstore.dto.order.UpdateOrderStatusRequest;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.AdminService;
import com.bookstore.service.BookService;
import com.bookstore.service.ImageStorageService;
import com.bookstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final BookService bookService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final AdminService adminService;
    private final ImageStorageService imageStorageService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("summary", adminService.salesSummary());
        model.addAttribute("orders", orderService.allOrders());
        return "admin/dashboard";
    }

    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books", bookService.getAll());
        return "admin/books";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new BookUpsertRequest());
        model.addAttribute("categories", com.bookstore.domain.enums.BookCategory.values());
        model.addAttribute("statuses", com.bookstore.domain.enums.AvailabilityStatus.values());
        return "admin/book-form";
    }

    @PostMapping("/books")
    public String create(@Valid @ModelAttribute("book") BookUpsertRequest req,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        String imageUrl = imageStorageService.storeBookImage(imageFile);
        if (imageUrl != null) {
            req.setImageUrl(imageUrl);
        }
        bookService.create(req);
        return "redirect:/admin/books";
    }

    @GetMapping("/books/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        var b = bookService.getById(id);
        BookUpsertRequest form = new BookUpsertRequest();
        form.setTitle(b.getTitle());
        form.setAuthor(b.getAuthor());
        form.setDescription(b.getDescription());
        form.setCategory(b.getCategory());
        form.setPrice(b.getPrice());
        form.setDiscountPercent(b.getDiscountPercent());
        form.setStockQuantity(b.getStockQuantity());
        form.setAvailabilityStatus(b.getAvailabilityStatus());
        form.setImageUrl(b.getImageUrl());

        model.addAttribute("book", form);
        model.addAttribute("bookId", id);
        model.addAttribute("categories", com.bookstore.domain.enums.BookCategory.values());
        model.addAttribute("statuses", com.bookstore.domain.enums.AvailabilityStatus.values());
        return "admin/book-form";
    }

    @PostMapping("/books/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("book") BookUpsertRequest req,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        String imageUrl = imageStorageService.storeBookImage(imageFile);
        if (imageUrl != null) {
            req.setImageUrl(imageUrl);
        }
        bookService.update(id, req);
        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/admin/books";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.allOrders());
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("updateReq", new UpdateOrderStatusRequest());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("status") OrderStatus status) {
        orderService.updateStatusAdmin(id, status);
        return "redirect:/admin/orders";
    }
}
