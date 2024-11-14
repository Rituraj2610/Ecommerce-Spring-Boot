package com.rituraj.ecommerce.controller.admin;

import com.rituraj.ecommerce.dto.admin.RegisterDTO;
import com.rituraj.ecommerce.dto.admin.request.LoginRequestDTO;
import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.service.admin.MainAdminService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/admin")
public class MainAdminController {

    private final MainAdminService mainAdminService;
    public MainAdminController(MainAdminService mainAdminService) {
        this.mainAdminService = mainAdminService;
    }

    /*
     * Method: adminLogin
     * Role: POST: Login for all admins
     */
    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) throws NoSuchAlgorithmException {
        LoginResponseDTO loginResponseDTO = mainAdminService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO.getToken());
    }

    /*
     * Method: register
     * Role: POST: Register for all admins
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO requestRegisterDTO) throws NoSuchAlgorithmException {
        String msg = mainAdminService.initializeAdmin(requestRegisterDTO);
        return ResponseEntity.ok(msg);
    }
}