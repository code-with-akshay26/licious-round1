package com.example.demo.model;

import java.util.List;

public record Order(
        String orderId,
        List<OrderItem> items,
        String status,
        long createdAt
) {}