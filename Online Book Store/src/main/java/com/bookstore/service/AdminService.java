package com.bookstore.service;

import com.bookstore.domain.enums.OrderStatus;
import com.bookstore.dto.admin.SalesSummaryResponse;
import com.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrderRepository orderRepository;

    public SalesSummaryResponse salesSummary() {
        var all = orderRepository.findAll();
        long total = all.size();
        long confirmed = all.stream().filter(o -> o.getStatus() == OrderStatus.CONFIRMED
                || o.getStatus() == OrderStatus.SHIPPED
                || o.getStatus() == OrderStatus.DELIVERED).count();
        BigDecimal revenue = all.stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED
                        || o.getStatus() == OrderStatus.SHIPPED
                        || o.getStatus() == OrderStatus.DELIVERED)
                .map(o -> o.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        return SalesSummaryResponse.builder()
                .totalOrders(total)
                .confirmedOrders(confirmed)
                .totalRevenue(revenue)
                .build();
    }
}
