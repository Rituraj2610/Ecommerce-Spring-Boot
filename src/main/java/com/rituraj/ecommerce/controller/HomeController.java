package com.rituraj.ecommerce.controller;

import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.dto.otp.OtpRequestDTO;
import com.rituraj.ecommerce.dto.user.UserLoginRequestDTO;
import com.rituraj.ecommerce.dto.user.UserRegisterRequestDTO;
import com.rituraj.ecommerce.model.Product;
import com.rituraj.ecommerce.service.user.DTOConversionService;
import com.rituraj.ecommerce.service.user.MainUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/")
public class HomeController {

    private DTOConversionService dtoConversionService;
    private MainUserService mainUserService;

    public HomeController(DTOConversionService dtoConversionService, MainUserService mainUserService) {
        this.dtoConversionService = dtoConversionService;
        this.mainUserService = mainUserService;
    }

    /*
     * Method: getHomePageProducts
     * Role: GET: Fetches products for home page
     */
    @GetMapping("/dashboard")
    public ResponseEntity<List<Product>> getHomePageProducts(){
        List<Product> productList = mainUserService.getHomePageProducts();
        return  ResponseEntity.ok(productList);}


    /*
     * Method: registerSeller
     * Role: POST: Registers sellers and sends otp
     */
        @PostMapping("/seller/register")
    public ResponseEntity<String> registerSeller(@RequestBody UserRegisterRequestDTO sellerRequestDTO) throws NoSuchAlgorithmException {
        String msg = dtoConversionService.registerSeller(sellerRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: verifySellerOtp
     * Role: POST: Verifies the Seller OTP
     */
    @PostMapping("/seller/otp")
    public ResponseEntity<String> verifySellerOtp(@RequestBody OtpRequestDTO otpRequestDTO) throws NoSuchAlgorithmException {
        String msg = dtoConversionService.verifySellerOtp(otpRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: loginSeller
     * Role: POST: Login of Seller
     */
    @PostMapping("/seller/login")
    public ResponseEntity<String> loginSeller(@RequestBody UserLoginRequestDTO userLoginRequestDTO, HttpServletResponse response) throws NoSuchAlgorithmException {
        LoginResponseDTO loginResponseDTO = dtoConversionService.validateSellerUser(userLoginRequestDTO);
        Cookie cookie = new Cookie("JWT_TOKEN", loginResponseDTO.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Set to true if using HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(3600); // Set expiration time as needed
        response.addCookie(cookie);
        return ResponseEntity.ok(loginResponseDTO.getToken());
    }

    /*
     * Method: registerBuyer
     * Role: POST: Registers buyer and sends otp
     */
    @PostMapping("/buyer/register")
    public ResponseEntity<String> registerBuyer(@RequestBody UserRegisterRequestDTO userRegisterRequestDTO) throws NoSuchAlgorithmException {
        String msg = dtoConversionService.registerBuyer(userRegisterRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: verifyBuyerOtp
     * Role: POST: Verifies the Buyer OTP
     */
    @PostMapping("/buyer/otp")
    public ResponseEntity<String> verifyBuyerOtp(@RequestBody OtpRequestDTO otpRequestDTO) throws NoSuchAlgorithmException {
        String msg = dtoConversionService.verifyBuyerOtp(otpRequestDTO);
        return ResponseEntity.ok(msg);
    }

    /*
     * Method: loginBuyer
     * Role: POST: Login of Buyer
     */
    @PostMapping("/buyer/login")
    public ResponseEntity<String> loginBuyer(@RequestBody UserLoginRequestDTO userLoginRequestDTO, HttpServletResponse response) throws NoSuchAlgorithmException {
        LoginResponseDTO loginResponseDTO = dtoConversionService.validateBuyerUser(userLoginRequestDTO);
        Cookie cookie = new Cookie("JWT_TOKEN", loginResponseDTO.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Set to true if using HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(3600); // Set expiration time as needed
        response.addCookie(cookie);
        return ResponseEntity.ok(loginResponseDTO.getToken());
    }
}
