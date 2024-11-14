package com.rituraj.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document("cart_item")

public class CartItem {
    private String productId;
    private String name;
    private int quantity;
    private double price;
    String image;
    private boolean selectedForPayment;
}
