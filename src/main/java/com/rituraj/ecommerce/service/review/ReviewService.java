package com.rituraj.ecommerce.service.review;

import com.mongodb.client.result.UpdateResult;
import com.rituraj.ecommerce.middleware.JwtAspect;
import com.rituraj.ecommerce.dto.review.*;
import com.rituraj.ecommerce.exception.*;
import com.rituraj.ecommerce.model.Review;
import com.rituraj.ecommerce.repository.ReviewRepo;
import com.rituraj.ecommerce.service.product.ProductService;
import com.rituraj.ecommerce.service.user.BuyerImplementation;
import com.rituraj.ecommerce.util.IdGenerator;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private ReviewRepo reviewRepo;
    private ProductService productService;
    private MongoTemplate mongoTemplate;
    private IdGenerator idGenerator;

//    @Lazy
    private BuyerImplementation buyerImplementation;

    public ReviewService(ReviewRepo reviewRepo, @Lazy ProductService productService, MongoTemplate mongoTemplate, IdGenerator idGenerator, @Lazy BuyerImplementation buyerImplementation) {
        this.reviewRepo = reviewRepo;
        this.productService = productService;
        this.mongoTemplate = mongoTemplate;
        this.idGenerator = idGenerator;
        this.buyerImplementation = buyerImplementation;
    }

    public String addProductReview(ReviewRequestDTO reviewRequestDTO){
        String id = JwtAspect.getCurrentUserId();
        if (id.isEmpty() || id ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }
        Review review = new Review(idGenerator.generateId(), id, reviewRequestDTO.getProductId(),
                reviewRequestDTO.getRating(), reviewRequestDTO.getComment());
        Optional<Review> optionalReview = Optional.ofNullable(reviewRepo.save(review));

        if(optionalReview.isPresent()){
            productService.addReviewId(review.getProductId(), review.getId());
            buyerImplementation.addReviewId(review.getUserId(), review.getId());
            return "Review successfully added";
        }
        throw new EntityPushException("Failed to add review to product.");
    }

    public String updateProductReview(ReviewUpdateRequestDTO reviewUpdateRequestDTO ) {
        String id = JwtAspect.getCurrentUserId();
        if (id.isEmpty() || id ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        String reviewId = reviewUpdateRequestDTO.getReviewId();
        int rating = reviewUpdateRequestDTO.getRating();
        String comments = reviewUpdateRequestDTO.getComment();

        Review review =  reviewRepo.findByReviewId(reviewId);

        if(review != null){
            Query query = new Query(Criteria.where("id").is(reviewId));
            Update update = new Update();

            update.set("rating", rating);
            update.set("comment", comments);

            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Review.class);

            if(updateResult.getModifiedCount() > 0){
                return "Updated Review Successfully";
            }
            throw new EntityUpdationException("Failed to update review.");
        }
        throw new ResourceNotFoundException("Review with specified credentials not found.");
    }

    public String deleteProductReview(String reviewId, String productId) {
        String id = JwtAspect.getCurrentUserId();
        if (id.isEmpty() || id ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }
        long c = reviewRepo.deleteByReviewId(reviewId);
        if(c > 0){
            buyerImplementation.deleteReviewId(id, reviewId);
            productService.deleteReviewId(productId, reviewId);
            return "Review Deleted Successfully";
        }
//        else{
//            throw new ResourceNotFoundException("Failed to find the review with provided credentials.");
//        }

        throw new EntityDeletionException("Failed to delete the review.");
    }

    public List<Review> getReviews(List<String> reviewIdList) {

        if (reviewIdList == null || reviewIdList.isEmpty()) {
            throw new InvalidInputException("Review ID list is empty.");
        }

        Query query = new Query(Criteria.where("_id").in(reviewIdList));
        List<Review> reviewList = mongoTemplate.find(query, Review.class);
        if(!reviewList.isEmpty()){
            return reviewList;
        }
        throw new ResourceNotFoundException("Reviews not found for the product.");
    }


}
