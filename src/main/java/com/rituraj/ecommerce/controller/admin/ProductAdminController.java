package com.rituraj.ecommerce.controller.admin;

import com.rituraj.ecommerce.util.JwtUtil;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.model.Seller;
import com.rituraj.ecommerce.service.admin.ProductAdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/productadmin")
public class ProductAdminController {

    private final ProductAdminService productAdminService;
    private final JwtUtil jwtUtil;

    public ProductAdminController(ProductAdminService productAdminService, JwtUtil jwtUtil) {
        this.productAdminService = productAdminService;
        this.jwtUtil = jwtUtil;
    }

    /*
     * Method: getAllSellers
     * Role: GET: Displays all sellers in db
     */
    @GetMapping("/dashboard/sellers")
    public ResponseEntity<List<Seller>> getAllSellers(HttpServletRequest request) {
        List<Seller> list = productAdminService.getAllSellers();
        return ResponseEntity.ok(list);
    }

    /*
     * Method: getAllProducts
     * Role: GET: Displays all products in db
     */
    @GetMapping("/dashboard/products")
    public ResponseEntity<List<Product>> getAllProducts(HttpServletRequest request) {
        return ResponseEntity.ok(productAdminService.getAllProducts());
    }

    /*
     * Method: getAllSellerProducts
     * Role: GET: Displays all products of a seller
     */
    @GetMapping("/dashboard/seller-products")
    public ResponseEntity<List<Product>> getAllSellerProducts(@RequestParam String sellerId, HttpServletRequest request) {
        return ResponseEntity.ok(productAdminService.getAllSellerProducts(sellerId));
    }

    /*
     * Method: getAllProductSeller
     * Role: GET: Displays all seller of a particular product
     */
    @GetMapping("/dashboard/product-seller")
    public ResponseEntity<Seller> getAllProductSeller(@RequestParam String productId, HttpServletRequest request) {
        return ResponseEntity.ok(productAdminService.getSellerFromProductId(productId));
    }

    /*
     * Method: deleteSeller
     * Role: DELETE: Deletes particular seller from db
     */
    @DeleteMapping("/dashboard/delete-seller")
    public ResponseEntity<String> deleteSeller(@RequestParam String userId, HttpServletRequest request) {
        String msg = productAdminService.deleteSeller(userId);
        return ResponseEntity.ok(msg);
    }

//    @DeleteMapping("/dashboard/delete-product")
//    public ResponseEntity<String> deleteProduct(@RequestParam String productId, HttpServletRequest request) {
//        String msg = productAdminService.deleteProduct(productId);
//        return ResponseEntity.ok(msg);
//    }
}
