package com.rituraj.ecommerce.dto.product.response;

import com.rituraj.ecommerce.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionListResponseDTO {
    private List<Product> productList;
}
