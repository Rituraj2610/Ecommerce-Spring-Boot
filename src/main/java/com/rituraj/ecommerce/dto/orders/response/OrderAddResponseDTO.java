package com.rituraj.ecommerce.dto.orders.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAddResponseDTO {
    private List<String> successfulOrders;
    private List<String> outOfStockItems;
    private List<String> failedOrders;
}
