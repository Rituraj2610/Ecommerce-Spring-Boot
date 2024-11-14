package com.rituraj.ecommerce.service.user;

import com.mongodb.client.result.UpdateResult;
import com.rituraj.ecommerce.middleware.JwtAspect;
import com.rituraj.ecommerce.util.JwtUtil;
import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.dto.orders.request.OrderIdRequestDTO;
import com.rituraj.ecommerce.dto.orders.response.OrderSellerGetResponseDTO;
import com.rituraj.ecommerce.dto.product.request.*;
import com.rituraj.ecommerce.dto.product.response.*;
import com.rituraj.ecommerce.exception.*;
import com.rituraj.ecommerce.model.*;
import com.rituraj.ecommerce.repository.SellerRepo;
import com.rituraj.ecommerce.service.order.OrderService;
import com.rituraj.ecommerce.service.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SellerImplementation implements SellerService{
    private SellerRepo sellerRepo;
    private ProductService productService;
    private MongoTemplate mongoTemplate;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;
    private OrderService orderService;

    public SellerImplementation(SellerRepo sellerRepo, ProductService productService, MongoTemplate mongoTemplate, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, OrderService orderService) {
        this.sellerRepo = sellerRepo;
        this.productService = productService;
        this.mongoTemplate = mongoTemplate;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
    }

    @Override
    public void register(Seller user){
            if(sellerRepo.save(user) != null){
                return;
            };
        throw new EntityPushException("Failed to add buyer in db");
    }

    @Override
    public LoginResponseDTO login(Seller user) {
        // Fetch the seller from the repository based on email
        Seller temp = sellerRepo.findByEmail(user.getEmail());

        // If no seller is found, throw an exception
        if (temp == null) {
            throw new EntityNotFoundException("Seller with provided credentials not found");
        }

        // Validate the password using PasswordEncoder
        if (passwordEncoder.matches(user.getPassword(), temp.getPassword())) {
            System.out.println("Seller Logged in");

            // Generate JWT token after successful login
            String token = jwtUtil.generateToken(temp.getEmail(), temp.getRoles().name(), temp.getId());

            // Return the login response DTO with token
            return new LoginResponseDTO(token, "Token generated");
        }

        // If the password doesn't match, throw an invalid input exception
        throw new InvalidInputException("Wrong Credentials");
    }


    // add aop + exception + maybe dont throw exception here.. it is just null (ask gpt)
//    public List<Seller> getAllSellers() {
//
//    }

    public String addProduct(ProductRequestDTO productRequestDTO, MultipartFile[] images) {
       return productService.addProduct(productRequestDTO, images);

    }


    public List<ProductGetResponseDTO> getAllProduct() {
        String sellerId = JwtAspect.getCurrentUserId();
        if (sellerId.isEmpty() || sellerId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        List<Product> productList = productService.getAllSellerProducts(sellerId);
        List<ProductGetResponseDTO> productDTOList = productList.stream()
                .map(product -> new ProductGetResponseDTO(
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getCategory(),
                        product.getStock(),
                        product.getImageUrls()
                ))
                .collect(Collectors.toList());

        return productDTOList;
    }

    public List<Product> getAllProducts(String sellerId) {
        return productService.getAllSellerProducts(sellerId);
    }

    public ProductGetResponseDTO getByNameAndSellerId(String productName) {
        return productService.getByNameAndSellerId(productName);
    }

    public List<Product> getByCategoryAndSellerId(String category){
        return productService.getByCategoryAndSellerId(category);
    }

    public List<Product> getByPriceRangeAndSellerId(double min, double max) {
        return productService.getByPriceRangeAndSellerId(min, max);
    }

    public ProductDeleteResponseDTO deleteByNameAndSellerId(String productName ) {
        String sellerId = JwtAspect.getCurrentUserId();
//        System.out.println(sellerId);
        if (sellerId.isEmpty() || sellerId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

       ProductDeleteResponseDTO productDeleteResponseDTO = productService.deleteByNameAndSellerId(sellerId, productName);
       String productId = productDeleteResponseDTO.getId();


       // deleting product id from seller document
       if(productId!=null){
           Query query = new Query(Criteria.where("_id").is(sellerId));
           Update update = new Update().pull("productId", productId);

           UpdateResult result = mongoTemplate.updateFirst(query, update, Seller.class);
           if( result.getModifiedCount() > 0){
               System.out.println("Product deleted from list of seller");
               return productDeleteResponseDTO;
           }
           throw new EntityUpdationException("Failed to update seller db");
       }
        throw new ResourceNotFoundException("Failed to find product with provided credentials");
    }

    public String updateProductByNameAndSellerId(ProductUpdateRequestDTO productUpdateRequestDTO,
                                                                        MultipartFile[] images){

        return productService.updateProductByNameAndSellerId(productUpdateRequestDTO, images);

    }

    // called by productService class(Lazy bean creation of current class there)
    public void updateSellerProductList(String sellerId, String productId){
        Query q = new Query(Criteria.where("_id").is(sellerId));
        Update update = new Update();
        update.push("productId", productId);

        UpdateResult result = mongoTemplate.updateFirst(q, update, Seller.class);
        if( result.getModifiedCount() > 0){
            System.out.println("Seller Updated in SellerImplementation.updateSellerProductList");
            return;
        }
        throw new EntityUpdationException("Failed to update seller list in db.");
    }

    // below methods are called by productAdminService
    public Seller getSellerFromProductId(String productId) {
        String sellerId = productService.getSellerIdFromProductId(productId);
        Optional<Seller> s = sellerRepo.findById(sellerId);
        if(s.isPresent()){
            return s.get();
        }
            throw new ResourceNotFoundException("Failed to find seller with provided credentials");
    }

//    public String deleteSeller(String sellerId) {
//
//    }

    public Seller findByEmail(String email) {
        return sellerRepo.findByEmail(email);
    }


    public Optional<Seller> findBySellerId(String sellerId) {
        return sellerRepo.findById(sellerId);
    }


    /*
     * Method: getOrders
     * Role: Displays all orders for the seller
     */
    public List<OrderSellerGetResponseDTO> getOrders() {
        String sellerId = JwtAspect.getCurrentUserId();
        if (sellerId.isEmpty() || sellerId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        List<Order> orderList =orderService.getOrdersBySellerId(sellerId);
        List<OrderSellerGetResponseDTO> orderSellerGetResponseDTO = orderList.stream()
                .map(order -> new OrderSellerGetResponseDTO(
                        order.getOrderId(),
                        order.getQuantity(),
                        order.getTotalPrice()/order.getQuantity(),
                        order.getTotalPrice(),
                        order.getStatus(),
                        order.getOrderDateTime()
                ))
                .collect(Collectors.toList());
        return orderSellerGetResponseDTO;
    }

    /*
     * Method: getOrders
     * Role: Displays all orders for the seller
     */
    public String updateOrders(OrderIdRequestDTO orderIdRequestDTO) {
        String sellerId = JwtAspect.getCurrentUserId();
        if (sellerId.isEmpty() || sellerId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        return orderService.updateOrders(orderIdRequestDTO, sellerId);
    }

    /*
     * Method: getOrdersByStatus
     * Role: Displays all orders for the seller
     */
    public OrderSellerGetResponseDTO getOrdersByStatus(String status) {
        String sellerId = JwtAspect.getCurrentUserId();
        if (sellerId.isEmpty() || sellerId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        Order order = orderService.getOrdersByStatus(status, sellerId);

        OrderSellerGetResponseDTO orderSellerGetResponseDTO = new OrderSellerGetResponseDTO(
                order.getOrderId(),
                order.getQuantity(),
                order.getTotalPrice()/ order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getOrderDateTime()
                );

        return orderSellerGetResponseDTO;
    }
}
