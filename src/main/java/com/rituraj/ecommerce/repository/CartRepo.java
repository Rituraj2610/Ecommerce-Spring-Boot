package com.rituraj.ecommerce.repository;

import com.rituraj.ecommerce.model.Cart;
import com.rituraj.ecommerce.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CartRepo extends MongoRepository<Cart, String> {

    @Query("{'cartItems' : ?0 }")
    CartItem findByTag(String productId);

    @Query("{'_id' : ?0}")
    Cart findByCartId(String cartId);
}
