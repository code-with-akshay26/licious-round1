package com.example.demo;

import com.example.demo.service.InventoryService;
import com.example.demo.CustomException.ProductNotFoundException;
import com.example.demo.CustomException.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DemoEntityApplicationTests {

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService.clearAllInventory();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testAddInventory() {
        inventoryService.addInventory("PROD001", 100);
        assertEquals(100, inventoryService.getStockByProduct("PROD001"));
    }

    @Test
    void testAddInventoryMultiple() {
        inventoryService.addInventory("PROD001", 50);
        inventoryService.addInventory("PROD001", 30);
        assertEquals(80, inventoryService.getStockByProduct("PROD001"));
    }

    @Test
    void testDeductInventory() {
        inventoryService.addInventory("PROD001", 100);
        inventoryService.deductInventory("PROD001", 30);
        assertEquals(70, inventoryService.getStockByProduct("PROD001"));
    }

    @Test
    void testDeductInventoryInsufficientStock() {
        inventoryService.addInventory("PROD001", 50);
        assertThrows(InsufficientStockException.class, () -> {
            inventoryService.deductInventory("PROD001", 100);
        });
    }

    @Test
    void testDeductInventoryProductNotFound() {
        assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.deductInventory("NONEXISTENT", 10);
        });
    }

    @Test
    void testGetStockByProduct() {
        inventoryService.addInventory("PROD002", 75);
        int stock = inventoryService.getStockByProduct("PROD002");
        assertEquals(75, stock);
    }

    @Test
    void testGetStockByProductNotFound() {
        assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.getStockByProduct("NONEXISTENT");
        });
    }

    @Test
    void testUpdateInventory() {
        inventoryService.addInventory("PROD003", 50);
        inventoryService.updateInventory("PROD003", 200);
        assertEquals(200, inventoryService.getStockByProduct("PROD003"));
    }

    @Test
    void testUpdateInventoryProductNotFound() {
        assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.updateInventory("NONEXISTENT", 100);
        });
    }

    @Test
    void testDeleteProduct() {
        inventoryService.addInventory("PROD004", 50);
        inventoryService.deleteProduct("PROD004");
        assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.getStockByProduct("PROD004");
        });
    }

    @Test
    void testDeleteProductNotFound() {
        assertThrows(ProductNotFoundException.class, () -> {
            inventoryService.deleteProduct("NONEXISTENT");
        });
    }

    @Test
    void testGetLowStockProducts() {
        inventoryService.addInventory("PROD005", 10);
        inventoryService.addInventory("PROD006", 100);
        inventoryService.addInventory("PROD007", 5);

        Map<String, Integer> lowStockProducts = inventoryService.getLowStockProducts(50);
        assertEquals(2, lowStockProducts.size());
        assertTrue(lowStockProducts.containsKey("PROD005"));
        assertTrue(lowStockProducts.containsKey("PROD007"));
    }

    @Test
    void testSearchProducts() {
        inventoryService.addInventory("LAPTOP_001", 10);
        inventoryService.addInventory("LAPTOP_002", 20);
        inventoryService.addInventory("PHONE_001", 15);

        Map<String, Integer> results = inventoryService.searchProducts("LAPTOP");
        assertEquals(2, results.size());
    }

    @Test
    void testClearAllInventory() {
        inventoryService.addInventory("PROD008", 50);
        inventoryService.addInventory("PROD009", 30);
        inventoryService.clearAllInventory();
        assertEquals(0, inventoryService.getTotalProductCount());
    }

    @Test
    void testAddInventoryWithZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.addInventory("PROD010", 0);
        });
    }

    @Test
    void testAddInventoryWithNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> {
            inventoryService.addInventory("PROD011", -10);
        });
    }

    @Test
    void testGetTotalProductCount() {
        inventoryService.addInventory("PROD012", 50);
        inventoryService.addInventory("PROD013", 30);
        inventoryService.addInventory("PROD014", 20);
        assertEquals(3, inventoryService.getTotalProductCount());
    }
}