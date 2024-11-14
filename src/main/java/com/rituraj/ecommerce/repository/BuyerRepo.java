package com.rituraj.ecommerce.repository;

import com.rituraj.ecommerce.model.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerRepo extends MongoRepository<Buyer, String> {

    @Query("{ 'email' : ?0 }")
    Buyer findByEmail(String email);

    @Query("{'_id':?0}")
    int deleteByUserId(String userId);

    @Query("email = ?0")
    boolean existsByEmail(String email);
}
