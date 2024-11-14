package com.rituraj.ecommerce.controller;

import com.rituraj.ecommerce.middleware.AuthRequired;
import com.rituraj.ecommerce.dto.cart.CartItemAddRequestDTO;
import com.rituraj.ecommerce.dto.cart.CartItemUpdateRequestDTO;
import com.rituraj.ecommerce.dto.orders.response.OrderAddResponseDTO;
import com.rituraj.ecommerce.dto.orders.response.OrderGetResponseDTO;
import com.rituraj.ecommerce.dto.product.response.ProductDescriptionListResponseDTO;
import com.rituraj.ecommerce.dto.review.*;
import com.rituraj.ecommerce.model.Cart;
import com.rituraj.ecommerce.model.Order;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.model.Review;
import com.rituraj.ecommerce.service.user.BuyerImplementation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buyer")
public class BuyerController {

    private BuyerImplementation buyerImplementation;

    public BuyerController(BuyerImplementation buyerImplementation) {
        this.buyerImplementation = buyerImplementation;
    }

    /*
     * Method: getHomePageProducts
     * Role: GET: Displays Products on home page
     */
    @GetMapping("/dashboard/products")public ResponseEntity<List<Product>> getHomePageProducts(){
        List<Product> productList = buyerImplementation.getHomePageProducts();
        return  ResponseEntity.ok(productList);
    }

    /*
     * Method: getBuyerCart
     * Role: GET: Displays items in the cart
     */
    @GetMapping("/cart")
    @AuthRequired
    public ResponseEntity<Cart> getBuyerCart(HttpServletRequest request){
        Cart cart = buyerImplementation.getCart();
        return ResponseEntity.ok(cart);
    }

    /*
     * Method: addProductToCart
     * Role: POST: Adds product to cart
     */
    @PostMapping("/cart")
    @AuthRequired
    public ResponseEntity<String> addProductToCart(@RequestBody CartItemAddRequestDTO cartItemAddRequestDTO){
        String msg = buyerImplementation.addProductToCart(cartItemAddRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: updateCartItem
     * Role: PUT: Updates product present in cart
     */
    @PutMapping("/cart")
    @AuthRequired
    public ResponseEntity<String> updateCartItem(@RequestBody CartItemUpdateRequestDTO cartItemUpdateRequestDTO){
        String msg = buyerImplementation.updateCartItem(cartItemUpdateRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: deleteCartItem
     * Role: DELETE: Deletes product present in cart
     */
    @DeleteMapping("/cart")
    @AuthRequired
    public ResponseEntity<String> deleteCartItem(@RequestParam String cartItemId){
        String msg = buyerImplementation.deleteCartItem(cartItemId);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: getAllOrders
     * Role: GET: Displays all orders placed by buyer
     */
    @GetMapping("/orders")
    @AuthRequired
    public ResponseEntity<OrderGetResponseDTO> getAllOrders(){
        OrderGetResponseDTO orderGetResponseDTO = buyerImplementation.getAllOrders();
        return ResponseEntity.ok(orderGetResponseDTO);
    }

    /*
     * Method: getOrder
     * Role: GET: Displays order using order id
     */
    @GetMapping("/order")
    @AuthRequired
    public ResponseEntity<Order> getOrder(@RequestParam String orderId){
        Order order = buyerImplementation.getOrder(orderId);
        return ResponseEntity.ok(order);
    }

    /*
     * Method: addOrder (only post method returning an object containing strings for any different response types)
     * Role: POST: Adds cart items to order
     */
    @PostMapping("/order")
    @AuthRequired
    public ResponseEntity<OrderAddResponseDTO> addOrder(){
        OrderAddResponseDTO orderResponse = buyerImplementation.placeOrder();

        HttpStatus status;
        if (!orderResponse.getSuccessfulOrders().isEmpty() && orderResponse.getOutOfStockItems().isEmpty() && orderResponse.getFailedOrders().isEmpty()) {
            status = HttpStatus.OK;
        } else if (!orderResponse.getSuccessfulOrders().isEmpty()) {
            status = HttpStatus.PARTIAL_CONTENT;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(orderResponse);
    }

    /*
     * Method: deleteOrder
     * Role: DELETE: Deletes the order
     */
    @DeleteMapping("/order")
    public ResponseEntity<String> deleteOrder(@RequestParam String orderId){
        String msg = buyerImplementation.deleteOrder(orderId);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: getProductByNameAndCategory
     * Role: GET: Displays products by name and category
     */
    @GetMapping("/search")
    public ResponseEntity<ProductDescriptionListResponseDTO> getProductByNameAndCategory(@RequestParam String name, @RequestParam String category){
        ProductDescriptionListResponseDTO productDescriptionListResponseDTO = buyerImplementation.getProductsByNameAndCategory(name, category);
        return ResponseEntity.ok(productDescriptionListResponseDTO);
    }

    /*
     * Method: getProductByPriceRange
     * Role: GET: Displays products by Price Range
     */
    @GetMapping("/filter")
    public ResponseEntity<ProductDescriptionListResponseDTO> getProductByPriceRange(@RequestParam double minPrice, @RequestParam double maxPrice){
        ProductDescriptionListResponseDTO productDescriptionListResponseDTO = buyerImplementation.getProductByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(productDescriptionListResponseDTO);
    }

    /*
     * Method: getProductReviews
     * Role: GET: Displays review on a product
     */
    @GetMapping("/product/review")
    public ResponseEntity<List<Review>> getProductReviews(@RequestParam String productId){
        List<Review> reviewList = buyerImplementation.getProductReviews(productId);
        return ResponseEntity.ok(reviewList);
    }

    /*
     * Method: addReview
     * Role: POST: Adds review on a product
     */
    @PostMapping("/product/review")
    @AuthRequired
    public ResponseEntity<String> addReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        String msg = buyerImplementation.addProductReviews(reviewRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: updateReview
     * Role: PUT: Updates the review added by the buyer on a product
     */
    @PutMapping("/product/review")
    @AuthRequired
    public ResponseEntity<String> updateReview(@RequestBody ReviewUpdateRequestDTO reviewUpdateRequestDTO){
        String msg = buyerImplementation.updateProductReviews(reviewUpdateRequestDTO);
        return ResponseEntity.ok(msg);

    }

    /*
     * Method: deleteReview
     * Role: DELETE: Delete the review added by the buyer on a product
     */
    @DeleteMapping("/product/review")
    @AuthRequired
    public ResponseEntity<String> deleteReview(@RequestParam String reviewId, @RequestParam String productId){
        String msg = buyerImplementation.deleteProductReviews(reviewId, productId);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: getProduct
     * Role: GET: Fetches the Product based on productId
     */
    @GetMapping("/product")
    public ResponseEntity<Product> getProduct(@RequestParam String productId){
        Product product = buyerImplementation.getProduct(productId);
        return ResponseEntity.ok(product);
    }




}