package com.example.demo.model;

public record InventoryRequest(
    String productID,
    int quantity
) {

}
