package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {


	//Develop an Inventory Management System with Deduct/Add Inventory functionality.
	//Requirements:
	//Develop a RESTful API to handle inventory management operations.
	//Implement endpoints for deducting and adding inventory quantities based on specified operations.
	//You may choose to store information in a database or utilize in-memory data structures for simplicity.
	//Utilize concurrent data structures or mechanisms to support concurrent inventory transactions.
	//Implement thread-safe operations to prevent race conditions and ensure data consistency during concurrent inventory updates.


	//
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
