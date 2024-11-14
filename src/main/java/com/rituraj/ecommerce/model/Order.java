package com.rituraj.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("order")
public class Order {
    @Id
    private String id;
    private String orderId; // Newly added orderId
    private String userId;
    private CartItem cartItem;
    private double totalPrice;
    private int quantity;
    private String status;
    private LocalDateTime orderDateTime; // Newly added field
    private String sellerId;


}
