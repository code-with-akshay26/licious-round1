package com.example.demo.controller;

import com.example.demo.model.InventoryRequest;
import com.example.demo.service.InventoryService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody InventoryRequest request) {
        service.addInventory(request.productID(), request.quantity());
        return ResponseEntity.ok("Inventory added successfully");
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deduct(@RequestBody InventoryRequest request) {
        service.deductInventory(request.productID(), request.quantity());
        return ResponseEntity.ok("Stock Deducted successfully !!!");
    }

    @GetMapping("/getInventory")
    public ResponseEntity<ConcurrentHashMap> getInventory() {
        return ResponseEntity.ok(service.getAllInventory());
    }

    @GetMapping("/getStock/{productId}")
    public ResponseEntity<Integer> getStockByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(service.getStockByProduct(productId));
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateInventory(@RequestBody InventoryRequest request) {
        service.updateInventory(request.productID(), request.quantity());
        return ResponseEntity.ok("Inventory updated successfully");
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        service.deleteProduct(productId);
        return ResponseEntity.ok("Product removed from inventory");
    }

    @GetMapping("/lowStock/{threshold}")
    public ResponseEntity<Map<String, Integer>> getLowStockProducts(@PathVariable int threshold) {
        return ResponseEntity.ok(service.getLowStockProducts(threshold));
    }

    @PostMapping("/addBulk")
    public ResponseEntity<String> addBulk(@RequestBody List<Map<String, Object>> requests) {
        service.addBulkInventory(requests);
        return ResponseEntity.ok("Bulk inventory added successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Integer>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchProducts(keyword));
    }

    @DeleteMapping("/clearAll")
    public ResponseEntity<String> clearAllInventory() {
        service.clearAllInventory();
        return ResponseEntity.ok("All inventory cleared");
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getTotalProductCount() {
        return ResponseEntity.ok(service.getTotalProductCount());
    }
}