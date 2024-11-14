package com.rituraj.ecommerce.dto.product.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeleteResponseDTO {
    String id;
    String productMsg;
    String sellerMsg;
}
