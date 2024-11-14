package com.rituraj.ecommerce.service.user;

import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.dto.otp.OtpRequestDTO;
import com.rituraj.ecommerce.dto.user.UserLoginRequestDTO;
import com.rituraj.ecommerce.dto.user.UserRegisterRequestDTO;
import com.rituraj.ecommerce.exception.InvalidInputException;
import com.rituraj.ecommerce.exception.UnauthorizedException;
import com.rituraj.ecommerce.model.Buyer;
import com.rituraj.ecommerce.model.Roles;
import com.rituraj.ecommerce.model.Seller;
import com.rituraj.ecommerce.model.TempUserRegistration;
import com.rituraj.ecommerce.service.otp.OtpService;
import com.rituraj.ecommerce.util.EmailValidator;
import com.rituraj.ecommerce.util.IdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class DTOConversionService {

    private final IdGenerator idGenerator;
    private final MainUserService mainUserService;
    private final PasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final OtpService otpService;


    public DTOConversionService(IdGenerator idGenerator, MainUserService mainUserService, PasswordEncoder passwordEncoder, EmailValidator emailValidator, OtpService otpService) {
        this.idGenerator = idGenerator;
        this.mainUserService = mainUserService;
        this.passwordEncoder = passwordEncoder;
        this.emailValidator = emailValidator;
        this.otpService = otpService;
    }

    public String registerSeller(UserRegisterRequestDTO sellerRequestDTO) {
        String email = sellerRequestDTO.getEmail();

        //email validation
        if(!emailValidator.isValidEmail(email)){
            throw new InvalidInputException("Invalid email input.");
        }

        String encodedPassword = passwordEncoder.encode(sellerRequestDTO.getPassword());

        TempUserRegistration tempUser = new TempUserRegistration(email, encodedPassword,
                sellerRequestDTO.getName(), LocalDateTime.now());

        String otp = otpService.generateOtp(email, tempUser);
        otpService.sendOtpByEmail(email, otp);

        return "OTP sent to email. Please verify to complete registration.";

    }

    public String verifySellerOtp(OtpRequestDTO otpRequestDTO){
        String email = otpRequestDTO.getEmail();
        String otp = otpRequestDTO.getOtp();

        if (!otpService.verifyOtp(email, otp)) {
            throw new UnauthorizedException("Invalid or expired OTP.");
        }

        // Retrieve temp user details after OTP verification
        TempUserRegistration tempUser = otpService.getTempUserDetails(email);


        Seller seller = new Seller(idGenerator.generateId(), tempUser.getFullName(),
                email, tempUser.getEncodedPassword(), new ArrayList<>());

        return mainUserService.createUser(seller, email);
    }

    public LoginResponseDTO validateSellerUser(UserLoginRequestDTO userLoginRequestDTO) throws NoSuchAlgorithmException {
        Seller seller = new Seller(idGenerator.generateId(), "",
                userLoginRequestDTO.getEmail(), userLoginRequestDTO.getPassword(), new ArrayList<>());


        return mainUserService.validateUser(seller);
    }

    public String registerBuyer(UserRegisterRequestDTO userRegisterRequestDTO) {
        String email = userRegisterRequestDTO.getEmail();

        //email validation
        if(!emailValidator.isValidEmail(email)){
            throw new InvalidInputException("Invalid email input.");
        }

        String encodedPassword = passwordEncoder.encode(userRegisterRequestDTO.getPassword());

        TempUserRegistration tempUser = new TempUserRegistration(email, encodedPassword,
                userRegisterRequestDTO.getName(), LocalDateTime.now());

        String otp = otpService.generateOtp(email, tempUser);
        otpService.sendOtpByEmail(email, otp);

        return "OTP sent to email. Please verify to complete registration.";
    }

    public String verifyBuyerOtp(OtpRequestDTO otpRequestDTO) throws NoSuchAlgorithmException {
        String email = otpRequestDTO.getEmail();
        String otp = otpRequestDTO.getOtp();

        if (!otpService.verifyOtp(email, otp)) {
            throw new UnauthorizedException("Invalid or expired OTP.");
        }

        // Retrieve temp user details after OTP verification
        TempUserRegistration tempUser = otpService.getTempUserDetails(email);

        Buyer buyer = new Buyer(idGenerator.generateId(), tempUser.getFullName(),
                email, tempUser.getEncodedPassword(), new ArrayList<>());

        return mainUserService.createUser(buyer, email);
    }

    public LoginResponseDTO validateBuyerUser(UserLoginRequestDTO userLoginRequestDTO) throws NoSuchAlgorithmException {
        Buyer buyer = new Buyer(idGenerator.generateId(), "",
                userLoginRequestDTO.getEmail(), userLoginRequestDTO.getPassword(), new ArrayList<>());


        return mainUserService.validateUser(buyer);
    }
}
