package com.rituraj.ecommerce.model;

import com.rituraj.ecommerce.service.cart.CartItemService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("cart")
public class Cart {
    @Id
    private String id;

    @Field("cartItems")
    private List<CartItem> cartItem;

    @Field("totalAmount")
    private double totalAmount;

}
