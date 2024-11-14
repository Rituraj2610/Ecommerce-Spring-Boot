package com.rituraj.ecommerce.controller.admin;

import com.rituraj.ecommerce.model.Buyer;
import com.rituraj.ecommerce.model.Cart;
import com.rituraj.ecommerce.model.Order;
import com.rituraj.ecommerce.model.Review;
import com.rituraj.ecommerce.service.admin.UserAdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/useradmin")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    /*
     * Method: getAllUsers
     * Role: GET: Displays all users in a dashboard
     */
    @GetMapping("/dashboard/users")
    public ResponseEntity<List<Buyer>> getAllUsers(HttpServletRequest request) {
        return ResponseEntity.ok(userAdminService.getAllBuyers());
    }

    /*
     * Method: getAllUserReviews
     * Role: GET: Displays all reviews of a user
     */
    @GetMapping("/dashboard/user-reviews")
    public ResponseEntity<List<Review>> getAllUserReviews(@RequestParam String userId, HttpServletRequest request) {
        return ResponseEntity.ok(userAdminService.getAllUserReviews(userId));
    }

    /*
     * Method: getUserCart
     * Role: GET: Displays cart of a user
     */
    @GetMapping("/dashboard/user-cart")
    public ResponseEntity<Cart> getUserCart(@RequestParam String buyerId, HttpServletRequest request) {
        return ResponseEntity.ok(userAdminService.viewUserCart(buyerId));
    }

    /*
     * Method: getUserOrders
     * Role: GET: Displays all orders of a user
     */
    @GetMapping("/dashboard/user-orders")
    public ResponseEntity<List<Order>> getUserOrders(@RequestParam String buyerId, HttpServletRequest request) {
        return ResponseEntity.ok(userAdminService.getUserOrder(buyerId));
    }

    /*
     * Method: deleteUser
     * Role: DELETE: Deletes particular user from db
     */
    @DeleteMapping("/dashboard/user")
    public ResponseEntity<String> deleteUser(@RequestParam String userId, HttpServletRequest request) {
        return ResponseEntity.ok(userAdminService.deleteUser(userId));
    }
}
