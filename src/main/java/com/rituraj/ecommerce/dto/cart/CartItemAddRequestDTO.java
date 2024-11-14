package com.rituraj.ecommerce.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemAddRequestDTO {
    private String productId;
    private String name;
    private int quantity;
    private double price;
    String image;
}
