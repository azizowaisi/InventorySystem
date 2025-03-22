package com.teckiz.InventorySystem.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "suppliers")
public class Supplier extends BasicEntity {

    @NotBlank(message = "Name is required")
    private String name;

    private String address;
}
