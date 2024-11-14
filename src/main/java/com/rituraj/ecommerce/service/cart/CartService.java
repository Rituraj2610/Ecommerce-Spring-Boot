package com.rituraj.ecommerce.service.cart;

import com.mongodb.client.result.UpdateResult;
import com.rituraj.ecommerce.middleware.JwtAspect;
import com.rituraj.ecommerce.dto.cart.CartItemAddRequestDTO;
import com.rituraj.ecommerce.dto.cart.CartItemUpdateRequestDTO;
import com.rituraj.ecommerce.exception.*;
import com.rituraj.ecommerce.model.Cart;
import com.rituraj.ecommerce.model.CartItem;
import com.rituraj.ecommerce.repository.CartRepo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private CartRepo cartRepo;
    private CartItemService cartItemService;
    private MongoTemplate mongoTemplate;

    public CartService(CartRepo cartRepo, CartItemService cartItemService, MongoTemplate mongoTemplate) {
        this.cartRepo = cartRepo;
        this.cartItemService = cartItemService;
        this.mongoTemplate = mongoTemplate;
    }

    /*
     * Method: addCartId
     * Role: On cration of user assigns cart id with the id of user only
     */
    public void addCartId(String userId){
        Cart cart = new Cart(userId, new ArrayList<CartItem>(), 0);
        Optional<Cart> optionalCart = Optional.of(cartRepo.save(cart));

        if(optionalCart.isPresent()){
            System.out.println("Cart created");
            return;
        }

        throw new EntityCreationException("Error creating the cart");
    }

    /*
     * Method: addProduct
     * Role: first finds if cartitem exists or not, if yes updates it, if no then coverts the product to cartItem then adds it to cart
     */
    public String addProduct(CartItemAddRequestDTO cartItemAddRequestDTO) {

        String cartId = JwtAspect.getCurrentUserId();
        if (cartId.isEmpty() || cartId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        String productId = cartItemAddRequestDTO.getProductId();
        int quantity = cartItemAddRequestDTO.getQuantity();
        double price = cartItemAddRequestDTO.getPrice();
        String name = cartItemAddRequestDTO.getName();
        String image = cartItemAddRequestDTO.getImage();

        //checks if product we are adding is already in our cart
        CartItem cartItem = cartItemExistsOrNot(cartId, productId);

        // if yes then updates it with new + old values
        if(cartItem != null) {
            quantity += cartItem.getQuantity();
            price = quantity * price;
            Query query = new Query(Criteria.where("_id").is(cartId).and("cartItems.productId").is(productId)); // Targeting correct array field
            Update update = new Update();
            update.set("cartItems.$.quantity", quantity); // Using positional operator
            update.set("cartItems.$.price", price);
            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);
            if(updateResult.getModifiedCount() > 0) {
                updateCartPrice(cartId);
                return "Product added successfully in cart";
            }
            throw new EntityPushException("Failed to add product in cart");
        }

        // if no then creates a new crat item and then adds it
        cartItem = cartItemService.productToCartItem(name, productId, price, quantity, image);
        Query query = new Query(Criteria.where("_id").is(cartId));
        Update update = new Update();
        update.addToSet("cartItems", cartItem); // Ensure field name is "cartItems"
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);
        if(updateResult.getModifiedCount() > 0) {
            updateCartPrice(cartId);
            return "Product added successfully in cart";
        }
        throw new EntityPushException("Failed to add product in cart");
    }

    /*
     * Method: updateCartItem
     * Role: finds if item exists, if yes updates price and quantity
     */
    public String updateCartItem(CartItemUpdateRequestDTO cartItemUpdateRequestDTO) {

        String cartId = JwtAspect.getCurrentUserId();
        if (cartId.isEmpty() || cartId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }

        String productId = cartItemUpdateRequestDTO.getProductId();
        double price = cartItemUpdateRequestDTO.getPrice();
        boolean selection = cartItemUpdateRequestDTO.isSelectedForPayment();
        int quantity = cartItemUpdateRequestDTO.getQuantity();

        // checks if cart item exists or not
        CartItem cartItem = cartItemExistsOrNot(cartId, productId);

        if (cartItem != null) {
            Query query = new Query(Criteria.where("_id").is(cartId).and("cartItems.productId").is(productId));
            Update update = new Update();
            update.set("cartItems.$.quantity", quantity);
            update.set("cartItems.$.price", price);
            update.set("cartItems.$.selectedForPayment", selection);  // Add selection to the update

            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);

            if (updateResult.getModifiedCount() > 0) {
                updateCartPrice(cartId);
                return "Successfully updated item in cart";
            } else {
                throw new EntityUpdationException("Error Updating item in cart");
            }
        }


        throw new ResourceNotFoundException("Cart Item doesnt exist");
    }

    /*
     * Method: deleteCartItem
     * Role: Deletes cartItem from cart
     */
    public String deleteCartItem(String productId) {

        String cartId = JwtAspect.getCurrentUserId();
        if (cartId.isEmpty() || cartId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }


        CartItem cartItem = cartItemExistsOrNot(cartId, productId);
        if(cartItem != null) {
            Query query = new Query(Criteria.where("_id").is(cartId));

            // Create a query to ensure all fields match
            Query pullQuery = new Query();
            pullQuery.addCriteria(Criteria.where("productId").is(cartItem.getProductId()));
            pullQuery.addCriteria(Criteria.where("quantity").is(cartItem.getQuantity()));
            pullQuery.addCriteria(Criteria.where("price").is(cartItem.getPrice()));

            Update update = new Update().pull("cartItems", pullQuery.getQueryObject()); // Use the structure-matching query

            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);
            if(updateResult.getModifiedCount() > 0){
                updateCartPrice(cartId);
                return "Item deleted successfully";
            }
            throw new EntityDeletionException("Error deleting cart item");
        }
        throw new ResourceNotFoundException("Cart item not found in cart");
    }

    /*
     * Method: deleteSelectedCartItems
     * Role: delete multiple cart items
     */
    public void deleteSelectedCartItems(String cartId, List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            return;
        }
        List<String> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("_id").is(cartId));
        Update update = new Update().pull("cartItems", new Query(Criteria.where("productId").in(productIds)));

        UpdateResult result = mongoTemplate.updateFirst(query, update, Cart.class);
        if (result.getModifiedCount() > 0) {
            System.out.println("Selected items deleted successfully");
            updateCartPrice(cartId);
            return;
        }
        throw new EntityDeletionException("Error deleting cart items");
    }




//    public void deleteCartItem(String cartId, String productId) {
//        CartItem cartItem = cartItemExistsOrNot(cartId, productId);
//        if(cartItem != null){
//            Query query = new Query(Criteria.where("_id").is(cartId));
//            Update update = new Update().pull("cartItems", cartItem);
//
//            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);
//            if(updateResult.getModifiedCount()>0){
//                System.out.println("Item deleted successfully");
//            }else{
//                System.out.println("Error deleting cartItem");
//            }
//        }else{
//            System.out.println("No cart item found!");
//        }
//
//    }


    public List<CartItem> getCartItems(String cartId){
        Cart cart = cartRepo.findById(cartId).orElseThrow(()->new RuntimeException("Error fetching the cart"));

        return cart.getCartItem()
                .stream()
                .filter(CartItem::isSelectedForPayment)
                .collect(Collectors.toList());
    }

    /*
     * Method: toggleSelection
     * Role: to add item in final payment or not
     */
//    public void toggleSelection(String cartId, String productId, boolean selected) {
//        Query query = new Query(Criteria.where("_id").is(cartId).and("cartItems.productId").is(productId));
//        Update update = new Update().set("cartItems.$.selectedForPayment", selected);
//        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);
//        if(updateResult.getModifiedCount()>0){
//            updateCartPrice(cartId);
//            System.out.println("Changed selection of cart item");
//        }else{
//            System.out.println("failed to change selection of cart item");
//        }
//    }

    /*
     * Method: updateCartPrice
     * Role: Updates total proice in cart according to total cartitems
     */
    private void updateCartPrice(String cartId){
        Query query = new Query(Criteria.where("_id").is(cartId));
        Cart cart = mongoTemplate.findOne(query, Cart.class);

        double totalAmount = cart.getCartItem().stream()
                .filter(CartItem::isSelectedForPayment)
                .mapToDouble(CartItem::getPrice)
                .sum();

        query = new Query(Criteria.where("_id").is(cartId));
        Update update = new Update();
        update.set("totalAmount", totalAmount);

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Cart.class);
        if(updateResult.getModifiedCount()>0){
            System.out.println("Updated cart price successfully in CartService.updateCartPrice");
            return;
        }

        throw new EntityUpdationException("Failed to update cart price");
    }

    /*
     * Method: cartItemExistsOrNot
     * Role: checks if in the cart that the product already exists or not
     */
    private CartItem cartItemExistsOrNot(String cartId, String productId) {
        // Use $elemMatch to check within array elements
        Query query = new Query(Criteria.where("_id").is(cartId).and("cartItems.productId").is(productId));
        Cart cart = mongoTemplate.findOne(query, Cart.class);
        if(cart != null && cart.getCartItem() != null) {
            for(CartItem item : cart.getCartItem()) {
                if(item.getProductId().equals(productId)) {
                    return item;
                }
            }
        }
        return null;
    }

    // gets user cart for admin..maybe can be used in fe too
    public Cart getCart(){
        String cartId = JwtAspect.getCurrentUserId();
        if (cartId.isEmpty() || cartId ==null) {
            throw new ResourceNotFoundException("User ID not found in JWT token.");
        }
        Optional<Cart> optionalCart = Optional.ofNullable(cartRepo.findByCartId(cartId));
        if(optionalCart.isPresent()){
            return optionalCart.get();
        }
        throw new ResourceNotFoundException("Cart not found");
    }




}
