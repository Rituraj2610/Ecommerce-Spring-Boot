package com.rituraj.ecommerce.repository;

import com.rituraj.ecommerce.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartItemRepo extends MongoRepository<CartItem, String> {
}
