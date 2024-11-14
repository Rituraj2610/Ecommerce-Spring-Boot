package com.rituraj.ecommerce.repository;

import com.rituraj.ecommerce.dto.user.UserSearchRequestDTO;
import com.rituraj.ecommerce.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends MongoRepository<Product, String> {

    @Query("{'name' : ?0, 'sellerId' : ?1}")
    Product findByNameAndSellerId(String name, String sellerId);

    @Query("{category : ?0, sellerId : ?1}")
    List<Product> findByCategoryAndSellerId(String category, String sellerId);

    @Query("{'price':{$gte: ?0, $lte:?1}, 'sellerId': ?2}")
    List<Product> findByPriceBetweenAndSellerId(double minPrice, double minPrice1, String sellerId);

    @Query(value = "{name : ?0, sellerId : ?1}", delete = true)
    long deleteByNameAndSellerId(String name, String sellerId);

    @Query(value = "{ 'sellerId' : ?0 }", delete = true)
    long deleteBySellerId(String sellerId);

    @Query(value = "{'_id' = ?0}", delete = true)
    long deleteByProductId(String productId);

    @Query("{'name':?0}")
    List<Product> findByName(String name);

    @Query("{'category':?0}")
    List<Product> findByCategory(String category);

    @Query("{'name':?0, 'category':?1}")
    List<Product> findByNameAndCategory(String name, String Category);

    @Query("{'sellerId' : ?0}")
    List<Product> findBySellerId(String sellerId);

    @Query("{'_id' : ?0}")
    Optional<Product> findByProductId(String productId);

    @Query("{'price':{$gte : ?0, $lte : ?1}}")
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

}
