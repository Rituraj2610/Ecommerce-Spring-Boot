package com.rituraj.ecommerce.service.user;
import com.mongodb.client.result.UpdateResult;
import com.rituraj.ecommerce.util.JwtUtil;
import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.dto.cart.CartItemAddRequestDTO;
import com.rituraj.ecommerce.dto.cart.CartItemUpdateRequestDTO;
import com.rituraj.ecommerce.dto.orders.response.OrderAddResponseDTO;
import com.rituraj.ecommerce.dto.orders.response.OrderGetResponseDTO;
import com.rituraj.ecommerce.dto.product.response.ProductDescriptionListResponseDTO;
import com.rituraj.ecommerce.dto.review.*;
import com.rituraj.ecommerce.exception.*;
import com.rituraj.ecommerce.model.*;
import com.rituraj.ecommerce.repository.BuyerRepo;
import com.rituraj.ecommerce.service.cart.CartService;
import com.rituraj.ecommerce.service.order.OrderService;
import com.rituraj.ecommerce.service.product.ProductService;
import com.rituraj.ecommerce.service.review.ReviewService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyerImplementation implements BuyerService {

    private BuyerRepo buyerRepo;
    private CartService cartService;
    private OrderService orderService;
    private ProductService productService;
    private ReviewService reviewService;
    private MongoTemplate mongoTemplate;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    public BuyerImplementation(BuyerRepo buyerRepo, CartService cartService, OrderService orderService, ProductService productService, ReviewService reviewService, MongoTemplate mongoTemplate, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.buyerRepo = buyerRepo;
        this.cartService = cartService;
        this.orderService = orderService;
        this.productService = productService;
        this.reviewService = reviewService;
        this.mongoTemplate = mongoTemplate;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(Buyer user){
        if(buyerRepo.save(user)!=null){
            cartService.addCartId(user.getId());
            return;
        }
        throw new EntityPushException("Failed to add buyer in db");
    }

    @Override
    public LoginResponseDTO login(Buyer user) {
        // Fetch the buyer from the repository based on email
        Buyer temp = buyerRepo.findByEmail(user.getEmail());

        // If no buyer is found, throw an exception
        if (temp == null) {
            throw new ResourceNotFoundException("Buyer with provided credentials not found");
        }

        // Validate the password
        if (passwordEncoder.matches(user.getPassword(), temp.getPassword())) {
            System.out.println("Buyer Logged in");

            // Generate JWT token after successful login
            String token = jwtUtil.generateToken(temp.getEmail(), temp.getRoles().name(), temp.getId());

            // Return the login response DTO with token
            return new LoginResponseDTO(token, "Token generated");
        }

        // If the password doesn't match, throw an invalid input exception
        throw new InvalidInputException("Wrong Credentials");
    }


    // get all products
     public List<Product> getHomePageProducts() {    return productService.getAllProducts();}

    // METHODS UNIQUE TO BUYER

    /*
     * Method: addProductToCart
     * Role: Adds the cartItem to cart
     */
    public String addProductToCart(CartItemAddRequestDTO cartItemAddRequestDTO){
        return cartService.addProduct(cartItemAddRequestDTO);
    }

    /*
     * Method: updateCartItem
     * Role: Updates the item quantity in cart if user wants to change.. takes cartid, prodid, new qun, price from FE
     */
    public String updateCartItem(CartItemUpdateRequestDTO cartItemUpdateRequestDTO) {
        return cartService.updateCartItem(cartItemUpdateRequestDTO);
    }

    public String deleteCartItem(String cartItemId) {
        return cartService.deleteCartItem(cartItemId);
    }

    public OrderAddResponseDTO placeOrder() {
        return orderService.placeOrder();
    }

    public OrderGetResponseDTO getAllOrders(){
        return orderService.getAllOrders();
    }

    public ProductDescriptionListResponseDTO getProductsByNameAndCategory(String name,  String category) {

        if(name.equals("null") && category.equals("null")){
            throw new InvalidInputException("No input given");
        }else if(name.equals("null")){
            return getProductsByCategory(category);
        } else if (category.equals("null")) {
            return getProductsByName(name);
        }else {
            return productService.getByNameAndCategory(name, category);
        }
    }

    /*
     * Method: getProductsByName
     * Role: User can get al products by name
     */
    //implement regex maybe
    private ProductDescriptionListResponseDTO  getProductsByName(String name){
        return productService.getByName(name);
    }

    /*
     * Method: getProductsByCategory
     * Role: User can sort products according to category
     */
    private ProductDescriptionListResponseDTO getProductsByCategory(String category){
        return productService.getByCategory(category);
    }

    public String addProductReviews(ReviewRequestDTO reviewRequestDTO){
        return reviewService.addProductReview(reviewRequestDTO);
    }

    public void addReviewId(String userId, String id) {
        Query query = new Query(Criteria.where("_id").is(userId));

        Update update = new Update();
        update.push("reviewId", id);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Buyer.class);
        if(updateResult.getModifiedCount() > 0){
            System.out.println("Review id added to buyer list");
            return;
        }
        throw new EntityPushException("Failed to add reveiw Id to buyer.");
    }

//    public ReviewListResponseDTO getProductReviews(String id, String productId) {
//        return reviewService.getProductReviews(id, productId);
//    }

    public String deleteProductReviews(String reviewId, String productId){
        return reviewService.deleteProductReview(reviewId, productId);
    }

    // delete id from user and product
    public void deleteReviewId(String id, String reviewId) {
        Query query = new Query(Criteria.where("_id").is(id));

        Update update = new Update();
        update.pull("reviewId", reviewId);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Buyer.class);
        if(updateResult.getModifiedCount() > 0){
            System.out.println("Review id deleted from buyer list");
            return;
        }
        throw new EntityDeletionException("Failed to delete review id from buyer.");
    }

    public String updateProductReviews(ReviewUpdateRequestDTO reviewUpdateRequestDTO) {
        return reviewService.updateProductReview(reviewUpdateRequestDTO);
    }

    public void addOrderId(String userId, String id) {
        Query query = new Query(Criteria.where("_id").is(userId));

        Update update = new Update();
        update.push("orderId", id);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Buyer.class);
        if(updateResult.getModifiedCount() > 0){
            System.out.println("Order id added to buyer list");
            return;
        }

        throw new EntityUpdationException("Failed to update review id from buyer.");
    }


//    // USER ADMIN CALL THESE METHODS
//    public List<Buyer> getAllBuyers() {
//
//    }

//    public List<String> getAllUserReviews(String buyerId){
//
//    }


//    public String deleteUser(String userId) {
//
//    }


    public Buyer findByEmail(String email) {
        return buyerRepo.findByEmail(email);
    }

    public Cart getCart(){
        return cartService.getCart();
    }

    public Order getOrder(String orderId) {
        return orderService.getOrder(orderId);
    }

    public List<Review> getProductReviews(String productId) {
        return productService.getProductReviews(productId);
    }

    /*
     * Method: getProduct
     * Role: Fetches the Product based on productId
     */
    public Product getProduct(String productId) {
        return productService.getById(productId);
    }

    /*
     * Method: getProductByPriceRange
     * Role: Displays products by Price Range
     */
    public ProductDescriptionListResponseDTO getProductByPriceRange(double minPrice, double maxPrice) {
        return productService.getByPriceRange(minPrice, maxPrice);
    }

    /*
     * Method: deleteOrder
     * Role: DELETE: Deletes the order
     */
    public String deleteOrder(String orderId) {
        return orderService.deleteOrder(orderId);
    }
}
