package com.rituraj.ecommerce.service.cart;

import com.rituraj.ecommerce.model.CartItem;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.repository.CartItemRepo;
import com.rituraj.ecommerce.repository.CartRepo;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private CartItemRepo cartItemRepo;

    public CartItemService(CartItemRepo cartItemRepo) {
        this.cartItemRepo = cartItemRepo;
    }

    public CartItem productToCartItem(String name, String productId, double price, int quantity, String image){
        CartItem cartItem = new CartItem();

        price = price * quantity;
        cartItem.setName(name);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(price);
        cartItem.setImage(image);
        cartItem.setSelectedForPayment(true);

        return cartItem;
    }
}
