package com.rituraj.ecommerce.service.admin;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.internal.bulk.DeleteRequest;
import com.rituraj.ecommerce.exception.EntityDeletionException;
import com.rituraj.ecommerce.exception.ResourceNotFoundException;
import com.rituraj.ecommerce.model.Admin;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.model.Roles;
import com.rituraj.ecommerce.model.Seller;
import com.rituraj.ecommerce.repository.AdminRepo;
import com.rituraj.ecommerce.repository.CartRepo;
import com.rituraj.ecommerce.repository.ProductRepo;
import com.rituraj.ecommerce.repository.SellerRepo;
import com.rituraj.ecommerce.service.product.ProductService;
import com.rituraj.ecommerce.service.user.SellerImplementation;
import com.rituraj.ecommerce.util.IdGenerator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductAdminService implements AdminService {

    private AdminRepo adminRepo;
    private SellerRepo sellerRepo;
    private ProductRepo productRepo;
    private IdGenerator idGenerator;
    private SellerImplementation sellerImplementation;
    private PasswordEncoder passwordEncoder;
    private MongoTemplate mongoTemplate;

    public ProductAdminService(AdminRepo adminRepo, SellerRepo sellerRepo, ProductRepo productRepo, IdGenerator idGenerator, SellerImplementation sellerImplementation, PasswordEncoder passwordEncoder, MongoTemplate mongoTemplate) {
        this.adminRepo = adminRepo;
        this.sellerRepo = sellerRepo;
        this.productRepo = productRepo;
        this.idGenerator = idGenerator;
        this.sellerImplementation = sellerImplementation;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    /*
     * Method: login
     * Role: Super Admin Login
     */
    @Override
    public Admin login(String email, String password, Roles role) throws NoSuchAlgorithmException {
        Admin admin = adminRepo.findByEmailAndRoles(email, role);
        if(admin.getId() != null && admin!=null){
            if (passwordEncoder.matches(password, admin.getPassword())) {
                // Password matches
                System.out.println("Admin found");
                return admin;
            } else {
                // Password doesn't match
                throw new ResourceNotFoundException("Admin with the specified credentials not found.");
            }
        }
        throw new ResourceNotFoundException("Admin with the specified credentials not found.");
    }


    public List<Seller> getAllSellers() {
        List<Seller> sellerList = sellerRepo.findAll();
        if (sellerList.isEmpty()) {
            throw new ResourceNotFoundException("Failed to fetch sellers.");
        }
        return sellerList;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = productRepo.findAll();
        if(!productList.isEmpty()){
            return productList;
        }
        throw new ResourceNotFoundException("Failed to fetch Products.");
    }

    public List<Product> getAllSellerProducts(String id) {
        Optional<Seller> seller = sellerImplementation.findBySellerId(id);
        if (seller.isEmpty()) {
            throw new ResourceNotFoundException("Seller not found with ID: " + id);
        }

        List<Product> products = productRepo.findBySellerId(id);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for seller with ID: " + id);
        }
        return products;
    }

    public Seller getSellerFromProductId(String productId){
        return sellerImplementation.getSellerFromProductId(productId);
    }

    public String deleteSeller(String id) {
        Optional<Seller> optionalSeller = sellerRepo.findById(id);
        if(optionalSeller.isEmpty()){
            throw new ResourceNotFoundException("Failed to find seller with specified category.");
        }

        // if seller exists
        List<String> productId = optionalSeller.get().getProductId();
        Query query = new Query(Criteria.where("_id").in(id));
        DeleteResult deleteResult = mongoTemplate.remove(query, Seller.class);

        if(deleteResult.getDeletedCount() > 0){
            Query query1 = new Query(Criteria.where("_id").in(productId));
            DeleteResult deleteResult1 = mongoTemplate.remove(query1, Product.class);
            if(deleteResult1.getDeletedCount() > 0){
                System.out.println("Deleted products from db");
            }else{
                throw new EntityDeletionException("Failed to delete Products from db");
            }
            return ("Seller Deleted successfully");

        }
        throw new EntityDeletionException("Failed to delete Seller.");
    }

    public String deleteProduct(String productId) {
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isEmpty()){
            throw new ResourceNotFoundException("Failed to find product with specified category.");
        }
        String sellerId = optionalProduct.get().getSellerId();
        Query query = new Query(Criteria.where("_id").is(productId));
        DeleteResult deleteResult = mongoTemplate.remove(query, Product.class);

        if(deleteResult.getDeletedCount() > 0){
            deleteSellerProduct(sellerId, productId);
            return ("Product Deleted successfully");
        }
        throw new EntityDeletionException("Failed to delete products of seller.");
    }

    private void deleteSellerProduct(String sellerId, String productId){
        Optional<Seller> optionalSeller = sellerRepo.findById(sellerId);
        if(optionalSeller.isEmpty()){
            throw new ResourceNotFoundException("Failed to find seller with specified credentials");
        }
        Query query = new Query(Criteria.where("_id").is(sellerId));
        Update update = new Update();
        update.pull("productId", productId);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Seller.class);
        if(updateResult.getModifiedCount() > 0 ){
            System.out.println("Product Deleted from seller successfully");
            return;
        }
        throw new EntityDeletionException("Failed to delete product from seller");
    }

}
