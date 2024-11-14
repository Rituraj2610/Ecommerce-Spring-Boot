package com.rituraj.ecommerce.repository;

import com.rituraj.ecommerce.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepo extends MongoRepository<Review, String> {

    @Query(value = "{id = ?0, userId = ?1}", delete = true)
    long deleteByIdAndUserId(String reviewId, String id);

    @Query(value = "{userId = ? 0, productId = ?1}")
    Review findByUserIdAndProductId(String id, String productId);

    @Query(value = "{productId = ?0}")
    List<Review> findAllByProductId(String productId);

    @Query("{'_id' : ?0}")
    Review findByReviewId(String reviewId);

    @Query(value = "{'_id' : ?0}", delete = true)
    long deleteByReviewId(String reviewId);
}
