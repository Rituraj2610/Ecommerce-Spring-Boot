package com.rituraj.ecommerce.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private String productId;
    private int rating;
    private String comment;
}
