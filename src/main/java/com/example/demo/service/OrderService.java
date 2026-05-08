package com.example.demo.service;

import com.example.demo.CustomException.OrderNotFoundException;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    public Order createOrder(List<OrderItem> items) {
        String id = "ORD-" + System.currentTimeMillis();
        Order newOrder = new Order(id, items, "PLACED", System.currentTimeMillis());
        orderMap.put(id, newOrder);
        return newOrder;
    }

    public Order updateStatus(String orderId, String newStatus) {
        Order existing = orderMap.get(orderId);
        if (existing == null) throw new OrderNotFoundException("Order " + orderId + " not found");

        Order updated = new Order(existing.orderId(), existing.items(), newStatus.toUpperCase(), existing.createdAt());
        orderMap.put(orderId, updated);
        return updated;
    }

    public Order getOrderById(String orderId) {
        Order order = orderMap.get(orderId);
        if (order == null) throw new OrderNotFoundException("Order " + orderId + " not found");
        return order;
    }

    public Collection<Order> getAllOrders() {
        return orderMap.values();
    }
}