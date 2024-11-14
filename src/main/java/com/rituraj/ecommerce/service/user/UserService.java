package com.rituraj.ecommerce.service.user;

import com.rituraj.ecommerce.dto.admin.response.LoginResponseDTO;
import com.rituraj.ecommerce.model.User;

import java.security.NoSuchAlgorithmException;

public interface UserService<T extends User> {

    public void register(T user);

    public LoginResponseDTO login(T user);

//    public String login(String email, String password, AdminRoles role) throws NoSuchAlgorithmException;
}
