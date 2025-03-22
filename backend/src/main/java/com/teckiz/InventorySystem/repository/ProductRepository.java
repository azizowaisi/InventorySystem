package com.teckiz.InventorySystem.repository;

import com.teckiz.InventorySystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
