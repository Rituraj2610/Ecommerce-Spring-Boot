package com.rituraj.ecommerce.repository;

import com.rituraj.ecommerce.model.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<Buyer, String> {
}
