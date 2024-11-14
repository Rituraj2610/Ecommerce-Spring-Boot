package com.rituraj.ecommerce.dto.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private String name;
    private double price;
    private String description;
    private String category;
    private int stock;
}
