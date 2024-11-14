package com.rituraj.ecommerce.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionResponseDTO {
    private String name;
    private double price;
    private String description;
    private String category;
}
