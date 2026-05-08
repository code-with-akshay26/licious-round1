package com.example.demo.service;

import com.example.demo.CustomException.InsufficientStockException;
import com.example.demo.CustomException.ProductNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    // concurrency
    private final ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // add inventory
    public void addInventory(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        inventory.computeIfAbsent(productId, k -> new AtomicInteger(0)).addAndGet(quantity);
        System.out.println("Product added: " + productId + " Quantity: " + quantity);
    }

    // deduct inventory
    public void deductInventory(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        AtomicInteger stock = inventory.get(productId);
        if (stock == null) {
            throw new ProductNotFoundException("Product is not found :" + productId);
        }

        int currentStock = stock.get();
        if (currentStock < quantity) {
            throw new InsufficientStockException("Not Enough Stock. Available: " + currentStock);
        }

        System.out.println("Product removed " + productId + " Quantity: " + quantity);
        stock.compareAndSet(currentStock, currentStock - quantity);
    }

    // get all inventory
    public ConcurrentHashMap<String, AtomicInteger> getAllInventory() {
        return inventory;
    }

    // get stock by product ID
    public int getStockByProduct(String productId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) {
            throw new ProductNotFoundException("Product is not found: " + productId);
        }
        return stock.get();
    }

    // update inventory (set to exact quantity)
    public void updateInventory(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (!inventory.containsKey(productId)) {
            throw new ProductNotFoundException("Product is not found: " + productId);
        }
        inventory.put(productId, new AtomicInteger(quantity));
        System.out.println("Product updated: " + productId + " New Quantity: " + quantity);
    }

    // delete product
    public void deleteProduct(String productId) {
        if (!inventory.containsKey(productId)) {
            throw new ProductNotFoundException("Product is not found: " + productId);
        }
        inventory.remove(productId);
        System.out.println("Product deleted: " + productId);
    }

    // get low stock products
    public Map<String, Integer> getLowStockProducts(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        return inventory.entrySet().stream()
                .filter(entry -> entry.getValue().get() < threshold)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));
    }

    // bulk add inventory
    public void addBulkInventory(List<Map<String, Object>> requests) {
        for (Map<String, Object> request : requests) {
            String productId = (String) request.get("productID");
            int quantity = (int) request.get("quantity");
            addInventory(productId, quantity);
        }
    }

    // search products
    public Map<String, Integer> searchProducts(String keyword) {
        return inventory.entrySet().stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));
    }

    // clear all inventory
    public void clearAllInventory() {
        inventory.clear();
        System.out.println("All inventory cleared");
    }

    // get inventory count
    public int getTotalProductCount() {
        return inventory.size();
    }
}