package com.teckiz.InventorySystem.repository;

import com.teckiz.InventorySystem.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
