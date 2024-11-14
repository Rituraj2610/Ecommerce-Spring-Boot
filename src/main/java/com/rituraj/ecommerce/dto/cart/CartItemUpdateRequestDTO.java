package com.rituraj.ecommerce.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateRequestDTO {
    private boolean selectedForPayment;
    private String productId;
    private double price;
    private int quantity;

}
