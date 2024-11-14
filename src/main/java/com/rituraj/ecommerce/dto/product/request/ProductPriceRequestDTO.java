package com.rituraj.ecommerce.dto.product.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceRequestDTO {
    private double minPrice;
    private double maxPrice;
}
