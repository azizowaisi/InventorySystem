package com.teckiz.InventorySystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InventoryManagementSystemApplication {

	public static void main(String[] args) {
		EnvLoader.load(".env");
		SpringApplication.run(InventoryManagementSystemApplication.class, args);
	}

}
