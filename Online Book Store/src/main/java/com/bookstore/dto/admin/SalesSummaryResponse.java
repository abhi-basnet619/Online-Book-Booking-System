package com.bookstore.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SalesSummaryResponse {
    private long totalOrders;
    private long confirmedOrders;
    private BigDecimal totalRevenue;
}
