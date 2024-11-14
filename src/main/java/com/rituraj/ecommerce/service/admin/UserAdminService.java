package com.rituraj.ecommerce.service.admin;

import com.mongodb.client.result.DeleteResult;
import com.rituraj.ecommerce.exception.EntityDeletionException;
import com.rituraj.ecommerce.exception.ResourceNotFoundException;
import com.rituraj.ecommerce.model.*;
import com.rituraj.ecommerce.repository.*;
import com.rituraj.ecommerce.service.review.ReviewService;
import com.rituraj.ecommerce.util.EmailValidator;
import com.rituraj.ecommerce.util.IdGenerator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserAdminService implements AdminService {

    private final AdminRepo adminRepository;
    private final BuyerRepo buyerRepository;
    private final ReviewRepo reviewRepository;
    private final CartRepo cartRepository;
    private final IdGenerator idGenerator;
    private final EmailValidator emailValidator;
    private final MongoTemplate mongoTemplate;
    private final ReviewService reviewService;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepo orderRepo;

    public UserAdminService(AdminRepo adminRepository, BuyerRepo buyerRepository, ReviewRepo reviewRepository, CartRepo cartRepository, IdGenerator idGenerator, EmailValidator emailValidator, MongoTemplate mongoTemplate, ReviewService reviewService, PasswordEncoder passwordEncoder, OrderRepo orderRepo) {
        this.adminRepository = adminRepository;
        this.buyerRepository = buyerRepository;
        this.reviewRepository = reviewRepository;
        this.cartRepository = cartRepository;
        this.idGenerator = idGenerator;
        this.emailValidator = emailValidator;
        this.mongoTemplate = mongoTemplate;
        this.reviewService = reviewService;
        this.passwordEncoder = passwordEncoder;
        this.orderRepo = orderRepo;
    }

    @Override
    public Admin login(String email, String password, Roles role) throws NoSuchAlgorithmException {
        Admin admin = adminRepository.findByEmailAndRoles(email, role);
        if(admin.getId() != null && admin!=null){
            //aop
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


    /*
     * Method: getAllBuyer
     * Role: fetces all registered buyers in db
     */
    public List<Buyer> getAllBuyers() {
        List<Buyer> buyerList = buyerRepository.findAll();
        if(!buyerList.isEmpty()){
            return buyerList;
        }

        throw new ResourceNotFoundException("No Buyers found.");
    }

    /*
     * Method: getAllBuyerReviews
     * Role: fetces all reviews of buyer
     */
    public List<Review> getAllUserReviews(String buyerId) {

        Query query = new Query(Criteria.where("_id").is(buyerId));
        Buyer buyer = mongoTemplate.findOne(query, Buyer.class);

        List<String> userReviews = buyer.getReviewId();
        if(userReviews.isEmpty()){
            throw new ResourceNotFoundException("No Reviews found.");
        }

        List<Review> reviewList = reviewService.getReviews(userReviews);

        return reviewList;
    }

/*
 * Method: viewUserCart
 * Role: view user cart
 */
    public Cart viewUserCart(String cartId){
        Optional<Cart> optionalCart = Optional.ofNullable(cartRepository.findByCartId(cartId));
        if(optionalCart.isPresent()){
            return optionalCart.get();
        }
        throw new ResourceNotFoundException("Cart not found");
    }


    public String deleteUser(String userId) {
        Query query = new Query(Criteria.where("_id").is(userId));
        DeleteResult deleteResult = mongoTemplate.remove(query, Buyer.class);
        if(deleteResult.getDeletedCount()>0){
            return "Successfully deleted";
        }
        throw new EntityDeletionException("Failed to delete user with given credentials");
    }


    public List<Order> getUserOrder(String buyerId) {
        List<Order> orderList = orderRepo.findByUserId(buyerId);
        if(orderList.isEmpty()){
            throw new ResourceNotFoundException("No orders found!");
        }
        return orderList;
    }
}
