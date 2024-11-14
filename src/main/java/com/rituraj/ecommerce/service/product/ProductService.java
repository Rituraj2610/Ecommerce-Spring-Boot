package com.rituraj.ecommerce.service.product;

import com.mongodb.client.result.UpdateResult;
import com.rituraj.ecommerce.middleware.JwtAspect;
import com.rituraj.ecommerce.dto.product.request.ProductRequestDTO;
import com.rituraj.ecommerce.dto.product.request.ProductUpdateRequestDTO;
import com.rituraj.ecommerce.dto.product.response.*;
import com.rituraj.ecommerce.exception.*;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.model.Review;
import com.rituraj.ecommerce.model.Seller;
import com.rituraj.ecommerce.repository.ProductRepo;
import com.rituraj.ecommerce.service.cloudinary.CloudinaryService;
import com.rituraj.ecommerce.service.review.ReviewService;
import com.rituraj.ecommerce.service.user.SellerImplementation;
import com.rituraj.ecommerce.util.IdGenerator;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    // bean called by sellerImplementation but this class calls sellerImplementation bean too...so marked as lazy

    private ProductRepo productRepo;
    private MongoTemplate mongoTemplate;
    private IdGenerator idGenerator;
    private SellerImplementation sellerImplementation;
    private CloudinaryService cloudinaryService;
    private ReviewService reviewService;

    public ProductService(ProductRepo productRepo, MongoTemplate mongoTemplate, IdGenerator idGenerator, @Lazy SellerImplementation sellerImplementation, CloudinaryService cloudinaryService, @Lazy ReviewService reviewService) {
        this.productRepo = productRepo;
        this.mongoTemplate = mongoTemplate;
        this.idGenerator = idGenerator;
        this.sellerImplementation = sellerImplementation;
        this.cloudinaryService = cloudinaryService;
        this.reviewService = reviewService;
    }

// handle getByNameAndsellerId o/p when fetched and not fetched

    public String addProduct(ProductRequestDTO productRequestDTO, MultipartFile[] images) {

        String productId = idGenerator.generateId();

        String id = JwtAspect.getCurrentUserId();
        if (id.isEmpty()) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        // Upload images to Cloudinary
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                String imageUrl = cloudinaryService.uploadImage(image);
                imageUrls.add(imageUrl);
            } catch (IOException e) {
                throw new EntityPushException("Failed to uplaod image to cloudinary");
            }
        }

        Product product = new Product(
                productId,
                id,
                productRequestDTO.getName(),
                productRequestDTO.getPrice(),
                productRequestDTO.getDescription(),
                productRequestDTO.getCategory(),
                productRequestDTO.getStock(),
                new ArrayList<>(),
                imageUrls
        );
        getByNameAndSellerId(product.getName(), product.getSellerId());

       Optional<Product> optionalProduct = Optional.ofNullable(productRepo.save(product));

       if(optionalProduct.isPresent()){
           sellerImplementation.updateSellerProductList(id, productId);
          return "Product added successfully";
       }
           throw new EntityPushException("Failed to add product to database");
    }

    // Get all products using sellerId
    public List<Product> getAllSellerProducts(String sellerId) {
        Optional<Seller> seller = sellerImplementation.findBySellerId(sellerId);
        if (seller.isEmpty()) {
            throw new ResourceNotFoundException("Seller not found with ID: " + sellerId);
        }

        List<Product> products = productRepo.findBySellerId(sellerId);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for seller with ID: " + sellerId);
        }
        return products;
    }

    // Get all products in db
//    public List<Product> getAllProducts() {
//
//    }

    // CALLED BY THIS CLASS ITSELF
    // NEED A METHOD THAT RETURNS VOID... ADDING PRODUCT RETURN TYPE AS COMMENT
//    public Product getByNameAndSellerId(String name, String sellerId) {
//
//        Product product = productRepo.findByNameAndSellerId(name, sellerId);
//        Optional<Product> productOptional = Optional.ofNullable(product);
//
//        if(productOptional.isPresent()){
//            System.out.println("Fetched the product");
//            return product;
//        }
//        throw new RuntimeException("Error fetching product in ProductService.getByNameAndSellerId");
//    }

    public void getByNameAndSellerId(String name, String sellerId) {

        Product product = productRepo.findByNameAndSellerId(name, sellerId);
        Optional<Product> productOptional = Optional.ofNullable(product);

        if(!productOptional.isPresent()){
            return;
        }
        throw new EntityAlreadyExistsException("Product already exist with specified credentials");
    }



    // CALLED BY CONTROLLER
    public ProductGetResponseDTO getByNameAndSellerId(String productName) {

        String id = JwtAspect.getCurrentUserId();
        if (id.isEmpty() || id ==null) {
            throw new RuntimeException("User ID not found in JWT token.");
        }
        Query query = new Query(
                Criteria.where("sellerId").is(id)
                        .and("name").regex(".*" + productName + ".*", "i")  // "i" makes it case-insensitive
        );

        Product product = mongoTemplate.findOne(query, Product.class);
        if (product != null) {
            return new ProductGetResponseDTO(
                    product.getName(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getCategory(),
                    product.getStock(),
                    product.getImageUrls()
            );
        }
        throw new ResourceNotFoundException("Failed to find product with specified credentials.");
    }



    // request from controller
    public List<Product> getByCategoryAndSellerId(String category) {
        String sellerId = JwtAspect.getCurrentUserId();
        if (sellerId.isEmpty() || sellerId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        List<Product> p = productRepo.findByCategoryAndSellerId(category, sellerId);

        if(!p.isEmpty()) {
            return p;
        }
        throw new ResourceNotFoundException("Failed to find product with specified credentials.");
    }


    public List<Product> getByPriceRangeAndSellerId(double minPrice, double maxPrice) {
        String sellerId = JwtAspect.getCurrentUserId();
        if (sellerId.isEmpty() || sellerId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }
        List<Product> p = productRepo.findByPriceBetweenAndSellerId(minPrice, maxPrice, sellerId);

        if(!p.isEmpty()){
            return p;
        }
        throw new ResourceNotFoundException("Failed to find product with specified credentials.");
    }


    // IN FE MAKE LIKE THIS THAT DELETE IS A BUTTON AND FIRST WE NEED TO FETCH ALL
    public ProductDeleteResponseDTO deleteByNameAndSellerId(String sellerId, String name) {

        String prodId = productRepo.findByNameAndSellerId(name, sellerId).getId();
        long c = productRepo.deleteByNameAndSellerId(name, sellerId);
        System.out.println(c);
       if(c > 0){
           return new ProductDeleteResponseDTO(prodId, "Product Deleted Successfully", "");
       }
       throw new EntityDeletionException("Failed to delete product from db");
    }


    // VALUES ARE FROM REST, IF VALUE IS NULL IT WONT UPDATE
    // if i update in frontend... either all the fields will be present there or none will appear so automatically need to add all again lol
    // so run query using new name only

    public String  updateProductByNameAndSellerId(ProductUpdateRequestDTO productUpdateRequestDTO, MultipartFile[] imagesToAdd) {
        String sellerId = JwtAspect.getCurrentUserId();
        if (sellerId == null || sellerId.isEmpty()) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        String name = productUpdateRequestDTO.getName();

        String newName = productUpdateRequestDTO.getNewName();
        double newPrice = productUpdateRequestDTO.getNewPrice();
        String newCategory = productUpdateRequestDTO.getNewCategory();
        String newDescription = productUpdateRequestDTO.getNewDescription();
        int newStock = productUpdateRequestDTO.getNewStock();
        List<String> imagesToDelete = productUpdateRequestDTO.getImagesToDelete(); // List of images to delete

        // Fetch the product document
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name).and("sellerId").is(sellerId));
        Product product = mongoTemplate.findOne(query, Product.class);

        // If there are images to delete, update the imageUrls field first
        if (imagesToDelete != null && !imagesToDelete.isEmpty() && product != null) {
            deleteImagesFromProduct(product, imagesToDelete);
        }

        // Create the update object
        Update update = new Update();
        if (newName != null) update.set("name", newName);
        if (newPrice != 0) update.set("price", newPrice);
        if (newCategory != null) update.set("category", newCategory);
        if (newDescription != null) update.set("description", newDescription);
        if (newStock != 0) update.set("stock", newStock);

        // Handle image uploads (adding new images)
        if (imagesToAdd != null && imagesToAdd.length > 0) {
            List<String> newImageUrls = new ArrayList<>();
            for (MultipartFile image : imagesToAdd) {
                try {
                    String imageUrl = cloudinaryService.uploadImage(image); // Upload image to Cloudinary
                    newImageUrls.add(imageUrl);
                } catch (IOException e) {
                    throw new EntityPushException("Failed to upload image to db");
                }
            }
            // Add new image URLs to the existing ones without duplicates
            update.addToSet("imageUrls").each(newImageUrls.toArray()); // Adds new images to the existing list
        }

        // Execute the update for other product fields
        UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);
        if (result.getModifiedCount() > 0) {
            query = new Query();
            query.addCriteria(Criteria.where("name").is(newName).and("sellerId").is(sellerId));
            Product updatedProduct = mongoTemplate.findOne(query, Product.class);
            return "Updated Product Successfully";
        }

        throw new EntityUpdationException("Failed to update the product in db");
    }

    private void deleteImagesFromProduct(Product product, List<String> imagesToDelete) {
            // Remove the images to delete from the current imageUrls
            List<String> updatedImageUrls = new ArrayList<>(product.getImageUrls());
            updatedImageUrls.removeAll(imagesToDelete);
        System.out.println(updatedImageUrls);

            // Update the product with the modified imageUrls list
            Query query = new Query();
            query.addCriteria(Criteria.where("name").is(product.getName()).and("sellerId").is(product.getSellerId()));

            Update update = new Update();
            update.set("imageUrls", updatedImageUrls); // Set the updated image URLs list
            mongoTemplate.updateFirst(query, update, Product.class);

        // Deleting images from Cloudinary
        for (String imageUrl : imagesToDelete) {
            // Assuming the image URL contains the public ID in the path (e.g., cloudinary.com/your_cloud_name/image/upload/v1612342345/sample.jpg)
            String publicId = cloudinaryService.extractPublicIdFromUrl(imageUrl);
            cloudinaryService.deleteImageFromCloudinary(publicId);
        }

    }





    // before adding images
//    public ProductDescriptionResponseDTO updateProductByNameAndSellerId(ProductUpdateRequestDTO productUpdateRequestDTO, MultipartFile[] images) {
//
//        String sellerId = JwtAspect.getCurrentUserId();
//        if (sellerId.isEmpty() || sellerId ==null) {
//            throw new RuntimeException("User ID not found in JWT token.");
//        }
//
//        String name = productUpdateRequestDTO.getName();
//
//        //checks if product exists
//        getByNameAndSellerId(name, sellerId);
//
//        String newName = productUpdateRequestDTO.getNewName();
//        double newPrice = productUpdateRequestDTO.getNewPrice();
//        String newCategory = productUpdateRequestDTO.getNewCategory();
//        String newDescription = productUpdateRequestDTO.getNewDescription();
//
//        Query query = new Query();
//        query.addCriteria(Criteria.where("name").is(name).and("sellerId").is(sellerId));
//
//        Update update = new Update();
//        if (newName != null) {
//            update.set("name", newName);
//        }
//        if (newPrice != 0) {
//            update.set("price", newPrice);
//        }
//        if (newCategory != null) {
//            update.set("category", newCategory);
//        }
//        if (newDescription != null) {
//            update.set("description", newDescription);
//        }
//
//        UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);
//        if( result.getModifiedCount() > 0){
//            Query query2 = new Query();
//            query2.addCriteria(Criteria.where("name").is(newName).and("sellerId").is(sellerId));
//            Product p = mongoTemplate.findOne(query2, Product.class);
//            return new ProductDescriptionResponseDTO(p.getName(), p.getPrice(), p.getDescription(), p.getCategory());
//        }
//        throw new RuntimeException("Failed to update the product in ProductService.updateProductByNameAndSellerId");
//    }



    public String getSellerIdFromProductId(String productId) {
       Optional<Product> p = productRepo.findById(productId);
       if(p.isPresent()){
        return p.get().getSellerId();
       }else{
           throw new ResourceNotFoundException("Product with specified credentials not found");
       }

    }

    public void deleteAllSellerProducts(String sellerId) {
        long count = productRepo.deleteBySellerId(sellerId);
        if(count > 0){
            System.out.println("ALl products deleted");
        }
        throw new EntityDeletionException("Failed to delete products of seller.");
    }

//    public String deleteByProductId(String productId) {
//
//    }

    public ProductDescriptionListResponseDTO getByNameAndCategory(String name, String category) {
        // Create a query to perform a partial, case-insensitive match on name and exact match on category
        Query query = new Query(
                Criteria.where("name").regex(".*" + name + ".*", "i")  // Partial match on name, case-insensitive
                        .and("category").is(category)                  // Exact match on category
        );

        // Execute the query to find matching products
        List<Product> productList = mongoTemplate.find(query, Product.class);

        // Check if any products were found and return them, otherwise throw an exception
        if (!productList.isEmpty()) {
            return new ProductDescriptionListResponseDTO(productList);
        }

        throw new ResourceNotFoundException("Product with specified credentials not found");
    }


    public ProductDescriptionListResponseDTO getByName(String name) {
        // Create a query with a regex pattern to perform a partial, case-insensitive match on the name
        Query query = new Query(
                Criteria.where("name").regex(".*" + name + ".*", "i")  // Case-insensitive partial match
        );

        // Execute the query to find matching products
        List<Product> productList = mongoTemplate.find(query, Product.class);

        // Check if any products were found and return them, otherwise throw an exception
        if (!productList.isEmpty()) {
            return new ProductDescriptionListResponseDTO(productList);
        }

        throw new ResourceNotFoundException("Product with specified credentials not found");
    }


    public ProductDescriptionListResponseDTO getByCategory(String category) {
        List<Product> productList = productRepo.findByCategory(category);
        if(!productList.isEmpty()){
            return new ProductDescriptionListResponseDTO(productList);
        }
        throw new ResourceNotFoundException("Product with specified credentials not found");
    }

    /*
     * Method: addReviewId
     * Role: adds review id to specific product
     */
    public void addReviewId(String productId, String id) {
        Query query = new Query(Criteria.where("_id").is(productId));

        Update update = new Update();
        update.push("reviewId", id);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Product.class);
        if(updateResult.getModifiedCount() > 0){
            System.out.println("Review id added to product list");
            return;
        }
        throw new RuntimeException("Failed to add review id to product list in ProductService.addReviewId");
    }


    // request from another method if required
    public List<Product> getByCategoryAndSellerId(String category, String sellerId) {
        List<Product> p = productRepo.findByCategoryAndSellerId(category, sellerId);

        if(!p.isEmpty()){
            System.out.println("Fetched product list based on category and seller id");
            return p;
        }
        throw new ResourceNotFoundException("Failed to find product with specified credentials.");
    }

    //update  product stock in backend
    public boolean updateProductStock(String productId, int quantity) {
        // Fetch the product by ID
        Optional<Product> productOptional = productRepo.findById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            int availableStock = product.getStock();

            // Check if stock is sufficient
            if (availableStock >= quantity) {
                // Reduce stock and save product
                product.setStock(availableStock - quantity);
                productRepo.save(product);
                return true; // Stock updated successfully
            }
        }
        return false; // Insufficient stock or product not found
    }


    public int getAvailableStock(String productId) {
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isPresent()){
            return optionalProduct.get().getStock();
        }
        return 0;
    }

    public List<Review> getProductReviews(String productId) {
        Optional<Product> product = productRepo.findByProductId(productId);
        if(product.isPresent()){
            List<String> reviewList = product.get().getReviewId();
            return reviewService.getReviews(reviewList);
        }
        throw new ResourceNotFoundException("Failed to fetch product with provided credentials.");
    }

    public void deleteReviewId(String productId, String reviewId) {
            Query query = new Query(Criteria.where("_id").is(productId));

            Update update = new Update();
            update.pull("reviewId", reviewId);

            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Product.class);
            if(updateResult.getModifiedCount() > 0){
                System.out.println("Review id deleted from product list");
                return;
            }
            throw new EntityDeletionException("Failed to delete review id from product.");

    }

    public List<Product> getAllProducts() {
        List<Product> productList = productRepo.findAll();
        if(!productList.isEmpty()){
            return productList;
        }
        throw new ResourceNotFoundException("No Products found.");
    }

    /*
     * Method: getById
     * Role: Fetches the Product based on productId
     */
    public Product getById(String productId) {
        Optional<Product> optionalProduct = productRepo.findById(productId);
        if(optionalProduct.isPresent()){
            return optionalProduct.get();
        }
        throw new ResourceNotFoundException("Failed to find the product with provided credntials");
    }

    /*
     * Method: getByPriceRange
     * Role: Displays products by Price Range
     */
    public ProductDescriptionListResponseDTO getByPriceRange(double minPrice, double maxPrice) {
        List<Product> productList = productRepo.findByPriceBetween(minPrice,maxPrice);
        if(!productList.isEmpty()){
            return new ProductDescriptionListResponseDTO(productList);
        }
        throw new ResourceNotFoundException("Failed to fetch products with provided credentials.");
    }
}
