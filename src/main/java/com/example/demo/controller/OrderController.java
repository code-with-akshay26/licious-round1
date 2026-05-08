package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/warehouse/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @PostMapping("/create")
    public ResponseEntity<Order> create(@RequestBody List<OrderItem> items) {
        return ResponseEntity.ok(service.createOrder(items));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> update(@PathVariable String orderId, @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(orderId, status));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> get(@PathVariable String orderId) {
        return ResponseEntity.ok(service.getOrderById(orderId));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Collection<Order>> getAll() {
        return ResponseEntity.ok(service.getAllOrders());
    }
}