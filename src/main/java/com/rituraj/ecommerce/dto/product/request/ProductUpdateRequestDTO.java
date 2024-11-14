package com.rituraj.ecommerce.dto.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDTO {
    String name;
    String newName;
    double newPrice;
    String newCategory;
    String newDescription;
    int newStock;
    private List<String> imagesToDelete;
}
