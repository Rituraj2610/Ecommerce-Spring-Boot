package com.rituraj.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("product")
    public class Product {
        @Id
        private String id;
        private String sellerId;
        private String name;
        private double price;
        private String description;
        private String category;
        private int stock;
        private List<String> reviewId;
        private List<String> imageUrls; // New field for storing image URLs
    }


// BEFORE IMAGE WAS ADDED
//public class Product {
//    @Id
//    private String id;
//    private String sellerId;
//
//
//    private String name;
//
//    private double price;
//    private String description;
//    private String category;
//    private List<String> reviewId;
//}
