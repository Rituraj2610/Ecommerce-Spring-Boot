package com.rituraj.ecommerce.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductGetResponseDTO {
        private String name;
        private double price;
        private String description;
        private String category;
        private int stock;
        private List<String> imageUrls;
}
