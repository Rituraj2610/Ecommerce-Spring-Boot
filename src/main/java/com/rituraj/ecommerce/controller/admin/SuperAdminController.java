package com.rituraj.ecommerce.controller.admin;

import com.rituraj.ecommerce.util.JwtUtil;
import com.rituraj.ecommerce.dto.admin.request.CreateAdminRequestDTO;
import com.rituraj.ecommerce.model.*;
import com.rituraj.ecommerce.service.admin.SuperAdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/superadmin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final JwtUtil jwtUtil;

    public SuperAdminController(SuperAdminService superAdminService, JwtUtil jwtUtil) {
        this.superAdminService = superAdminService;
        this.jwtUtil = jwtUtil;
    }

    /*
     * Method: viewDashboard
     * Role: GET: Displays the dashboard
     */
    @GetMapping("/dashboard")
    public String viewDashboard(HttpServletRequest request) {
        return "Super Admin Dashboard";
    }

    /*
     * Method: createAdmins
     * Role: POST: Creates new admins
     */
    @PostMapping("/dashboard/admins")
    public ResponseEntity<String> createAdmins(@RequestBody CreateAdminRequestDTO createAdminRequestDTO, HttpServletRequest request){
        String msg = superAdminService.createAdmins(createAdminRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: getUserAdmin
     * Role: GET: Displays all User Admin
     */
    @GetMapping("/dashboard/useradmins")
    public ResponseEntity<List<Admin>> getUserAdmin(HttpServletRequest request) {
        return superAdminService.getAllUserAdmin();
    }

    /*
     * Method: getProductAdmin
     * Role: GET: Displays all Product Admin
     */
    @GetMapping("/dashboard/productadmins")
    public ResponseEntity<List<Admin>> getProductAdmin(HttpServletRequest request) {
        return superAdminService.getAllProductAdmin();
    }

    /*
     * Method: deleteAdmins
     * Role: DELETE: Deletes the admin
     */
    @DeleteMapping("/dashboard/admins")
    public ResponseEntity<String> deleteAdmins(@RequestParam String userId){
        String msg = superAdminService.deleteUserAdmin(userId);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: getAllSellers
     * Role: GET: Displays all Sellers
     */
    @GetMapping("/dashboard/sellers")
    public ResponseEntity<List<Seller>> getAllSellers(HttpServletRequest request) {
        List<Seller> list = superAdminService.getAllSellers();
        return ResponseEntity.ok(list);
    }

    /*
     * Method: getAllProducts
     * Role: GET: Displays all Products
     */
    @GetMapping("/dashboard/products")
    public ResponseEntity<List<Product>> getAllProducts(HttpServletRequest request) {
        return ResponseEntity.ok(superAdminService.getAllProducts());
    }

    /*
     * Method: getAllSellerProducts
     * Role: GET: Displays all Products of a seller
     */
    @GetMapping("/dashboard/seller-products")
    public ResponseEntity<List<Product>> getAllSellerProducts(@RequestParam String sellerId, HttpServletRequest request) {
        return ResponseEntity.ok(superAdminService.getAllSellerProducts(sellerId));
    }

    /*
     * Method: getAllProductSeller
     * Role: GET: Displays seller of a product
     */
    @GetMapping("/dashboard/product-seller")
    public ResponseEntity<Seller> getAllProductSeller(@RequestParam String productId, HttpServletRequest request) {
        return ResponseEntity.ok(superAdminService.getSellerFromProductId(productId));
    }

    /*
     * Method: deleteSeller
     * Role: DELETE: Delete the Seller
     */
    @DeleteMapping("/dashboard/delete-seller")
    public ResponseEntity<String> deleteSeller(@RequestParam String userId, HttpServletRequest request) {
        String msg = superAdminService.deleteSeller(userId);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: deleteProduct
     * Role: DELETE: Delete the product in db
     */
    @DeleteMapping("/dashboard/delete-product")
    public ResponseEntity<String> deleteProduct(@RequestParam String productId, HttpServletRequest request) {
        String msg = superAdminService.deleteProduct(productId);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: getAllUsers
     * Role: GET: Displays all users in a dashboard
     */
    @GetMapping("/dashboard/users")
    public ResponseEntity<List<Buyer>> getAllUsers(HttpServletRequest request){
        return ResponseEntity.ok(superAdminService.getAllBuyers());
    }

    /*
     * Method: getAllUserReviews
     * Role: GET: Displays all reviews of a user
     */
    @GetMapping("/dashboard/user-reviews")
    public ResponseEntity<List<Review>> getAllUserReviews(@RequestParam String userId, HttpServletRequest request){
        return ResponseEntity.ok(superAdminService.getAllUserReviews(userId));
    }

    /*
     * Method: getUserCart
     * Role: GET: Displays cart of a user
     */
    @GetMapping("/dashboard/user-cart")
    public ResponseEntity<Cart> getUserCart(@RequestParam String buyerId, HttpServletRequest request){
        return ResponseEntity.ok(superAdminService.viewUserCart(buyerId));
    }

    /*
     * Method: deleteUser
     * Role: DELETE: Deletes particular user from db
     */
    @DeleteMapping("/dashboard/user")
    public ResponseEntity<String> deleteUser(@RequestParam String userId, HttpServletRequest request){
        return ResponseEntity.ok(superAdminService.deleteUser(userId));
    }

    /*
     * Method: getUserOrders
     * Role: GET: Displays all orders of a user
     */
    @GetMapping("/dashboard/user-orders")
    public ResponseEntity<List<Order>> getUserOrders(@RequestParam String buyerId, HttpServletRequest request){
        return ResponseEntity.ok(superAdminService.getUserOrder(buyerId));
    }
}