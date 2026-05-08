package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for InventoryController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TestEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        // Clear inventory before each test
        mockMvc.perform(delete("/inventory/clearAll"));
    }

    // ADD INVENTORY TESTS
    @Test
    void testAddInventorySuccess() throws Exception {
        String requestBody = "{\"productID\": \"PROD001\", \"quantity\": 100}";

        mockMvc.perform(post("/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Inventory added successfully"));
    }

    @Test
    void testAddInventoryZeroQuantity() throws Exception {
        String requestBody = "{\"productID\": \"PROD001\", \"quantity\": 0}";

        mockMvc.perform(post("/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    // DEDUCT INVENTORY TESTS
    @Test
    void testDeductInventorySuccess() throws Exception {
        // First add inventory
        String addRequest = "{\"productID\": \"PROD001\", \"quantity\": 100}";
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest));

        // Then deduct
        String deductRequest = "{\"productID\": \"PROD001\", \"quantity\": 30}";
        mockMvc.perform(post("/inventory/deduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deductRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Stock Deducted successfully !!!"));
    }

    @Test
    void testDeductInventoryProductNotFound() throws Exception {
        String requestBody = "{\"productID\": \"NONEXISTENT\", \"quantity\": 10}";

        mockMvc.perform(post("/inventory/deduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeductInventoryInsufficientStock() throws Exception {
        // Add inventory
        String addRequest = "{\"productID\": \"PROD002\", \"quantity\": 50}";
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest));

        // Try to deduct more than available
        String deductRequest = "{\"productID\": \"PROD002\", \"quantity\": 100}";
        mockMvc.perform(post("/inventory/deduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(deductRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // GET INVENTORY TESTS
    @Test
    void testGetAllInventory() throws Exception {
        // Add some inventory first
        String request1 = "{\"productID\": \"PROD001\", \"quantity\": 50}";
        String request2 = "{\"productID\": \"PROD002\", \"quantity\": 100}";

        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request1));
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request2));

        mockMvc.perform(get("/inventory/getInventory"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PROD001").exists())
                .andExpect(jsonPath("$.PROD002").exists());
    }

    // GET STOCK BY PRODUCT TESTS
    @Test
    void testGetStockByProductSuccess() throws Exception {
        String addRequest = "{\"productID\": \"PROD003\", \"quantity\": 75}";
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest));

        mockMvc.perform(get("/inventory/getStock/PROD003"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("75"));
    }

    @Test
    void testGetStockByProductNotFound() throws Exception {
        mockMvc.perform(get("/inventory/getStock/NONEXISTENT"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // UPDATE INVENTORY TESTS
    @Test
    void testUpdateInventorySuccess() throws Exception {
        String addRequest = "{\"productID\": \"PROD004\", \"quantity\": 50}";
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest));

        String updateRequest = "{\"productID\": \"PROD004\", \"quantity\": 200}";
        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Inventory updated successfully"));
    }

    @Test
    void testUpdateInventoryProductNotFound() throws Exception {
        String updateRequest = "{\"productID\": \"NONEXISTENT\", \"quantity\": 100}";
        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // DELETE PRODUCT TESTS
    @Test
    void testDeleteProductSuccess() throws Exception {
        String addRequest = "{\"productID\": \"PROD005\", \"quantity\": 50}";
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addRequest));

        mockMvc.perform(delete("/inventory/delete/PROD005"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Product removed from inventory"));
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        mockMvc.perform(delete("/inventory/delete/NONEXISTENT"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // LOW STOCK TESTS
    @Test
    void testGetLowStockProducts() throws Exception {
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"PROD006\", \"quantity\": 10}"));
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"PROD007\", \"quantity\": 100}"));

        mockMvc.perform(get("/inventory/lowStock/50"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PROD006").exists());
    }

    // BULK ADD TESTS
    @Test
    void testBulkAddInventory() throws Exception {
        String requestBody = "[" +
                "{\"productID\": \"PROD008\", \"quantity\": 50}," +
                "{\"productID\": \"PROD009\", \"quantity\": 100}," +
                "{\"productID\": \"PROD010\", \"quantity\": 75}" +
                "]";

        mockMvc.perform(post("/inventory/addBulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Bulk inventory added successfully"));
    }

    // SEARCH TESTS
    @Test
    void testSearchProducts() throws Exception {
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"LAPTOP_001\", \"quantity\": 10}"));
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"LAPTOP_002\", \"quantity\": 20}"));
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"PHONE_001\", \"quantity\": 15}"));

        mockMvc.perform(get("/inventory/search?keyword=LAPTOP"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.LAPTOP_001").exists())
                .andExpect(jsonPath("$.LAPTOP_002").exists());
    }

    // CLEAR INVENTORY TESTS
    @Test
    void testClearAllInventory() throws Exception {
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"PROD011\", \"quantity\": 50}"));

        mockMvc.perform(delete("/inventory/clearAll"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("All inventory cleared"));
    }

    // COUNT TESTS
    @Test
    void testGetTotalProductCount() throws Exception {
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"PROD012\", \"quantity\": 50}"));
        mockMvc.perform(post("/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productID\": \"PROD013\", \"quantity\": 30}"));

        mockMvc.perform(get("/inventory/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }
}