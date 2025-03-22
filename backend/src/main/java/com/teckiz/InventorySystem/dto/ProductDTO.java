package com.teckiz.InventorySystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO extends BasicDTO{
    private Long productId;
    private Long categoryId;
    private Long supplierId;

    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private String description;
    private String imageUrl;
    private LocalDateTime expiryDate;

}
