package com.teckiz.InventorySystem.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BasicDTO {
    private Long id;
    private String key;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
