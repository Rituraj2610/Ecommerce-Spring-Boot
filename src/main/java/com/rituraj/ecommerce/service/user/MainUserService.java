package com.rituraj.ecommerce.service.user;

import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.exception.EntityAlreadyExistsException;
import com.rituraj.ecommerce.exception.InvalidInputException;
import com.rituraj.ecommerce.model.Buyer;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.model.Seller;
import com.rituraj.ecommerce.model.User;
import com.rituraj.ecommerce.repository.AdminRepo;
import com.rituraj.ecommerce.service.product.ProductService;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class MainUserService {

    private final BuyerImplementation buyerImplementation;
    private final SellerImplementation sellerImplementation;
    private final AdminRepo adminRepo;
    private final ProductService productService;

    public MainUserService(BuyerImplementation buyerImplementation, SellerImplementation sellerImplementation, AdminRepo adminRepo, ProductService productService) {
        this.buyerImplementation = buyerImplementation;
        this.sellerImplementation = sellerImplementation;
        this.adminRepo = adminRepo;
        this.productService = productService;
    }

    public <T extends User>String createUser(T user, String email) {


        boolean emailExists = adminRepo.findByEmail(email) != null ||
                buyerImplementation.findByEmail(email) != null ||
                sellerImplementation.findByEmail(email) != null;

        if (emailExists) {
            throw new EntityAlreadyExistsException("An account with this email already exists");
        }
        if(user instanceof Buyer){
            buyerImplementation.register((Buyer)user);
            return "Buyer Registered Successfully";
        }else if(user instanceof Seller){
            sellerImplementation.register((Seller)user);
            return "Seller Registered Successfully";
        }
        throw  new InvalidInputException("Invalid Argument type");
    }

    public <T extends User> LoginResponseDTO validateUser(T user) throws NoSuchAlgorithmException {
        if(user instanceof Buyer){
            return buyerImplementation.login((Buyer) user);
        }else if(user instanceof Seller){
            return sellerImplementation.login((Seller) user);
        }

        throw  new InvalidInputException("Invalid Argument type");
    }

    public List<Product> getHomePageProducts() {
        return productService.getAllProducts();
    }
}
