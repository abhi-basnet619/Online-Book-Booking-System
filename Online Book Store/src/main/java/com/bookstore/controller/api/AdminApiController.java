package com.bookstore.controller.api;

import com.bookstore.domain.enums.OrderStatus;
import com.bookstore.dto.admin.SalesSummaryResponse;
import com.bookstore.dto.book.BookResponse;
import com.bookstore.dto.book.BookUpsertRequest;
import com.bookstore.dto.order.OrderResponse;
import com.bookstore.dto.order.UpdateOrderStatusRequest;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.AdminService;
import com.bookstore.service.BookService;
import com.bookstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final BookService bookService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final AdminService adminService;

    @GetMapping("/books")
    public List<BookResponse> books() { return bookService.getAll(); }

    @PostMapping("/books")
    public BookResponse create(@Valid @RequestBody BookUpsertRequest req) { return bookService.create(req); }

    @PutMapping("/books/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookUpsertRequest req) { return bookService.update(id, req); }

    @DeleteMapping("/books/{id}")
    public void delete(@PathVariable Long id) { bookService.delete(id); }

    @GetMapping("/users")
    public Object users() { return userRepository.findAll(); }

    @GetMapping("/orders")
    public List<OrderResponse> orders() { return orderService.allOrders(); }

    @PutMapping("/orders/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest req) {
        return orderService.updateStatusAdmin(id, req.getStatus());
    }

    @GetMapping("/sales-summary")
    public SalesSummaryResponse salesSummary() { return adminService.salesSummary(); }
}
