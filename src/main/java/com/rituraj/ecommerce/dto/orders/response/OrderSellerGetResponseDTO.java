package com.rituraj.ecommerce.dto.orders.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSellerGetResponseDTO {
    private String orderId;
    private int quantity;
    private double price;
    private double totalPrice;
    private String status;
    private LocalDateTime orderDateTime;
}
