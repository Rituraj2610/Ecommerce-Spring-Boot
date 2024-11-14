package com.rituraj.ecommerce.repository;

import com.rituraj.ecommerce.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends MongoRepository<Order, String> {

    @Query("{'userId' : ?0}")
    List<Order> findByUserId(String userId);

    @Query("{'orderId' : ?0}")
    Optional<Order> findByOrderId(String orderId);

    @Query("{'sellerId' : ?0}")
    List<Order> findBySellerId(String sellerId);

    @Query("{'orderId' : ?0, 'sellerId' : ?1}")
    Optional<Order> findByOrderIdAndSellerId(String orderId, String sellerId);

    @Query("{'status' : ?0, 'sellerId' : ?1}")
    Optional<Order> findByStatusAndSellerId(String status, String sellerId);

    @Query(value = "{'orderId' : ?0}", delete = true)
    long deleteByOrderId(String orderId);
}
