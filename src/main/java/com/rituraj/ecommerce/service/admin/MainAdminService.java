package com.rituraj.ecommerce.service.admin;

import com.rituraj.ecommerce.util.JwtUtil;
import com.rituraj.ecommerce.dto.admin.RegisterDTO;
import com.rituraj.ecommerce.dto.admin.request.LoginRequestDTO;
import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.exception.ResourceNotFoundException;
import com.rituraj.ecommerce.exception.UnauthorizedException;
import com.rituraj.ecommerce.model.Admin;

import com.rituraj.ecommerce.model.Roles;
import com.rituraj.ecommerce.repository.AdminRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class MainAdminService {

    private SuperAdminService superAdminService;
    private ProductAdminService productAdminService;
    private UserAdminService userAdminService;
    private AdminRepo adminRepo;
    private JwtUtil jwtTokenProvider;
    private PasswordEncoder passwordEncoder;

    public MainAdminService(SuperAdminService superAdminService, ProductAdminService productAdminService, UserAdminService userAdminService, AdminRepo adminRepo, JwtUtil jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.superAdminService = superAdminService;
        this.productAdminService = productAdminService;
        this.userAdminService = userAdminService;
        this.adminRepo = adminRepo;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws NoSuchAlgorithmException {
        String email = loginRequestDTO.getEmail();
        String password = loginRequestDTO.getPassword();
        Roles role = loginRequestDTO.getRole();

        Admin admin;
        if (role == Roles.SUPER_ADMIN) {
            admin = superAdminService.login(email, password, role);
        } else if (role == Roles.USER_ADMIN) {
            admin = userAdminService.login(email, password, role);
        } else if (role == Roles.PRODUCT_ADMIN) {
            admin = productAdminService.login(email, password, role);
        } else {
            throw new UnauthorizedException("Invalid role provided. Please provide a valid role.");
        }

        String token = jwtTokenProvider.generateToken(email, admin.getRoles().name(), admin.getId());
        return new LoginResponseDTO(token, "Token generated");
    }

    public String initializeAdmin(RegisterDTO requestRegisterDTO){
        return superAdminService.initializeAdmin(requestRegisterDTO);
    }

    public boolean adminExistsOrNot() {
        return superAdminService.adminExistsOrNot();
    }

    public Admin getAdminById(String id) {
        return adminRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Admin with the specified credentials not found."));
    }
}
